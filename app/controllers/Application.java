package controllers;

import play.*;
import play.mvc.*;

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
        renderText(SmapHandler.doGet());
    }
}