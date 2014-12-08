package stellarium.stellars.cbody;

import stellarium.stellars.StellarManager;
import stellarium.stellars.local.LocalCValue;
import stellarium.stellars.orbit.OrbitMv;
import stellarium.stellars.orbit.OrbitSt;
import stellarium.initials.*;
import stellarium.util.math.*;
import stellarium.view.ViewPoint;
import stellarium.viewrender.render.RBase;
import cpw.mods.fml.relauncher.*;


public class StarBody extends CBody {
	
	public static final String PTemp="Te", PLum="Lu";
	boolean InitLum;
	public static final double SunTemp=5.778e+3, SunRadius=4.654e-3;
	
	@Override
	public void RegisterCBody() {
		CTranslateManager.AddTranslation("Temp", PTemp);
		CTranslateManager.AddTranslation("Temperature", PTemp);
		CTranslateManager.AddTranslation("Luminosity", PLum);
	}
	
	public void Construct(String[] ConBody){
		InitLum=false;
		if(manager.Star!=null)
			CCertificateHelper.IllegalConfig("Two Stars are not supported (until 0.2.0)");
		else manager.Star=this; 
			
		super.Construct(ConBody);
		if(InitLum){
			this.Temp=Math.sqrt(Math.sqrt(Lum)/Radius*SunRadius)*SunTemp;
		}
		else{
			double Rad=Radius/SunRadius;
			double T=Temp/SunTemp;
			this.Lum=Rad*Rad*T*T*T*T;
		}
		B_V=Spmath.TemptoB_V(Temp);
	}
	
	public void ConstructLoop(String[] ConBody){
		super.ConstructLoop(ConBody);
		
		if(ConBody[j].equals(PTemp)){
			j++;
			this.Temp=Spmath.StrtoD(ConBody[j]);
		}
		
		if(ConBody[j].equals(PLum)){
			j++;
			this.Lum=Spmath.StrtoD(ConBody[j]);
			InitLum=true;
		}
	}
	
	public void Certificate(){
		CCertificateHelper cch=null;
		if(this.Mass < cch.StarLimit)
			cch.Unstable(Name+" lacks Mass to be a Star!");
	}
	
	
	double B_V;
	
	@SideOnly(Side.SERVER)
	@Override
	public void Update(double yr) {
		super.Update(yr);
		UpdateLuminosity();
	}

	@Override
	@SideOnly(Side.SERVER)
	protected void UpdateLuminosity(){
		;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double GetMag() {
//		double Flux=Lum/(theOrbit.Dist*theOrbit.Dist);
//		return -26.74-2.5*Math.log10(Flux);
		return 0.0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void DrawImg() {
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RBase RenderBody(LocalCValue lcv, double res) {
		return null;
	}



}

