package smap;

public class SmapTagFormat {
	//http://nms.iiitd.edu.in:9101/api/tags
	
	public class LoadLocation {
        public String FlatNumber;
        public String Building;
        public String Wing;
        public String Floor;
    }
    
    public class Supply {
        public String BillingType;// : "Residential",
        public String Transformer;//: "Transformer-3",
        public String Source;//: "Utility",
        public String BusCoupler;//: "BusCoupler-1",
        public String Voltage;//: "Low",
        public String Panel;//: "Panel-4"
    }
    //SourceName: "Faculty Housing",
    public class Extra {
    	public String FlatNumber;
    	public String PhysicalParameter;
    	public String SupplyType;
    	public String LoadType;//: "Apartments",
    	public String IP;//: "192.168.136.7",
    	public String CommInterface;//: "Modbus",
    	public String Sync;//: "1",
    	public String MAC;//: "b8:27:eb:2a:e8:5c",
    	//public String FlatNumber;//: "203",
    	public String SubLoadType;//: "Apartment 203",
    	public String ControllerName;//: "FH-RPi02",
    	public String MeterLevel;//: "Metadata/Instrument/SupplyType",
    	public String MeterID;//: "22",
    	public String Type;//: "Power",
    	public String Wing;//: "0",
    	public String Block;//: "0"
    }
    
    public class Instrument {
        public String SupplyType;//: "Power",
        public String LoadType;//: "Apartments",
        public String SamplingPeriod;//: "1 Second",
        public String SubLoadType;//: "Apartment 203",
        public String Model;//: "EM6433",
        public String MeterID;//: "22",
        public String Manufacturer;//: "Schneider Electric"
    }
    
    public class Location {
    	public String Building;//: "Faculty Housing",
    	public String FlatNumber;//: "203",
    	public String City;//: "Delhi",
    	public String Country;//: "India",
    	public String Floor;//: "2",
    	public String Campus;//: "IIIT Delhi",
    	public String Wing;//: "0"
    }
    
    public class ControllerLocation {
    	public String Building;//: "Faculty Housing",
    	public String City;//: "Delhi",
    	public String Floor;//: "2",
    	public String Country;//: "India",
    	public String Wing;//: "0",
    	public String Campus;//: "IIIT Delhi"
    }
    
    public class Metadata {
    	public LoadLocation LoadLocation;
    	public Supply Supply;
    	public String SourceName;
    	public Extra Extra;
    	public Instrument Instrument;
    	public Location Location;
    	public ControllerLocation ControllerLocation;
    }
    
	class Properties {
        public String Timezone;
        public String UnitofMeasure;
        public String ReadingType;
	}
		
	public String Path;
	public String uuid;
	public String Readings;
	public Properties Properties;
	public Metadata Metadata;	
}
