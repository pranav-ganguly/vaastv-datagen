package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

public class HiveQueryInput implements Input {
	private final int id;
	private final String jobId;
	private final String query;
	private long resultCount;
	private boolean valid;
	private Calendar startTime;
	
	

	public HiveQueryInput(String jobId, String query, long resultCount, boolean valid) {
		super();
		this.jobId = jobId;
		this.query = query;
		this.id = query.hashCode();
		this.resultCount = resultCount;
		this.valid = valid;
	}

	public String getInputId() {
		// TODO Auto-generated method stub
		return String.valueOf(this.id);
	}

	public String getInputDescription() {
		// TODO Auto-generated method stub
		return this.query;
	}

	public long getInputCount() {
		// TODO Auto-generated method stub
		return this.resultCount;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new HiveQueryInput(this.jobId,this.query, this.resultCount, this.valid);
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return this.valid;
	}

	@Override
	public String getInputJobId() {
		// TODO Auto-generated method stub
		return this.jobId;
	}

	@Override
	public String getEntityType() {
		// TODO Auto-generated method stub
		return "INPUT";
	}

	@Override
	public void setInputCount(long count) {
		this.resultCount = count;
	}

	@Override
	public void setValid(boolean flag) {
		// TODO Auto-generated method stub
		this.valid = flag;
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
