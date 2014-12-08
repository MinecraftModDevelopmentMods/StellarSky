package stellarium.stellars.orbit;

import java.util.*;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import scala.Int;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.*;
import stellarium.util.math.*;
import stellarium.initials.*;
import stellarium.initials.CConstructReader.*;
import stellarium.stellars.*;
import stellarium.stellars.cbody.*;
import stellarium.stellars.local.*;
import stellarium.view.ViewPoint;


public abstract class Orbit {
	

	public Orbit ParOrbit;
	public ArrayList<Orbit> SatOrbit;
		
	public boolean IsVirtual=false;
	
	public CBody theBody;
	
	public String Name;
	
	public double Mass;
	
	public EVector Pos;
	
	
	public StellarManager manager;
	
	public CConstructReader conreader = new CConstructReader();
	
	
	abstract public void RegisterOrbit();
	
	
	public static final String CBody= "+";
	public static final String Virtual= "V";
	public static final String MassP= "M";
	public static final String CBType= "T";

	protected CModes virbody;
	
	protected CExistenceProperty PVirtual;
	protected CProperty PMass;
	protected CProperty PCBType;
	
	public void PreConstruct(){
		virbody = conreader.addModes("VirBody");
		virbody.addMode("V");
		virbody.addMode("E");
		
		PVirtual = conreader.addExistenceProperty(Virtual, virbody, "V", "E");
		conreader.addStopProperty(CBody);
		
		PMass = conreader.addProperty(MassP, 1);
		PMass.addModeforRead(virbody, "E");
		
		PCBType = conreader.addProperty(CBType, 1);
		PCBType.addModeforRead(virbody, "E");
	}
	
	//Do Not Overload this class
	public void Construct(String[] ConOrb){
		PreConstruct();
		conreader.Read(ConOrb);
		PostConstruct();
	}
	
	public void PostConstruct(){
		this.IsVirtual = conreader.ReadValue(PVirtual);
		this.Mass = Spmath.StrtoD( conreader.ReadValue(PMass)[0] );
		String CBType = conreader.ReadValue(PCBType)[0];
		
		CConstructManager ccm = null;
		
		
		if(!ccm.CBTypes.containsKey(CBType)){
			ccm.IllegalForm("Body Type "+CBType+" Not Exist!");
		}
		Class<? extends CBody> bodytype = ccm.CBTypes.get(CBType);
		try {
			theBody = bodytype.newInstance();
		} catch (InstantiationException e) {
			ccm.IllegalForm("Invalid Body Type " + CBType);
		} catch (IllegalAccessException e) {
			ccm.IllegalForm("Invalid Body Type " + CBType);
		}
	}
	
	public void PreCertificate(){
		if(IsVirtual)
			this.Mass = SatOrbit.get(0).Mass + SatOrbit.get(1).Mass;
		else theBody.PreCertificate();
	}
	
	public void Certificate(){
		CCertificateHelper cch = null;
		
		if(IsVirtual){
			if(SatOrbit.size()<2)
				cch.IllegalConfig("Virtual Orbit Must have two or more Satellite Orbits.");
			if(!SatOrbit.get(0).getClass().isInstance(SatOrbit.get(1))){
				cch.IllegalConfig("Two Main Satellite Orbits of virtual orbit should be the same type.");
			}	
		}
		else theBody.Certificate();
	}
	

	@SideOnly(Side.SERVER)
	public void Update(double yr){
		if(!IsVirtual)
			theBody.Update(yr);
		for(int i=0; i<SatOrbit.size(); i++)
			SatOrbit.get(i).Update(yr);
	}
	
	@SideOnly(Side.SERVER)
	public boolean Match(String name){
		return name.equals(Name);
	}
	
	@SideOnly(Side.SERVER)
	public Orbit Search(String name){
		if(Match(name)) return this;
		else{
			Orbit orb;
			for(int i=0; i<SatOrbit.size(); i++){
				orb=SatOrbit.get(i).Search(name);
				if(orb!=null) return orb;
			}
		}
		return null;
	}
	
	@SideOnly(Side.SERVER)
	public void addSatellite(Orbit satorbit){
		satorbit.manager=this.manager;
		if(!satorbit.IsVirtual){
			CBody body=satorbit.theBody;
			body.manager=this.manager;
			if(manager.side.isClient())
				body.PointyTexLoc=this.theBody.PointyTexLoc;
		}
		SatOrbit.add(satorbit);
	}
	
	/*
	 * Local for Viewpoint
	 * Start
	 * */

	public void GetLocalized(ViewPoint vp, LocalCValue lcv){
		lcv.Pos.set(VecMath.sub(Pos, vp.EcRPos));
		lcv.Dist=Spmath.getD(VecMath.size(lcv.Pos));
		if(!this.IsVirtual)
			theBody.GetLocalized(vp, lcv);
	}
}

