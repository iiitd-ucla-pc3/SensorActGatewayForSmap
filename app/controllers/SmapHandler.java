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

public class SmapHandler {

	public static final Logger LOG = Logger.getLogger(SmapHandler.class.getName());
	
	private static String data = "";
	private static Date time = new Date();;
	private static long counter = 0;
	
	public static class smap {
		String uuid;
		List<List<Double>> Readings;
	}
	
    public static void sendToSensorAct(String key, String device, String sensor,
			String channel, long time, String value) {

    	String url="http://localhost:9011/" + device + "/" + sensor + "/" + channel;
		url = url + "?time="+time + "&value="+value;			
		
		try {
			Map<String,String> header = new HashMap<String,String>();
			header.put("x-apikey", key);
			
			LOG.info(url);			
			WSRequest wsr = WS.url(url).headers(header).timeout("30s");		
			HttpResponse trainRes = wsr.put();	
		} catch (Exception e) {
			LOG.info("sendtoAnother.. " + url + " " + e.getMessage());
		}
	}
	
    public static void doProcess(String json) {

    	//File f = Play.getFile("./conf/smap.json");
    	//FileReader fr = new FileReader(f);    	
    	//BufferedReader br = new BufferedReader(fr);    	
    	//String json = br.readLine();
    	
    	try {
	    	JsonElement jelement = new JsonParser().parse(json);    	
	        JsonObject  jobject = jelement.getAsJsonObject();
	        
	        long epoch;
	        double data;
	        
	        for (Entry<String, JsonElement> entry : jobject.entrySet()) {
	            String key = entry.getKey();
	            JsonElement value = entry.getValue();            
	            
	            //System.out.println(key);
	            //System.out.println(value.toString());
	            
	            StringTokenizer st = new StringTokenizer(key,"/");
	            
	            String device = st.nextToken();
	            String sensor = st.nextToken();
	            String channel = st.nextToken();
	            
	            //System.out.println(device+sensor+channel);
	            
	            Gson gson = new GsonBuilder().create();            
	            smap ss = (smap) gson.fromJson(value, smap.class);
	            
	            for(List<Double> l1: ss.Readings) {
	            	
	            	epoch = l1.get(0).longValue();
	            	data = l1.get(1).doubleValue();
	            	
	            	System.out.println(epoch);
	            	System.out.println(data);
	            	
	            	sendToSensorAct(key, device, sensor, channel, epoch, data+"");
	            	//for(String l2: l1) {
	            		//System.out.println(l2);
	            	//}
	            }
	        }
    	} catch(Exception e) {
    		LOG.error(e.getMessage());
    	}
	}
}
