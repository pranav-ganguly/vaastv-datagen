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
	public Object clone() throws CloneNotSupportedException;
}
