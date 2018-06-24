package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

public interface Input {
	public String getInputId();
	public String getInputJobId();
	public String getInputDescription();
	public long getInputCount();
	public void setInputCount(long count);
	public Calendar getStartTime();
	public void setStartTime(Calendar time);
	public String getEntityType();
	
	public boolean isValid();
	public void setValid(boolean flag);
	public long getTimeTakenMillis();
	public void setTimeTakenMillis(long timeTakenMillis);
	public double getBadInputPct();
	public void setBadInputPct(double badInputPct);
	public Object clone() throws CloneNotSupportedException;
}
