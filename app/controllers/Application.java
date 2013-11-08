package controllers;

import play.*;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.*;

import java.io.DataInputStream;
import java.io.InputStream;
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
    	if(!Downloader.isRunning) {
    		new Downloader().doJob();
    	}
    	
    	renderText(Downloader.getStatus());
    }
}