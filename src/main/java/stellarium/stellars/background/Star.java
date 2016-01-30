package stellarium.stellars.background;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import stellarium.stellars.ExtinctionRefraction;
import stellarium.stellars.StellarObj;
import stellarium.util.math.Transforms;

public abstract class Star extends StellarObj{
	
	//Star Values
	byte star_value[];
	
	//Is It variable or not?
	boolean isVariable;
	
	//B-V Value
	public double B_V;
	
	//Apparant B-V
	public double app_B_V;

	
	public String name;
	
	public EVector pos = new EVector(3);
	
	/*
	 * Get star's position
	 * time is 'tick' unit
	 * world is false in Overworld, and true in Ender
	*/
	public IValRef<EVector> getPosition(){
		IValRef pvec=Transforms.ZTEctoNEc.transform((IEVector)EcRPos);
		pvec=Transforms.EctoEq.transform(pvec);
		pvec=Transforms.NEqtoREq.transform(pvec);
		pvec=Transforms.REqtoHor.transform(pvec);
		return pvec;
	}

	@Override
	public void update() {
		appPos.set(getAtmPos());
		double Airmass=ExtinctionRefraction.airmass(appPos, true);
    	appMag=mag+Airmass*ExtinctionRefraction.ext_coeff_V;
    	app_B_V=B_V+Airmass*ExtinctionRefraction.ext_coeff_B_V;
	}

	@Override
	abstract public void initialize();
	
}