package org.projectmimir.vaastv.mockmetrics;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Generator {
	
	private static final Logger log = LogManager.getLogger(Generator.class);
	private static final DateFormat dtf = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.LONG);
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			//DateFormat.getDateInstance(DateFormat.SHORT);
	private TransportClient es_client;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		log.debug("Starting data generation");
		Generator generator =  new Generator();
		generator.initialize();
		generator.generateData();
		generator.cleanup();
	}

	private void initialize() {
		log.debug("Enter Initialize()");
		Random rJobMagn = new Random();
		Random rGaussD1 = new Random();
		long startInputVolume = (long)Math.round(this.inputVolume/this.inputGrowthFactor);
		log.debug("inputVolume = {}, inputGrowthFactor = {}, startInputVolume = {}",this.inputVolume, this.inputGrowthFactor, startInputVolume);
		long inVolAvg = startInputVolume/jobCount;
		int avgRuntime = this.jobWindowInMins*this.jobAvgParallelism/this.jobCount;
		log.debug("jobCount = {}, inVolAvg = {}, avgRuntime = {}",this.jobCount, inVolAvg, avgRuntime);
		this.baseJobs = new Job[this.jobCount];
		
		for(int i=0;i<this.jobCount;i++) {
			Job job = new Job();
			job.setJobId("Job#"+i);
			job.setName("Job#"+i);
			log.debug("{}. Generating Job details for {}",i,job.getName());
			
			/**
			 * Select a magnitude for the job which determines how large it is.
			 * A value between 0 and 2 with a standard deviation of 0.2 and mean of 1
			 * Governs all other aspects of the job footprint such as I/O, Runtime etc
			 */
			double magn = 0;
			do{
				magn = rJobMagn.nextGaussian()*0.2;
			}while(magn>=1||magn<=-1);
			magn+=1;
			job.set_magnitude(magn);
			log.debug("{} _magnitude = {}",job.getName(),magn);
			/**
			 * Set total input Volume for Job based on magnitude
			 */
			long jobInVol = (long)Math.round(inVolAvg*job.get_magnitude());
			log.debug("{} jobInVol = {}",job.getName(),jobInVol);
			/**
			 * Set up Job Inputs.
			 */
			 // Select a random number of inputs between 1 and 2*inputsAvg.
			int inCnt = 0;
			do {
				inCnt = (int)(Math.round(rGaussD1.nextGaussian())+this.inputsAvg);
			}while(inCnt<1||inCnt>this.inputsAvg*2);
			
			log.debug("{} inCnt = {}",job.getName(),inCnt);
			
			Input[] jobInputs = new Input[inCnt];
			
			// Select in what ratios the total input volume be divided among the inputs
			int[] inRatios = new int[inCnt];
			int inRatioTotal = 0;
			for(int j=0;j<inCnt;j++) {
				inRatios[j] = (int)Math.ceil(Math.random()*100);
				inRatioTotal += inRatios[j];
			}
			
			log.debug("{} inRatios = {}",job.getName(),inRatios);
			
			//Setup inputs with +-0.125% size as per ratios.
			for(int j=0;j<inCnt;j++) {
				jobInputs[j] = new HiveQueryInput(job.getJobId(),"Job#"+i+"Query#"+j, 
						(long)((inRatios[j]*jobInVol/inRatioTotal)*
						(1+(0.5-Math.random())*0.25)),
						(Math.random()<this.inputsValidness) //probability of bad input
					);
				log.debug("{}, input {} id = {}, count = {}, valid ={}",
						job.getName(),j,jobInputs[j].getInputId(), 
						jobInputs[j].getInputCount(), jobInputs[j].isValid());
			}
			job.setInputs(jobInputs);
			
			/**
			 * Set up job runtime based on magnitude. 5 if any inputs are invalid
			 */
			int jobRuntime = (int)(avgRuntime*job.get_magnitude());
			job.setTimeInMins(job.isValidInputs()?jobRuntime>5?jobRuntime:
				(int)(5*Math.random()*magn):(int)(5*Math.random()*magn));
			Calendar jobStartTime = Utils.getRandomTimeOfDay(this.startDate);
			Calendar jobEndTime = (Calendar)jobStartTime.clone();
			jobEndTime.add(Calendar.MINUTE,jobRuntime);
			job.setStartTime(jobStartTime);
			job.setEndTime(jobEndTime);
			log.debug("{}, jobRuntime = {}, startTime = {}, endTime ={}",
					job.getName(),jobRuntime,
					Generator.dtf.format(jobStartTime.getTime()), 
					Generator.dtf.format(jobEndTime.getTime()));
			
			/**
			 * Set job status
			 */
			//A Job can fail in three ways 
			//fail (4* input failure chance)*magn*(prevFail?2:1), 
			//abend (0.1 * failure chance) 
			//hang (0.1 * abend chance)
			//Select whether the job was successful. 
			if(job.isValidInputs()) {
				double failure_prob = (1-this.jobStability)*job.get_magnitude();
				log.debug("{}, failure_prob = {}", job.getName(),failure_prob);
				double dice = Math.random();
				if(dice<failure_prob) {
					if(dice<failure_prob*0.1) {
						if(dice<failure_prob*0.01) {
							job.setStatus("ABORTED");
						}else {
							job.setStatus("ABORTED");
						}
					}else {
						job.setStatus("FAILED");
					}
				}else {
					job.setStatus("SUCCESSFUL");
				}
			}else {
				job.setStatus("FAILED");
			}
			log.debug("{}, status = {}", job.getName(),job.getStatus());
			/**
			 * Prepare outputs
			 */
			//Unless failed due to bad inputs, a job may have outputs
			//A successful job will always have outputs
			//A failed job has a 25% probability of generating outputs
			job.setValidOutputs(job.isValidInputs()&&(
					"successful".equalsIgnoreCase(job.getStatus())
					|| (Math.random()<0.25)
				));
			long jobOutVol = (long)Math.round(jobInVol*this.ioVolRatio*Math.random()*2);
			log.debug("{}, jobOutVol = {}", job.getName(),jobOutVol);
			/**
			 * Set up Job Inputs.
			 */
			 // Select a random number of inputs between 1 and 2*inputsAvg.
			int outCnt = 0;
			do {
				outCnt = (int)(Math.round(rGaussD1.nextGaussian())+this.outputsAvg);
			}while(outCnt<1||outCnt>this.outputsAvg*2);
			log.debug("{}, outCnt = {}", job.getName(),outCnt);
			
			Output[] jobOutputs = new Output[outCnt];
			
			// Select in what ratios the total input volume be divided among the inputs
			int[] outRatios = new int[outCnt];
			int outRatioTotal = 0;
			for(int j=0;j<outCnt;j++) {
				outRatios[j] = (int)Math.ceil(Math.random()*100);
				outRatioTotal += outRatios[j];
			}
			
			log.debug("{}, outRatios = {}", job.getName(),outRatios);
			
			//Setup inputs with +-0.125% size as per ratios.
			for(int j=0;j<outCnt;j++) {
				jobOutputs[j] = new HiveTableOutput(job.getJobId(),"Job#"+i+"Table#"+j,
						"Job #"+i+" Table #"+j,
						(long)((outRatios[j]*jobOutVol/outRatioTotal)*
						(1+(0.5-Math.random())*0.25))
					);
				log.debug("{}, output {} id = {}, count = {}",
						job.getName(),j,jobOutputs[j].getOutputId(), 
						jobOutputs[j].getOutputCount());
			}
			job.setOutputs(jobOutputs);
			
			this.baseJobs[i]=job;
		}
		
		try {
			this.es_client  = new PreBuiltTransportClient(Settings.EMPTY)
			        .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void generateData() {
		Calendar date = this.startDate;
		int cycle = 0;
		double incrFactor = (this.inputGrowthFactor-1)/this.jobCycles;
		do {
			pushToES(date);
			cycle++;
			date.add(Calendar.DATE, 1);
			for(Job job : this.baseJobs) {
				double magn = job.get_magnitude();
				for(Input in:job.getInputs()) {
					long inSz = in.getInputCount();
					inSz *= (0.5*Math.random()+0.75)*(incrFactor*magn/2);
					in.setInputCount(inSz);
					in.setValid(Math.random()<(this.inputsValidness*(1+(in.isValid()?0.1:0))));
				}
				for(Output op:job.getOutputs()) {
					long opSz = op.getOutputCount();
					opSz *= (0.5*Math.random()+0.75)*(incrFactor*magn/2);
					op.setOutputCount(opSz);
				}
				
				int jobRuntime = (int)(job.getTimeInMins()*(1+incrFactor)*(1.125-0.125*Math.random()));
				job.setTimeInMins(job.isValidInputs()?jobRuntime:5);
				job.getStartTime().add(Calendar.DATE, 1);
				job.getStartTime().add(Calendar.MINUTE,(int)Math.round(20*Math.random()-10));
				job.setEndTime((Calendar)job.getStartTime().clone());
				job.getEndTime().add(Calendar.MINUTE,jobRuntime);
				
				if(job.isValidInputs()) {
					double failure_prob = (1-this.jobStability)*job.get_magnitude()*("SUCCESSFUL".equalsIgnoreCase(job.getStatus())?1:2);
					log.trace("{}, failure_prob = {}", job.getName(),failure_prob);
					double dice = Math.random();
					if(dice<failure_prob) {
						if(dice<failure_prob*0.2) {
							if(dice<failure_prob*0.01) {
								job.setStatus("ABORTED");
							}else {
								job.setStatus("ABORTED");
							}
						}else {
							job.setStatus("FAILED");
						}
					}else {
						job.setStatus("SUCCESSFUL");
					}
				}else {
					job.setStatus("FAILED");
				}
				job.setValidOutputs(job.isValidInputs()&&(
						"successful".equalsIgnoreCase(job.getStatus())
						|| (Math.random()<0.25)
					));
				
			}
		}while(cycle<this.jobCycles);
	}
	
	
	private void pushToES(Calendar date) {
		IndexResponse res = null;
		String indexName = "mockmetrics."+Generator.df.format(date!=null?date.getTime():this.startDate.getTime());
		ObjectMapper om = new ObjectMapper();
		BulkRequestBuilder bulkReq = this.es_client.prepareBulk();
		for(int i=0;i<this.baseJobs.length;i++) {
			Job job = this.baseJobs[i];
			String json = null;
			try {
				for(Input in : job.getInputs()) {
					in.setStartTime(job.getStartTime());
					json = om.writeValueAsString(in);
					bulkReq.add(this.es_client.prepareIndex(indexName, "entity").setSource(json, XContentType.JSON));
					//log.debug("Sent To ES Index {} : {}",indexName, in.getInputId());
					
				}
				if(job.isValidOutputs()) {
					for(Output out: job.getOutputs()) {
						out.setStartTime(job.getStartTime());
						json = om.writeValueAsString(out);
						bulkReq.add(this.es_client.prepareIndex(indexName, "entity").setSource(json, XContentType.JSON));
						//res = this.es_client.prepareIndex(indexName,"entity").setSource(json, XContentType.JSON).get();
						//log.debug("Sent To ES Index {} : {}",indexName, out.getOutputId());
					}
				}
				json = om.writeValueAsString(this.baseJobs[i]);
				bulkReq.add(this.es_client.prepareIndex(indexName, "entity").setSource(json, XContentType.JSON));
				//res = this.es_client.prepareIndex(indexName,"entity").setSource(json, XContentType.JSON).get();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		BulkResponse bulkRes = bulkReq.get();
		log.debug("Index {} BulkRequest completed with status {} for {} item in {} mS", 
				indexName, bulkRes.status(), bulkRes.getItems().length, bulkRes.getIngestTookInMillis());
		
	}
	
	private void cleanup() {
		this.es_client.close();
		
	}



	private Generator() {
		super();
		// TODO Auto-generated constructor stub
		this.ioNetSize = this.DEFAULT_IO_NETSIZE;
		this.inputVolume = this.DEFAULT_INPUT_VOLUME;
		this.inputVolumeVariance = this.DEFAULT_INPUT_VOLUME_VARIANCE;
		this.inputGrowthType = this.DEFAULT_INPUT_GROWTH_TYPE;
		this.inputGrowthFactor = this.DEFAULT_INPUT_GROWTH_FACTOR;
		this.inputsAvg = this.DEFAULT_INPUTS_AVG;
		this.inputsVariance = this.DEFAULT_INPUTS_VARIANCE;
		this.inputsSharedness = this.DEFAULT_INPUTS_SHAREDNESS;
		this.inputsValidness = this.DEFAULT_INPUTS_VALIDNESS;
		this.ioVolRatio = this.DEFAULT_INPUT_OUTPUT_VOL_RATIO;
		this.outputsAvg = this.DEFAULT_OUTPUTS_AVG;
		this.outputsVariance = this.DEFAULT_OUTPUTS_VARIANCE;
		this.outputsSharedness = this.DEFAULT_OUTPUTS_SHAREDNESS;
		this.jobWindowInMins = this.DEFAULT_JOB_WINDOW_MINS;
		this.jobAvgParallelism = this.DEFAULT_JOB_PAR_AVG;
		this.jobCount = this.DEFAULT_JOB_COUNT;
		this.jobCycles = this.DEFAULT_JOB_CYCLES;
		this.jobStability = this.DEFAULT_JOB_STABILITY;
		this.startDate = this.DEFAULT_START_DATE;
		this.rwRatio = this.DEFAULT_READ_WRITE_RATIO;
		this.ioTimeRatio = this.DEFAULT_IO_TIME_RATIO;
		
	}

	
	
	
	
	
	private Job[] baseJobs;
	private List<Input> baseInputs;
	private List<Output> baseOutputs;

	private long ioNetSize;
	private long inputVolume;
	private double inputVolumeVariance;
	private int inputGrowthType;
	private double inputGrowthFactor;
	
	private int inputsAvg;
	private double inputsVariance;
	private double inputsSharedness;
	private double inputsValidness;
	
	private double ioVolRatio;
	
	private int outputsAvg;
	private double outputsVariance;
	private double outputsSharedness;
	
	private int jobWindowInMins;
	private int jobCount;
	private int jobCycles;
	private int jobAvgParallelism;
	private double jobStability;
	private Calendar startDate;
	private double rwRatio;
	private double ioTimeRatio;
	
	private final long DEFAULT_INPUT_VOLUME = (int)Math.pow(10, 9);
	private final double DEFAULT_INPUT_VOLUME_VARIANCE = 0.2;
	private final double DEFAULT_INPUT_GROWTH_FACTOR = 5; 
	private final int DEFAULT_INPUT_GROWTH_TYPE = Generator.GROWTH_TYPE_LINEAR;
	
	private final int DEFAULT_INPUTS_AVG = 3;
	private final long DEFAULT_IO_NETSIZE = (int)(20*Math.pow(10, 12));
	private final double DEFAULT_INPUTS_VARIANCE = 0.5;
	private final double DEFAULT_INPUTS_SHAREDNESS = 0.5; 
	private final double DEFAULT_INPUTS_VALIDNESS = 0.85;
	
	private final double DEFAULT_INPUT_OUTPUT_VOL_RATIO = 0.2;
	
	private final int DEFAULT_OUTPUTS_AVG = 3;
	private final double DEFAULT_OUTPUTS_VARIANCE = 0.5;
	private final double DEFAULT_OUTPUTS_SHAREDNESS = 0.2;
	
	
	private final int DEFAULT_JOB_WINDOW_MINS = 840;
	private final int DEFAULT_JOB_COUNT = 500;
	private final int DEFAULT_JOB_PAR_AVG = 100;
	private final int DEFAULT_JOB_CYCLES = 90;
	private final double DEFAULT_JOB_STABILITY = 0.9;
	private final Calendar DEFAULT_START_DATE = new GregorianCalendar(2018, 0, 1);
	
	private final double DEFAULT_READ_WRITE_RATIO = 0.1;
	private double DEFAULT_IO_TIME_RATIO = 0.4;
	
	private final static int GROWTH_TYPE_LINEAR = 21;
	private final static int GROWTH_TYPE_EXPONENTIAL = 23;
	private final static int GROWTH_TYPE_LOGARITHMIC = 25;
	
	
	
	
	
}
