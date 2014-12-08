package stellarium.viewrender.viewer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.stellars.background.StellarBgManager;
import stellarium.stellars.cbody.*;
import stellarium.stellars.local.*;
import stellarium.stellars.orbit.*;
import stellarium.view.VPClient;
import stellarium.viewrender.render.*;
import stellarium.viewrender.scope.*;
import stellarium.stellars.*;

@SideOnly(Side.CLIENT)
public class Viewer {
	public VPClient CurVp;
	
	public StellarRenderManager rm;
	public StellarBgManager bgm;
	
	public StellarManager manager;

	
	public void SetViewer(StellarManager m){
		manager=m;
		CurVp=new VPClient();
		CurVp.InitVp(manager);
		rm=new StellarRenderManager();
		rm.Init(this);
	}
	
	
	public void SetScope(ScopeEye sc){
		rm.eye.SetScope(sc);
	}
	
	public void SetCCDScope(ScopeCCD scd){
		if(rm.CCD!=null) rm.CCD.SetScope(scd);
	}
	
	public void EnableCCD(){
		rm.EnableCCD();
	}
	
	public void DisableCCD(){
		rm.DisableCCD();
	}

	
	public void OnRender(double part){
		CurVp.Setpartial(part);
		rm.PreRenderAdd();
		RenderOrbit(manager.mvmanager.CSystem, part);
		rm.PostRenderAdd();
		rm.Render();
	}
	
	public void RenderOrbit(Orbit orb, double part){
		if(!orb.IsVirtual)
			rm.AddCBodyforRender(orb.theBody, part);
		for(int i=0; i<orb.SatOrbit.size(); i++)
			RenderOrbit(orb.SatOrbit.get(i), part);
	}
}
