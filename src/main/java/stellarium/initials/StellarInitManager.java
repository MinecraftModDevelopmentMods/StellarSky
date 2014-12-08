package stellarium.initials;

import java.io.IOException;

import stellarium.stellars.StellarManager;
import stellarium.stellars.orbit.OrbitSt;

public class StellarInitManager{
	public StellarManager manager;
	
	public CConfiguration configf;
	
	public CConstructManager conmanager = new CConstructManager();
	public CTranslateManager trmanager = new CTranslateManager();
	
	public StellarInitManager(StellarManager m){
		manager = m;
		
		conmanager.PreRegister();
		trmanager.TranslationBase();
	}
	
	public void SetConfig() throws IOException{
		configf = new CConfiguration("CWorld");
		if(configf.NeedCreate)
			configf.InitializebyFile("config/CWorld.cfg", 200000);
			

	}
	
	public OrbitSt ConstructSystem() throws IOException{
		byte[] config;
		
		if(configf == null)
			SetConfig();
		
		String[] CWorld = trmanager.Translate(configf.Extract());
		
		//manager.datamanager.ConStr = CWorld;
		
		return conmanager.Construct(CWorld);
	}
	
	public OrbitSt ConstructSystem(byte[] context){
		String[] CWorld = trmanager.Translate(context);
		
		//manager.datamanager.ConStr = CWorld;
		
		return conmanager.Construct(CWorld);
	}
	
	public OrbitSt ConstructSystem(String[] CWorld){
		
		//manager.datamanager.ConStr = CWorld;
		
		return conmanager.Construct(CWorld);
	}
}
