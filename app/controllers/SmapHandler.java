package controllers;

import java.util.Date;

import org.apache.log4j.Logger;

public class SmapHandler {

	public static final Logger LOG = Logger.getLogger(SmapHandler.class.getName());
	
	private static String data = null;
	private static Date time = null;
	private static long counter = 0;
	
	public static void doProcess(String body) {
		data = body;
		counter++;
		time = new Date();
		LOG.info(body);
	}

	public static String doGet() {
		return time.toLocaleString() + "\nCounter : " + counter + "\n\n" + data; 
	}

}
