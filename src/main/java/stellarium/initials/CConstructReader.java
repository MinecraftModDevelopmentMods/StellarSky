package stellarium.initials;

import java.util.*;

public class CConstructReader {
	
	public class CProperty{
		
		protected String PropComName;
		protected ArrayList<CModes> ModeRelated;
		protected ArrayList<String> ModeforRead;
		
		public int Place;
		public String[] Read;
		
		public int NumStr;
		
		CProperty(String propname, int numstr){
			PropComName=propname;
			NumStr=numstr;
			
			Read = new String[numstr];
		}
		
		public String getPropName(){
			return PropComName;
		}
		
		public boolean addModeforRead(CModes moderelated, String modeforread){
			if(ModeRelated.contains(moderelated))
				return false;
			if(!moderelated.containsMode(modeforread))
				return false;
			
			ModeRelated.add(moderelated);
			ModeforRead.add(modeforread);
			
			return true;
		}
		
		public boolean checkReadableMode(){
			
			for(int i=0; i<ModeRelated.size(); i++){
				if(!ModeRelated.get(i).getCurrentMode().equals(ModeforRead.get(i)))
					return false;
			}
			
			return true;
		}
	}
	
	public class CExistenceProperty extends CProperty
	{
		public boolean Readb = false;
		
		 protected CModes modesrelated;
		 protected String modefrom;
		 protected String modeto;
		 
		 protected boolean stopper;
		
		CExistenceProperty(String propname) {
			super(propname, 1);
		}
		
		CExistenceProperty(String propname, CModes modesrel, String from, String to){
			super(propname, 1);
			
			modesrelated = modesrel;
			
			modefrom = from;
			modeto = to;
		}
		
		CExistenceProperty(String propname, boolean stop){
			super(propname, 1);
			
			stopper = stop;
		}
		
		public void OnEnabled(){
			if(modesrelated.getCurrentMode().equals(modefrom))
				modesrelated.ChangeMode(modeto);
		}
	}
	
	public class CModes{
		protected ArrayList<String> list = new ArrayList<String>();
		protected int currentmode = 0;
		
		public boolean addMode(String ModeName){
			if(list.contains(ModeName))
				return false;
			list.add(ModeName);
			return true;
		}
		
		public boolean containsMode(String ModeName){
			return list.contains(ModeName);
		}
		
		public String getCurrentMode(){
			return list.get(currentmode);
		}
		
		public boolean ChangeMode(String ModeName){
			for(int i=0; i<list.size(); i++)
				if(list.get(i).equals(ModeName))
				{
					currentmode = i;
					return true;
				}
			return false;
		}
		

	}
	
	
	
	ArrayList<CProperty> properties;
	Map<String, CModes> modes = new HashMap<String, CModes>();
	
	
	public CModes addModes(String ModesName){
		if(modes.containsKey(ModesName))
			return getModes(ModesName);
		CModes mode = new CModes();
		modes.put(ModesName, mode);
		return mode;
	}
	
	public CModes getModes(String ModesName){
		return modes.get(ModesName);
	}
	
	public CProperty addProperty(String PropComName, int NumStr){
		CProperty theprop = new CProperty(PropComName, NumStr);
		properties.add(theprop);
		return theprop;
	}
	
	public CExistenceProperty addStopProperty(String PropComName){
		CExistenceProperty theprop = new CExistenceProperty(PropComName, true);
		properties.add(theprop);
		return theprop;
	}
	
	public CExistenceProperty addExistenceProperty(String PropComName, CModes modesrel, String from, String to){
		if(modesrel.containsMode(from) && modesrel.containsMode(to)){
			CExistenceProperty theprop = new CExistenceProperty(PropComName, modesrel, from, to);
			properties.add(theprop);
			return theprop;
		}
		else return null;
	}
	
	public void Read(String[] ConStr){
		int i=0;
		while(i < ConStr.length){
			for(int j=0; j < properties.size(); j++){
				CProperty theprop = properties.get(j);
				if(theprop.getPropName().equals(ConStr[i]) && theprop.checkReadableMode()){
					
					theprop.Place = i;
					
					if(theprop instanceof CExistenceProperty)
					{
						CExistenceProperty theprope = (CExistenceProperty) theprop;
						
						if(theprope.stopper){
							i = ConStr.length;
							break;
						}
						
						theprope.OnEnabled();
						theprope.Readb = true;
					}
					else{
						for(int k=0; k < theprop.NumStr; k++)
							theprop.Read[k] = ConStr[i+k+1];
						
						i += theprop.NumStr;
					}
					
					break;
				}
			}
			
			i++;
		}
	}
	
	
	/**
	 * Get Read Value from property
	 * Only use after Read
	 * */
	public String[] ReadValue(CProperty prop){
		return prop.Read;
	}
	
	/**
	 * Get Read Value from property
	 * Only use after Read
	 * */
	public boolean ReadValue(CExistenceProperty prop){
		return prop.Readb;
	}
}
