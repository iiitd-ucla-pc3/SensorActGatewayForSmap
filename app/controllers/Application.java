package controllers;

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
import java.io.FileWriter;
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
import com.google.gson.reflect.TypeToken;

import models.*;
import java.lang.reflect.*;

public class Application extends Controller {

	public static Map<String, SmapDevice> deviceMap = null;
	private static File device_map_file = Play.getFile("./conf/devicemap.json");
	
	public static boolean isDownloaderStarted = false; 
	
	static {
		//new Application().loadDeviceMap();
		try {
			Type type = new TypeToken<Map<String, SmapDevice>>() {}.getType();
			FileReader fr = new FileReader(device_map_file);
			deviceMap = SmapStreamList.gson.fromJson(fr, type);
			System.out.println("Loaded device map");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void index() throws Exception {
		//renderJSON(new SmapStreamList().convertToSADevices());
		renderText("sMAP gateway for SensorAct! \nURLs " +
				"\n /smap/start " +
				"\n /smap/stop " +
				"\n /smap/status " +
				"\n /devicemap/create " +
				"\n /devicemap/load");
	}

	

	// Create sMAP tags to SensorAct devices map
	public static void createDeviceMap() {
		try {
			SmapStreamList sat = new SmapStreamList();
			Map<String, SmapDevice> saMap = sat.convertToSADevices();
			FileWriter fw = new FileWriter(device_map_file);			
			String json = sat.gson.toJson(saMap);
			fw.write(json);
			fw.close();
			renderText(json);
		} catch (Exception e) {
			renderText(e);
		}
	}

	public static void loadDeviceMap() {		
		try {
			Type type = new TypeToken<Map<String, SmapDevice>>() {}.getType();
			FileReader fr = new FileReader(device_map_file);
			deviceMap = SmapStreamList.gson.fromJson(fr, type);
			System.out.println("Loaded device map");
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJSON(deviceMap);
	}
	
	public static void smapStart() {
		if (!Downloader.getStatus()) {
			Downloader.setStatus(true);
			new Downloader().now();
			isDownloaderStarted = true;
		}
		renderText(Downloader.getStatusMessage());
	}
	
	public static void smapStop() {
		Downloader.setStatus(false);
		renderText(Downloader.getStatusMessage());
		isDownloaderStarted = false;
	}
	
	public static void smapStatus() {
		renderText(Downloader.getStatusMessage());
	}
	
	public static void test(String device, String sensor, String channel) {
		renderText("tested..." + device + sensor + channel);
	}	
}