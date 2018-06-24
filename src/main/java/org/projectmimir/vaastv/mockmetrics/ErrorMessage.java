package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

public class ErrorMessage {
	
	public ErrorMessage(String message, Calendar timestamp, String entityId) {
		super();
		this.message = message;
		this.timestamp = timestamp;
		this.entityId = entityId;
	}

	private String message;
	private Calendar timestamp;
	private String entityId;
	
	
	
	public String getEntityType() {
		return "ERROR";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Calendar getStartTime() {
		return timestamp;
	}

	public void setStartTime(Calendar timestamp) {
		this.timestamp = timestamp;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
}
