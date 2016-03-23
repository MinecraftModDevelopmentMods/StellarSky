package stellarium.stellars.star.brstar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.common.collect.Lists;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.StellarSky;
import stellarium.stellars.background.rebase.BrStar;
import stellarium.stellars.sketch.CelestialObject;
import stellarium.stellars.star.BgStar;
import stellarium.stellars.star.LayerBgStar;
import stellarium.util.math.Rotate;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;

public class LayerBrStar extends LayerBgStar {
	
	//constants
	public static final int NumStar=9110;
	public static final int Bufsize=198;

	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Rotate EqtoEc = new Rotate('X').setRAngle(-e); 

	//Initialization check
	private boolean IsInitialized=false;
	
	//File
	private byte str[];
	
	//stars
	private List<BgStar> stars = Lists.newArrayList();

	@Override
	public List<? extends CelestialObject> getObjectList() {
		return this.stars;
	}
	
	@Override
	public void initialize(boolean isRemote) throws IOException {
		
		//Counter Variable
		int i, j, k;
		
		System.out.println("[Stellarium]: "+"Loading Bright Stars Data...");
		
		//Read
		str=new byte[NumStar*Bufsize];
		InputStream brs=BrStar.class.getResourceAsStream("/data/bsc5.dat");
	    BufferedInputStream bbrs = new BufferedInputStream(brs);
	    bbrs.read(str);
	    bbrs.close();
	    
	    //Input Star Information
	    j=0;
	    for(i=0; i<NumStar; i++)
	    {
	    	byte[] star_value = new byte[Bufsize];
	    	
	    	k=0;
	    	while(str[j]!='\n'){
	    		star_value[k]=str[j];
	    		j++;
	    		k++;
	    	}
	    	j++;
	    	

			if(star_value[103]==' ')
				continue;
			
			double mag=Spmath.sgnize(star_value[102],
					(float)Spmath.btoi(star_value, 103, 1)
					+Spmath.btoi(star_value, 105, 2)*0.01f);

			double B_V=Spmath.sgnize(star_value[109],
					(float)Spmath.btoi(star_value, 110, 1)
					+Spmath.btoi(star_value, 112, 2)*0.01f);

			//J2000
			double RA=Spmath.btoi(star_value, 75, 2)*15.0f
					+Spmath.btoi(star_value, 77, 2)/4.0f
					+Spmath.btoi(star_value, 79, 2)/240.0f
					+Spmath.btoi(star_value, 82, 1)/2400.0f;

			double Dec=Spmath.sgnize(star_value[83],
					Spmath.btoi(star_value, 84, 2)
					+Spmath.btoi(star_value, 86, 2)/60.0f
					+Spmath.btoi(star_value, 88, 2)/3600.0f);

			EVector pos = new EVector(3).set(EqtoEc.transform((IValRef)new SpCoord(RA, Dec).getVec()));

			if(mag > StellarSky.proxy.getClientSettings().mag_Limit)
				continue;
			
			star_value=null;
			
	    	stars.add(new BgStar(isRemote, mag, B_V, pos));
	    }
	    
	    str=null;
	    
	    System.out.println("[Stellarium]: "+"Bright Stars are Loaded!");
	    IsInitialized=true;
	}

	@Override
	public boolean existOnServer() {
		return false;
	}

}
