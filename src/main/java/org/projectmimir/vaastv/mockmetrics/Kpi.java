package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Kpi {
	private static final String entityType = "KPI";
	private int staleness;
	private String name;
	private String group;
	private double baseValue;
	private double value;
	private int magnitude;
	private Calendar timestamp;
	public int getStaleness() {
		return staleness;
	}
	public void setStaleness(int staleness) {
		this.staleness = staleness;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double d) {
		this.value = d;
	}
	@JsonProperty("startTime")
	public Calendar getTimestamp() {
		return timestamp;
	}
	@JsonProperty("startTime")
	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getEntityType() {
		return Kpi.entityType;
	}
	
	public int getMagnitude() {
	/*	String[] parts = String.valueOf(this.value).split("\\.");
		if(this.value > 1 ) {
			return parts[0].length();
		}else if(this.value > 0){
			return -1*parts[1].length();
		} else {
			return 1;
		}*/
		return this.magnitude;
	}
	
	public void setMagnitude(int magnitude) {
		this.magnitude = magnitude;
	}
	@JsonIgnore
	public double getBaseValue() {
		return baseValue;
	}
	public void setBaseValue(double baseValue) {
		this.baseValue = baseValue;
	}

}
