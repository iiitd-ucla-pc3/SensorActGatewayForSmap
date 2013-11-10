package controllers;

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

import models.*;

public class Application extends Controller {
	
	
    public static void index() throws Exception {
     	renderText("sMAP gateway for SensorAcat! ");
    }

    public static void smapGet() {  
    	if(!Downloader.isRunning) {
    		System.out.println("Starting downloader..");   		
    		Downloader.isRunning = true;
    		new Downloader().now();
    		System.out.println("Done..");
    		renderText("Started the downloader..");
    	}    	
    	renderText(Downloader.getStatus());    	
    }
}