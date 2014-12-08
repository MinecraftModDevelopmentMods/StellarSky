package stellarium.stellars.cbody;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.*;
import sciapi.api.value.util.BOp;
import sciapi.api.value.util.VOp;
import stellarium.initials.CCertificateHelper;
import stellarium.lighting.CShade;
import stellarium.stellars.Color;
import stellarium.stellars.StellarManager;
import stellarium.stellars.local.LocalCValue;
import stellarium.util.math.*;
import stellarium.view.ViewPoint;
import stellarium.viewrender.render.RBase;
import stellarium.viewrender.render.RPointy;

public abstract class NonStarBody extends CBody {
	
	public static String PAlbedo = "Ad";
	
	public static String PColor = "Co";
	
	public void Construct(String[] ConBody){
		super.Construct(ConBody);
	}
	
	public void ConstructLoop(String[] ConBody){
		super.ConstructLoop(ConBody);
		if(ConBody[j].equals(PAlbedo)){
			j++;
			Albedo=Spmath.StrtoD(ConBody[j]);
		}
		if(ConBody[j].equals(PColor)){
			j++;
			color.r = (short)Spmath.StrtoI(ConBody[j]);
			j++;
			color.g = (short)Spmath.StrtoI(ConBody[j]);
			j++;
			color.b = (short)Spmath.StrtoI(ConBody[j]);
		}
	}
	
	public void Certificate(){
		ParStarBody=manager.Star;
		CCertificateHelper cch=null;
		if(this.Mass > cch.StarLimit)
			cch.Unstable(Name+"'s Mass is too big, it is going to be a Star!");
		if(this.Albedo > 1.0)
			cch.IllegalConfig(Name+"'s Albedo should be smaller than 1.0.");
	}
	
	
	StarBody ParStarBody;
	double Albedo;
	Color color;
	
	//Shades
	public ArrayList<CShade> shades;
	
	@SideOnly(Side.CLIENT)
	public ArrayList<CShade> shadesn;
	
	
	public void Initialize(){
		shades=new ArrayList<CShade>();
	}
	
	public void InitClient(){
		shades=new ArrayList<CShade>();
		shadesn=new ArrayList<CShade>();
	}

	@SideOnly(Side.SERVER)
	@Override
	public void Update(double yr) {
		super.Update(yr);
		UpdateShade();
	}
	
	
	protected void UpdateShade() {
		shades.clear();
		// TODO Auto-generated method stub
	}

	@Override
	@SideOnly(Side.SERVER)
	protected void UpdateLuminosity() {
		double dist=Spmath.getD(VecMath.size(VecMath.sub(theOrbit.Pos, ParStarBody.theOrbit.Pos)));
		Lum = ParStarBody.Lum / (4*dist*dist)*this.Radius*this.Radius;
	}
	
	@SideOnly(Side.CLIENT)
	public void UpdatePartial(double part){
		shadesn.clear();
		for(int i=0; i<shades.size(); i++)
			shadesn.add(shades.get(i).Get(part));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double GetMag() {
//		double Flux=Lum*GetPhase()/(theOrbit.Dist*theOrbit.Dist);
//		return -26.74-2.5*Math.log10(Flux);
		return 0.0;
	}

	@SideOnly(Side.CLIENT)
	public double GetPhase(){
		return (1.0+Spmath.getD(BOp.div(VecMath.dot(theOrbit.Pos, ParStarBody.theOrbit.Pos), BOp.mult(VecMath.size(ParStarBody.theOrbit.Pos), VecMath.size(ParStarBody.theOrbit.Pos)))))/2.0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void DrawImg() {
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public RBase RenderBody(LocalCValue lcv, double res) {
		if(res > lcv.Dist)
			return new RPointy().SetColor(color)
					.SetLum(Lum / (lcv.Dist * lcv.Dist))
					.SetPos(theOrbit.Pos);
		return null;
	}

}