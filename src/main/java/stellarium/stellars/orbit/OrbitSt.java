package stellarium.stellars.orbit;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.initials.CConstructReader.CProperty;
import stellarium.initials.CTranslateManager;
import stellarium.stellars.StellarManager;
import stellarium.stellars.cbody.CBody;
import stellarium.util.math.*;

public class OrbitSt extends Orbit {
	
	public static final String PPos= "P";

	
	public void RegisterOrbit(){
		CTranslateManager.AddTranslation("Stationary", "S");
		
		CTranslateManager.AddTranslation("Position", PPos);
	}


	protected CProperty PrPos;
	protected double Di = 0.0, Lo = 0.0, La = 0.0;

	@Override
	public void PreConstruct(){
		super.PreConstruct();
		PrPos = conreader.addProperty(PPos, 3);
		PrPos.addModeforRead(virbody, "E");
	}
	
	@Override
	public void PostConstruct(){
		super.PostConstruct();
		
		String[] SPos = conreader.ReadValue(PrPos);
		
		Di = Spmath.StrtoD(SPos[0]);
		Lo = Spmath.StrtoD(SPos[1]);
		La = Spmath.StrtoD(SPos[2]);
		
		Pos.set(VecMath.mult(Di, new SpCoord(Lo, La).getVec()));
	}
	
	
	
	@SideOnly(Side.SERVER)
	@Override
	public void Update(double yr) {
		super.Update(yr);
	}

}
