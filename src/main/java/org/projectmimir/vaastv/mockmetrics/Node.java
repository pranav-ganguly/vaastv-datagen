package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

public class Node {
	
	public String getEntityType() {
		return "NODE";
	}
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public Calendar getStartTime() {
		return snapshotTimestamp;
	}
	public void setStartTime(Calendar snapshotTimestamp) {
		this.snapshotTimestamp = snapshotTimestamp;
	}
	public double getCpu() {
		return cpu;
	}
	public void setCpu(double cpu) {
		this.cpu = cpu;
	}
	public double getMem() {
		return mem;
	}
	public void setMem(double mem) {
		this.mem = mem;
	}
	
	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	private String hostname;
	private Calendar snapshotTimestamp;
	private double cpu;
	private double mem;
	private boolean available;
}
