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

	private static final String smap_republish_url = "http://192.168.1.40:9101/republish";
	
	//private static final String smap_republish_url = "http://localhost:9011";

	public static boolean isRunning = false;

	public static String getStatus() {
		return "Started " + start_time.toLocaleString() + "\nCurrent "
				+ cur_time.toLocaleString() + "\nCounter : " + counter + "\n\n"
				+ data;
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
					if (line.length() > 10) {
						data = line;
						counter++;
						cur_time = new Date();
						SmapHandler.doProcess(data);
						// LOG.info(data);
					}
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
	}

	public void doJob() {
		System.out.println("..inside doJob");
		isRunning = true;
		start_time = new Date();

		while (isRunning) {
			fetchStream();
		}
	}

}
