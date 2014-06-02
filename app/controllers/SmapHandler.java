package controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import play.*;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.*;
import smap.SmapDevice;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import play.jobs.Job;

public class SmapHandler extends Job {

	public static final Logger LOG = Logger.getLogger(SmapHandler.class
			.getName());

	// private static String data = "";
	// private static Date time = new Date();;
	// private static long counter = 0;

	private String strJsonSmap = null;

	public static class smap {
		String uuid;
		List<List<Double>> Readings;
	}

	public SmapHandler(String strJsonSmap) {
		this.strJsonSmap = strJsonSmap;
	}

	public static void sendToSensorAct(String key, String device,
			String sensor, String channel, long time, String value) {

		String url = "http://sensoract.iiitd.edu.in:9011/" + device + "/"
				+ sensor + "/" + channel;
		// String url="http://localhost:9011/" + device + "/" + sensor + "/" +
		// channel;
		url = url + "?time=" + time + "&value=" + value;

		try {
			Map<String, String> header = new HashMap<String, String>();
			header.put("x-apikey", key);

			LOG.info(url);
			WSRequest wsr = WS.url(url).headers(header).timeout("30s");
			HttpResponse trainRes = wsr.put();
			String res = trainRes.getString();
			if (!res.contains("Device " + device)) {
				System.out.println(new Date().toLocaleString() + "\n"
						+ trainRes.getString() + "\n");
			}
		} catch (Exception e) {
			LOG.info("sendToSensorAct.. " + url + " " + e.getMessage());
		}
	}

	public static Gson gson = new GsonBuilder().create();

	public void doJob() {
		doProcess(strJsonSmap);
	}

	private void doProcess(String strJsonSmap) {

		// File f = Play.getFile("./conf/smap.json");
		// FileReader fr = new FileReader(f);
		// BufferedReader br = new BufferedReader(fr);
		// String json = br.readLine();

		int counter = 0;

		try {
			JsonElement jelement = new JsonParser().parse(strJsonSmap);
			JsonObject jobject = jelement.getAsJsonObject();

			long epoch;
			double data;

			for (Entry<String, JsonElement> entry : jobject.entrySet()) {
				String key = entry.getKey();
				JsonElement jsonReading = entry.getValue();

				// System.out.println(key);
				// System.out.println(value.toString());

				StringTokenizer st = new StringTokenizer(key, "/");
				String device = st.nextToken();
				String sensor = st.nextToken();
				String channel = st.nextToken();

				smap ss = (smap) gson.fromJson(jsonReading, smap.class);

				if (Application.deviceMap == null) {
					LOG.error(ss.uuid + " Device map NOT loaded...");
					break;
				}

				if (Application.deviceMap.containsKey(ss.uuid)) {
					SmapDevice sd = Application.deviceMap.get(ss.uuid);
					device = sd.Device;
					sensor = sd.Sensor;
					channel = sd.Channel;
				} else {
					// LOG.error(ss.uuid + " Not found in device map!" +
					// gson.toJson(ss));
					System.out.println("\n" + new Date().toLocaleString() + " "
							+ ss.uuid + " Not found in device map!\n"
							+ gson.toJson(ss));
					continue;
				}

				// 18:50:12,367 INFO ~ Data received |
				// nesl_owner:FacilitiesBuilding:UPS-Out4:Power 1393387078000
				// 3378.5517578125
				if (device.equals("FacilitiesBuilding")
						&& sensor.equals("UPS-Out4") && channel.equals("Power")) {
					// System.out.println(value);
				} else {
					// continue;
				}

				// System.out.println(device+sensor+channel);

				for (List<Double> l1 : ss.Readings) {

					epoch = l1.get(0).longValue();
					data = l1.get(1).doubleValue();

					counter++;
					// System.out.println(epoch);
					// System.out.println(data);

					// Thread.sleep(10);

					sendToSensorAct(key, device, sensor, channel, epoch, data
							+ "");
					// for(String l2: l1) {
					// System.out.println(l2);
					// }
				}
			}

			// System.out.println("#upload reqs " + counter);

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}
}
