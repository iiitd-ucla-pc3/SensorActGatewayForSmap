package controllers;

import java.util.List;

import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import smap.SmapTagFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.*;
import java.util.*;
import smap.*;

public class SmapStreamList {

	//private String smap_uuid_url = "http://nms.iiitd.edu.in:9101/api/query/uuid";
	private String smap_uuid_url = "http://192.168.1.38:9106/api/query/uuid";
	private String smap_tags_url = "http://192.168.1.38:9106/api/tags";
	public static Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues()
			.setPrettyPrinting().create();

	public String getUUIDList() {

		WSRequest wsr = WS.url(smap_uuid_url).timeout("300s");
		HttpResponse wsRes = wsr.get();

		String res = wsRes.getString();
		List<String> uuidList = gson.fromJson(res,
				new TypeToken<List<String>>() {
				}.getType());

		// List<Video> videos = gson.fromJson(json, new
		// TypeToken<List<Video>>(){}.getType());
		return gson.toJson(uuidList);
		// http://nms.iiitd.edu.in:9101/api/query/uuid
	}

	public String getTags() {
		System.out.println("Downloading tags from " + smap_tags_url );
		WSRequest wsr = WS.url(smap_tags_url).timeout("300s");
		HttpResponse wsRes = wsr.get();
		return wsRes.getString();
		// http://nms.iiitd.edu.in:9101/api/query/uuid
	}

	public String parseTags() {
		String tagsJson = getTags();
		Type type = new TypeToken<List<SmapTagFormat>>() {
		}.getType();
		List<SmapTagFormat> tagList = gson.fromJson(tagsJson, type);

		System.out.print(tagList.size());
		return gson.toJson(tagList);
	}
		
	public String getSensorName(String device, SmapTagFormat tag) {
		
		String sensor = null;
		if (device.equals("Faculty Housing")) {
			
			String block = tag.Metadata.Extra.SubLoadType;			
			block = (block == null) ? tag.Metadata.Instrument.SubLoadType : block;
			if(block == null || block.equals("Unused")) {				
				StringTokenizer st = new StringTokenizer(tag.Path, "/");				
				block = st.nextToken() + "_" + st.nextToken();
				return block;
			}						
			String type1 = null;			
			if(tag.Metadata.Instrument.LoadType != null && tag.Metadata.Instrument.LoadType.equals("Apartments")) {
				type1 = tag.Metadata.Instrument.SupplyType; 
			}			
			type1 = (type1==null) ? tag.Metadata.Instrument.SupplyType : type1;
			type1 = (type1==null) ? tag.Metadata.Extra.SupplyType : type1;			
			sensor = block + "_" + type1;
			
		} else if (device.equals("Boys Hostel") || device.equals("Girls Hostel")) {
			
			String wing = tag.Metadata.Extra.Wing;			
			wing = (wing == null) ? tag.Metadata.LoadLocation.Wing : wing;
			String type1 = tag.Metadata.Extra.Type;
			
			String floor = null;
			if(tag.Metadata.LoadLocation != null ) {
				floor = tag.Metadata.LoadLocation.Floor;
			}			
			floor = (floor == null) ? tag.Metadata.Location.Floor : floor;
					
			sensor = "Wing" + wing + "_" + type1 + "_Floor" + floor;
		} else if (device.equals("Academic Building")) {
			
			String block = tag.Metadata.Extra.Block;
			//wing = null;
			//if(tag.Metadata.LoadLocation != null ) {
				//wing = tag.Metadata.LoadLocation.Floor;
			//}			
			String type1 = tag.Metadata.Extra.Type;
			
			// included wing information to avoid duplicate names
			String floor = null;			
			if(tag.Metadata.LoadLocation != null ) {
				floor = tag.Metadata.LoadLocation.Floor + "W" + tag.Metadata.LoadLocation.Wing;
			}			
			// adjust the duplicate by inlcuding the wing
			if(floor == null) {
				floor = tag.Metadata.Location.Floor + "W" + tag.Metadata.Extra.Wing;
			}			
			sensor = block + "_" + type1 + "_" + floor;
		} else if (device.equals("Library Building") || device.equals("Mess Building")) {
			String type1 = tag.Metadata.Extra.Type;
			
			String floor = null;			
			if(tag.Metadata.LoadLocation != null ) {
				floor = tag.Metadata.LoadLocation.Floor;
			}			
			floor = (floor == null) ? tag.Metadata.Location.Floor : floor;
			sensor = "Floor" + floor + "_" + type1;
		} else if (device.equals("Facilities Building")) {
			sensor = tag.Metadata.Extra.Type;
		}
		
		if (sensor == null) {			
			StringTokenizer st = new StringTokenizer(tag.Path, "/");				
			sensor = st.nextToken() + "_" + st.nextToken();			
		}
		return sensor;
	}

