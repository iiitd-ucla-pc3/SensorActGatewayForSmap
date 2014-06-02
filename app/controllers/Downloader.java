package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;

import com.sun.swing.internal.plaf.synth.resources.synth;

import play.jobs.Job;

public class Downloader extends Job {

	public static final Logger LOG = Logger.getLogger(SmapHandler.class
			.getName());

	private static String data = null;
	private static Date start_time = new Date();;
	private static Date cur_time = new Date();;
	private static long counter = 0;

	private static final String smap_republish_url = "http://192.168.1.38:9106/republish";	
	//private static final String smap_republish_url = "http://localhost:9011";

	private static boolean isRunning = false;

	public static String getStatusMessage() {
		return "Started " + start_time.toLocaleString() + 
				"\nCurrent " + cur_time.toLocaleString() + 
				"\nCounter : " + counter +
				"\nisRunning : " + isRunning +
				"\n\n" + data;
	}

	public static synchronized boolean getStatus() {
		return isRunning;
	}

	public static synchronized boolean setStatus(boolean status) {
		isRunning = status;
		return isRunning;
	}
	
	public void fetchStream() {
		
		try {
			System.out.println("requesting...");
			URL url = new URL(smap_republish_url);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			// InputStream in = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			String line = reader.readLine();
			
			// inner try-catch to avoid Prematre EOF exception
			try {
				while (reader.ready() && (line = reader.readLine()) != null) {
					
					if(isRunning == false) {
						System.out.println(new Date().toLocaleString() + " ..fetchStream... Stoping the downloader.");
						System.gc();
						reader.close();
						urlConnection.disconnect();
						break;
					}						
					
					if (line.length() > 10) {
						data = line;
						counter++;
						cur_time = new Date();
						new SmapHandler(data).now();
						//SmapHandler.doProcess(data);
						// LOG.info(data);
					}					
					LOG.info("Pending in buffer : " + urlConnection.getInputStream().available());
				}
			} catch (Exception e) {
				System.out.println(new Date().toLocaleString() + " "
						+ e.getMessage());
				e.printStackTrace(System.out);
			} finally {
				reader.close();
				urlConnection.disconnect();
			}
		} catch (Exception e) {
			System.out.println(new Date().toLocaleString() + " "
					+ e.getMessage());
			e.printStackTrace(System.out);
		}		
		System.out.println(new Date().toLocaleString() + "..end of fetchStream");
		isRunning = false;
	}

	public void doJob() {
		System.out.println(new Date().toLocaleString() + "..inside doJob");
		start_time = new Date();
		fetchStream();
	}
}
