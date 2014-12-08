package stellarium.stellars.background;

import stellarium.stellars.Color;
import stellarium.stellars.StellarManager;
import stellarium.util.math.Spmath;
import stellarium.viewrender.render.*;
import stellarium.viewrender.scope.Scope;

public class StellarBgManager {
	
	StellarManager manager;
	
	StarManager brs = new BrStarManager();
	//StarManager mids = new MidStarManager();
	//StarManager fns = new FnStarManager();
	
	//DSObjManager mes = new MessierManager();
	
	public StellarBgManager(StellarManager m){
		manager = m;
	}
	
	/*public void AddBgToRender(StellarRenders sr){
		Vec view = sr.view;
		Scope scope = sr.scope;
		Star[] star;
		DSObj[] dsobj;
		
		int lev = GetLev(scope);
		
		if(lev == 0){
			star = brs.GetStarArray(view, scope.FOV / 2);
			
			dsobj = new DSObj[1];
		}
		else if(lev == 1){
			star = mids.GetStarArray(view, scope.FOV / 2);
			
			dsobj = mes.GetDsObjArray(view, scope.FOV / 2);
		}
		else {
			star = fns.GetStarArray(view, scope.FOV / 2);
			
			dsobj = mes.GetDsObjArray(view, scope.FOV / 2);
		}
		
		
		for(int i=0; i < star.length; i++){
			RPointy rp = ((RPointy)(new RPointy()
			.SetColor(Color.GetColor(star[i].B_V))
					.SetPos(star[i].Pos)
					.SetLum(Spmath.MagToLum(star[i].Mag))));
			sr.RenderPointy(rp);
		}
		
		for(int i=0; i < dsobj.length; i++){
			RDSObj rs = (RDSObj) new RDSObj()
			.SetSize(dsobj[i].Size)
			.SetImgLoc(dsobj[i].ImgLoc)
			.SetPos(dsobj[i].Pos)
			.SetLum(Spmath.MagToLum(dsobj[i].Mag));
			sr.RenderDSObj(rs);
		}
		
	}
	
	public int GetLev(Scope scope){
		if(scope.Conc < 16.0)
			return 0;
		else if(scope.Conc < 200.0)
			return 1;
		else return 2;
	}*/
}