	public Map<String, SmapDevice> convertToSADevices() {

		System.out.println("getting tags");
		String tagsJson = getTags();

		System.out.println("parsing tags");
		Type type = new TypeToken<List<SmapTagFormat>>() {
		}.getType();
		List<SmapTagFormat> tagList = gson.fromJson(tagsJson, type);

		Map<String, SmapDevice> saMap = new HashMap<String, SmapDevice>();		
		Map<String, SmapDevice> devMap = new TreeMap<String, SmapDevice>();
		
		String device = null, sensor = null, channel = null;
		for (SmapTagFormat tag : tagList) {
			
			StringTokenizer st = new StringTokenizer(tag.Path, "/");
			int tc = st.countTokens();

			if (tc == 2) {
				device = tag.Metadata.SourceName;
				sensor = st.nextToken();
				channel = st.nextToken();
			} else if (tc == 3) {
				device = tag.Metadata.SourceName;
				sensor = getSensorName(device, tag);
				st.nextToken();
				st.nextToken();
				channel = st.nextToken();
			} else if (tc == 5) {
				device = st.nextToken();
				device = device.concat("_").concat(st.nextToken());
				device = device.concat("_").concat(st.nextToken());
				sensor = st.nextToken();
				channel = st.nextToken();
			} else {
				System.out.println("Invalid #tokens " + tag.Path);
			}			
			
			device = device.replace(" ", "");
			sensor = sensor.replace(" ", "");

			// System.out.println(device + sensor + channel);
			SmapDevice sd = new SmapDevice();
			sd.Path = tag.Path;
			sd.Device = device;
			sd.Sensor = sensor;
			sd.Channel = channel;
			
			saMap.put(tag.uuid, sd);
			
			String dd = device + "..." + sensor + "..." + channel;			
			if(devMap.containsKey(dd)) {
				System.out.println("Duplicatte deivce " + dd);
			}
			
			devMap.put(dd, sd);
		}
		
		return saMap;
		//return gson.toJson(saMap);		
		//return gson.toJson(tsMap.keySet());
		//return gson.toJson(tsMap.keySet()) + "\n" + gson.toJson(tagList2);
		// return null;
	}

	public String convertToSADevices_Test() {

		System.out.println("getting tags");
		String tagsJson = getTags();

		System.out.println("parsing tags");
		Type type = new TypeToken<List<SmapTagFormat>>() {
		}.getType();
		List<SmapTagFormat> tagList = gson.fromJson(tagsJson, type);

		List<SmapTagFormat> tagList1 = new ArrayList<SmapTagFormat>();		
		for(SmapTagFormat tag: tagList) {
			if(tag.Metadata.SourceName.equals("Academic Building")){
				tagList1.add(tag);
			}
		}
	
		Map<String, SmapDevice> saMap = new HashMap<String, SmapDevice>();

		String device = null, sensor = null, channel = null;

		for (SmapTagFormat tag : tagList) {

			StringTokenizer st = new StringTokenizer(tag.Path, "/");
			int tc = st.countTokens();

			if (tc == 2) {
				device = tag.Metadata.SourceName;
				sensor = st.nextToken();
				channel = st.nextToken();
			} else if (tc == 3) {
				device = tag.Metadata.SourceName;
				sensor = getSensorName(device, tag);
				st.nextToken();
				st.nextToken();
				channel = st.nextToken();
			} else if (tc == 5) {
				device = st.nextToken();
				device = device.concat("_").concat(st.nextToken());
				device = device.concat("_").concat(st.nextToken());
				sensor = st.nextToken();
				channel = st.nextToken();
			} else {
				System.out.println("Invalid #tokens " + tag.Path);
			}

			// System.out.println(device + sensor + channel);

			SmapDevice sd = new SmapDevice();
			sd.Device = device;
			sd.Sensor = sensor;
			sd.Channel = channel;
			
			saMap.put(tag.uuid, sd);
		}
		
		List<SmapTagFormat> tagList2 = new ArrayList<SmapTagFormat>();		
		
		Map<String, SmapTagFormat> tsMap = new TreeMap<String, SmapTagFormat>();
		//String wing = "";
//		/String block = "";
		//String type1 = "";
		//String floor = "";
		
		String tosend = "";
		
		for (SmapTagFormat tag : tagList1) {
			
			String block = tag.Metadata.Extra.Block;
			//wing = null;
			//if(tag.Metadata.LoadLocation != null ) {
				//wing = tag.Metadata.LoadLocation.Floor;
			//}			
			String type1 = tag.Metadata.Extra.Type;
			
			String floor = null;			
			if(tag.Metadata.LoadLocation != null ) {
				floor = tag.Metadata.LoadLocation.Floor + "W" + tag.Metadata.LoadLocation.Wing;
			}
			
			// adjust the duplicate by inlcuding the wing
			if(floor == null) {
				floor = tag.Metadata.Location.Floor + "W" + tag.Metadata.Extra.Wing;
			}
		
			//floor = (floor == null) ? tag.Metadata.Location.Floor : floor;
			
			sensor = block + "_" + type1 + "_" + floor;
			
			StringTokenizer st = new StringTokenizer(tag.Path, "/");
			st.nextToken(); st.nextToken();
			
			String ch = sensor +  st.nextToken();
			
			if(tsMap.containsKey(ch)) {
				System.out.println(tag.Path + " " + sensor);
				tosend = tosend + "\n\n" + gson.toJson(tag) + "\n" + gson.toJson(tsMap.get(ch));
				tosend = tosend + "\n\n\n\n";
			}
			
			tsMap.put(ch, tag);
			if(type1 == null) {
				//tagList2.add(tag);
			}			
			//tagList2.add(tag);
		}

		//return gson.toJson(tsMap.keySet());
		return gson.toJson(tsMap.keySet()) + "\n" + gson.toJson(tagList2) + tosend;
		// return null;
	}

	public void doProcess() {
	}

}
