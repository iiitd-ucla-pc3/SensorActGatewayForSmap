package controllers;

import play.*;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	renderText("sMAP gateway for SensorAcat!");
    }

    public static void smapPut() {
    	SmapHandler.doProcess(request.params.get("body"));
        renderText("Got It!");
    }

    public static void smapGet() {  
    	    	 
		while (true) {			
			try {
				
				
				System.out.println("requestion...");
				//HttpResponse res = WS.url("http://sensoract.iiitd.edu.in:9010/smap/get").get();
				//InputStream is = res.getStream();
				//DataInputStream dis = new DataInputStream(is);
				
				URL url = new URL("http://192.168.1.40:9101/republish");
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
						
				InputStream in = url.openStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String result, line = reader.readLine();
				result = line;
				
				while((line=reader.readLine())!=null){
				    //result+=line;
				    System.out.println(line);
				}			
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*			
    	if(!Downloader.isRunning) {
    		System.out.println("Starting downloader..");   		
    		Downloader.isRunning = true;
    		new Downloader().now();
    		System.out.println("Done..");
    		renderText("Started the downloader..");
    	}
    	
    	renderText(Downloader.getStatus());
    	*/
    }
}