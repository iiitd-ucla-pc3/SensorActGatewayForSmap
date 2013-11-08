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
	
	public static boolean isRunning = true;
	
	public static String getStatus() {
		return time.toLocaleString() + "\nCounter : " + counter + "\n\n" + data; 
	}

	public void doJob() {
		while (isRunning) {
			try {
				HttpResponse res = WS.url("http://192.168.1.40:9101/republish")
						.get();
				InputStream is = res.getStream();

				DataInputStream dis = new DataInputStream(is);
				String line = null;

				while ((line = dis.readLine()) != null) {					
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
