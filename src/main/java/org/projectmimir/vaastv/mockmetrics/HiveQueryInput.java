package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

public class HiveQueryInput implements Input {
	private final int id;
	private final String jobId;
	private final String query;
	private long resultCount;
	private boolean valid;
	private Calendar startTime;
	private long timeTakenMillis;
	private double badInputPct;
	
	

	public HiveQueryInput(String jobId, String query, long resultCount, long timeTakenMillis, boolean valid) {
		super();
		this.jobId = jobId;
		this.query = query;
		this.id = query.hashCode();
		this.resultCount = resultCount;
		this.valid = valid;
		this.timeTakenMillis = timeTakenMillis;
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
		return new HiveQueryInput(this.jobId,this.query, this.resultCount, this.timeTakenMillis, this.valid);
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

	public long getTimeTakenMillis() {
		return timeTakenMillis;
	}

	public void setTimeTakenMillis(long timeTakenMillis) {
		this.timeTakenMillis = timeTakenMillis;
	}

	public double getBadInputPct() {
		return badInputPct;
	}

	public void setBadInputPct(double badInputPct) {
		this.badInputPct = badInputPct;
	}
	
	

}
