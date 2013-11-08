package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;

import play.jobs.Job;

public class Downloader extends Job {
	
	public static final Logger LOG = Logger.getLogger(SmapHandler.class.getName());
	
	private static String data = "";
	private static Date time = new Date();;
	private static long counter = 0;
	
	public static boolean isRunning = false;
	
	public static String getStatus() {
		return time.toLocaleString() + "\nCounter : " + counter + "\n\n" + data; 
	}

	public void doJob() {		
		System.out.println("..inside doJob");
		isRunning = true;		

		while (isRunning) {			
			try {				
				System.out.println("requestion...");
				URL url = new URL("http://192.168.1.40:9101/republish");
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
						
				InputStream in = url.openStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String line = reader.readLine();
				while((line=reader.readLine())!=null){
					data = line;
					counter++;
					time = new Date();
					LOG.info(line);
				}			
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
