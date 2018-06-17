package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

public class HiveTableOutput implements Output {

	private final String jobId;
	private final String tableId;
	private final String tableURN;
	private  long resultCount;
	private Calendar startTime;

	public HiveTableOutput(String jobId,String tableId, String tableURN, long l) {
		super();
		this.jobId = jobId;
		this.tableURN = tableURN;
		this.tableId = String.valueOf(tableURN.hashCode());
		this.resultCount = l;
	}

	public String getOutputId() {
		// TODO Auto-generated method stub
		return this.tableId;
	}

	public String getOutputDescription() {
		// TODO Auto-generated method stub
		return this.tableURN;
	}

	public long getOutputCount() {
		// TODO Auto-generated method stub
		return this.resultCount;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new HiveTableOutput(this.jobId,this.tableId, this.tableURN, this.resultCount);
	}

	@Override
	public String getOutputJobId() {
		// TODO Auto-generated method stub
		return this.jobId;
	}

	@Override
	public String getEntityType() {
		// TODO Auto-generated method stub
		return "OUTPUT";
	}

	@Override
	public void setOutputCount(long count) {
		this.resultCount = count;
		
	}

	@Override
	public Calendar getStartTime() {
		// TODO Auto-generated method stub
		return this.startTime;
	}

	@Override
	public void setStartTime(Calendar time) {
		// TODO Auto-generated method stub
		this.startTime = time;
	}

}
