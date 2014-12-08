package stellarium.stellars.orbit;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.initials.CCertificateHelper;
import stellarium.initials.CConstructReader.CExistenceProperty;
import stellarium.initials.CConstructReader.CProperty;
import stellarium.initials.CTranslateManager;
import stellarium.stellars.StellarManager;
import stellarium.stellars.cbody.CBody;
import stellarium.util.*;
import stellarium.util.math.Rotate;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;

public class Orbitse extends OrbitMv {
	public double a, e;
	public UpDouble w=new UpDouble(),
			M=new UpDouble();
	
	protected double wbar, wbard, L;
	
	protected boolean IsMajor;
	
	//Majors
	public static final String Pwbar="W", PL="L";
	public static final String Pwbard="Wd";
	
	protected CProperty CPwbar, CPL, CPwbard;
	
	//Minors
	public static final String Pa="a", Pe="e", Pi="i", Pw="w", POm="O", PM="M";
	public static final String Pid="id", Pwd="wd", POmd="Od";
	
	protected CProperty CPa, CPe, CPi, CPw, CPOm, CPM;
	protected CProperty CPid, CPwd, CPOmd;
	
	@Override
	public void RegisterOrbit() {
		CTranslateManager.AddTranslation("Common", "s");
		
		CTranslateManager.AddTranslation("Omega", POm);
		CTranslateManager.AddTranslation("Omegad", POmd);
		CTranslateManager.AddTranslation("wbar", Pwbar);
		CTranslateManager.AddTranslation("wbard", Pwbard);
	}
	
	
	@Override
	public void PreConstruct(){
		super.PreConstruct();
		
		CPOm = conreader.addProperty(POm, 1);
		CPM = conreader.addProperty(PM, 1);
		CPOmd = conreader.addProperty(POmd, 1);
		CPa = conreader.addProperty(Pa, 1);
		CPe = conreader.addProperty(Pe, 1);
		CPi = conreader.addProperty(Pi, 1);
		CPw = conreader.addProperty(Pw, 1);
		CPid = conreader.addProperty(Pid, 1);
		CPwd = conreader.addProperty(Pwd, 1);
		
		CPwbar = conreader.addProperty(Pwbar, 1);
		CPL = conreader.addProperty(PL, 1);
		CPwbard = conreader.addProperty(Pwbard, 1);
	}
	
	@Override
	public void PostConstruct(){
		super.PostConstruct();
		
		a = Spmath.StrtoD(conreader.ReadValue(CPa)[0]);
		e = Spmath.StrtoD(conreader.ReadValue(CPe)[0]);
		i.val0 = Spmath.StrtoD(conreader.ReadValue(CPi)[0]);
		i.vald = Spmath.StrtoD(conreader.ReadValue(CPid)[0]);
		Om.val0 = Spmath.StrtoD(conreader.ReadValue(CPOm)[0]);
		Om.vald = Spmath.StrtoD(conreader.ReadValue(CPOmd)[0]);
		
		IsMajor = false;
		
		if(conreader.ReadValue(CPwbar) != null)
			IsMajor = true;
		
		if(IsMajor){
			wbar = Spmath.StrtoD(conreader.ReadValue(CPwbar)[0]);
			wbard = Spmath.StrtoD(conreader.ReadValue(CPwbard)[0]);
			L = Spmath.StrtoD(conreader.ReadValue(CPL)[0]);
			
			w.val0 = wbar - Om.val0;
			w.vald = wbard - Om.vald;
			
			M.val0 = L - wbar;
		}
		else{
			w.val0 = Spmath.StrtoD(conreader.ReadValue(CPw)[0]);
			w.vald = Spmath.StrtoD(conreader.ReadValue(CPwd)[0]);
			
			M.val0 = Spmath.StrtoD(conreader.ReadValue(CPOmd)[0]);
		}
	}
	
	@Override
	public void PreCertificate(){
		super.PreCertificate();
		
		if(ParOrbit.IsVirtual){
			if(this == ParOrbit.SatOrbit.get(0)){
				if(ParOrbit.SatOrbit.get(1) instanceof Orbitse){
					Orbitse friend = (Orbitse)ParOrbit.SatOrbit.get(1);
					this.a = friend.a * friend.Mass / this.Mass;
					HillRadius();
				}
			}
		}
		
		M.vald = (2 * Math.PI) / (Math.sqrt(a)*a);
	}
	
	@Override
	public void Certificate(){
		CCertificateHelper cch = null;
		
		super.Certificate();
		if(ParOrbit instanceof OrbitMv){
			OrbitMv par = (OrbitMv) ParOrbit;
			
			if(this.MaxDistance() > par.Hill_Radius)
				cch.Unstable(this.Name+" Is Going to leave the"+ParOrbit.Name+"'s Gravitational Field!");
		}
	}
	
	@SideOnly(Side.SERVER)
	@Override
	public void Update(double yr) {
		super.Update(yr);
	}

	
	@SideOnly(Side.SERVER)
	@Override
	protected void UpdateOrbitalElements(double yr) {
		i.Update(yr);
		w.Update(yr);
		Om.Update(yr);
		M.Update(yr);
	}
	
	@SideOnly(Side.SERVER)
	protected void UpdateEcRPos(){
		Pos.set(Spmath.GetOrbVec(a, e, new Rotate('X').setRAngle(-Spmath.Radians(i.val)), new Rotate('Z').setRAngle(-Spmath.Radians(w.val)), new Rotate('Z').setRAngle(-Spmath.Radians(Om.val)), M.val));
	}
	
	@Override
	@SideOnly(Side.SERVER)
	protected void HillRadius() {
		Hill_Radius=a*(1-e)*Math.pow(this.Mass/(3.0*ParOrbit.Mass),1.0/3.0);
	}


	protected double MaxDistance() {
		return a*(1+e);
	}

	@Override
	public double GetAvgRot() {
		return M.vald;
	}
}
