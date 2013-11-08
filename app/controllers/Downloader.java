package controllers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.Logger;

import play.jobs.Job;
import play.libs.WS;
import play.libs.WS.HttpResponse;

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
				HttpResponse res = WS.url("http://sensoract.iiitd.edu.in:9010/smap/get")
						.get();
				InputStream is = res.getStream();

				DataInputStream dis = new DataInputStream(is);
				String line = null;

				while ((line = dis.readLine()) != null) {
					System.out.println(line);
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
