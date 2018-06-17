package org.projectmimir.vaastv.mockmetrics;

import java.util.Calendar;

public class Utils {
	
	public static Calendar getRandomTimeOfDay(Calendar date) {
		Calendar date2 = (Calendar)date.clone();
		date2.set(Calendar.HOUR_OF_DAY, 0);
		date2.set(Calendar.MINUTE, 0);
		date2.set(Calendar.SECOND, 0);
		date2.set(Calendar.MILLISECOND, 0);
		long offset = (long)Math.round(Math.random()*24*60*60000);
		date2.setTimeInMillis(date2.getTimeInMillis()+offset);
		return date2;
	}

}
