package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

public interface Output {
	public String getOutputId();
	public String getOutputJobId();
	public String getOutputDescription();
	public long getOutputCount();
	public void setOutputCount(long count);
	public Object clone() throws CloneNotSupportedException;
	public String getEntityType();
	public Calendar getStartTime();
	public void setStartTime(Calendar time);
	
}
