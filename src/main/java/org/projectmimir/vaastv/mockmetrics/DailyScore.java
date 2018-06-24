package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

public class DailyScore {
	
	private double quality;
	private double dtQ;
	private double whQ;
	private double svcQ;
	private Calendar startTime;
	public double getQuality() {
		return quality;
	}
	public void setQuality(double quality) {
		this.quality = quality;
	}
	public double getDtQ() {
		return dtQ;
	}
	public void setDtQ(double dtQ) {
		this.dtQ = dtQ;
	}
	public double getWhQ() {
		return whQ;
	}
	public void setWhQ(double whQ) {
		this.whQ = whQ;
	}
	public double getSvcQ() {
		return svcQ;
	}
	public void setSvcQ(double svcQ) {
		this.svcQ = svcQ;
	}
	public Calendar getStartTime() {
		return startTime;
	}
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

}
