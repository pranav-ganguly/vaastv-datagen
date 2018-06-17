package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Job {
	private String jobId;
	private int[] dependencies;
	
	private String name;
	private String status;
	private int timeInMins;
	private Input[] inputs;
	private int[] inputRejectionCount;
	private String[] errors;
	private Output[] outputs;
	private Calendar startTime;
	private Calendar endTime;
	private double _magnitude;
	private boolean validOutputs;
	
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTimeInMins() {
		return timeInMins;
	}
	public void setTimeInMins(int timeInMins) {
		this.timeInMins = timeInMins;
	}
	
	
	public Input[] getInputs() {
		return inputs;
	}
	public void setInputs(Input[] inputs) {
		this.inputs = inputs;
	}
	public int[] getInputRejectionCount() {
		return inputRejectionCount;
	}
	public void setInputRejectionCount(int[] inputRejectionCount) {
		this.inputRejectionCount = inputRejectionCount;
	}
	public String[] getErrors() {
		return errors;
	}
	public void setErrors(String[] errors) {
		this.errors = errors;
	}
	
	
	public Output[] getOutputs() {
		return outputs;
	}
	public void setOutputs(Output[] outputs) {
		this.outputs = outputs;
	}
	public Calendar getStartTime() {
		return startTime;
	}
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}
	public Calendar getEndTime() {
		return endTime;
	}
	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}
	
	@JsonIgnore
	public double get_magnitude() {
		return _magnitude;
	}
	public void set_magnitude(double _magnitude) {
		this._magnitude = _magnitude;
	}
	
	
	public boolean isValidInputs() {
		for(Input i : this.inputs) {
			if(!i.isValid()) return false;
		}
		return true;
	}
	
	@JsonIgnore
	public int[] getDependencies() {
		return dependencies;
	}
	public void setDependencies(int[] dependencies) {
		this.dependencies = dependencies;
	}
	
	public String getEntityType() {
		return "JOB";
	}
	
	@JsonIgnore
	public boolean isValidOutputs() {
		return validOutputs;
	}
	public void setValidOutputs(boolean outputValid) {
		this.validOutputs = outputValid;
	}

	public Object clone() throws CloneNotSupportedException{
		Job j = new Job();
		j.jobId = this.jobId;
		j._magnitude = this._magnitude;
		j.name = this.name;
		j.status = this.status;
		j.timeInMins = this.timeInMins;
		j.startTime = this.startTime;
		j.endTime = this.endTime;
		
		j.dependencies = new int[this.dependencies.length];
		for(int i=0;i<this.dependencies.length;i++) {
			j.dependencies[i]=this.dependencies[i];
		}
		
		j.inputs = new Input[this.inputs.length];
		for(int i =0;i<this.inputs.length;i++) {
			j.inputs[i] = (Input)this.inputs[i].clone();
		}
		
		j.inputRejectionCount = new int[this.inputRejectionCount.length];
		for(int i=0;i<this.inputRejectionCount.length;i++) {
			j.inputRejectionCount[i] = this.inputRejectionCount[i];
		}
		
		j.outputs = new Output[this.outputs.length];
		for(int i =0;i<this.outputs.length;i++) {
			j.outputs[i] = (Output)this.outputs[i].clone();
		}
		return j;
		
	}
	
}
