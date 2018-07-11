package stellarium.stellars.star.brstar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.StellarSky;
import stellarium.StellarSkyReferences;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.star.BgStar;
import stellarium.stellars.star.LayerBgStar;
import stellarium.stellars.star.StarRenderCache;
import stellarium.util.math.StellarMath;

public class LayerBrStar extends LayerBgStar<IConfigHandler, INBTConfig> {

	//constants
	public static final int NumStar=9110;
	public static final int Bufsize=198;

	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Matrix3 EqtoEc = new Matrix3();
	
	static {
		EqtoEc.setAsRotation(1.0, 0.0, 0.0, -e);
	}

	//Initialization check
	private boolean IsInitialized=false;
	
	//File
	private byte str[];
	
	//stars
	private List<BgStar> stars = Lists.newArrayList();

	public LayerBrStar() {
		super(new ResourceLocation(StellarSkyReferences.MODID, "brstars"), 1);
	}

	@Override
	public void initializeClient(IConfigHandler config,
			StellarCollection<BgStar> container) throws IOException {
		this.loadStarData(StellarSky.PROXY.getClientSettings().mag_Limit);
	}

	@Override
	public void initializeCommon(INBTConfig config,
			StellarCollection<BgStar> container) throws IOException {
		if(!this.IsInitialized)
			this.loadStarData(4.0);
		
		for(BgStar star : this.stars)
		{
			container.loadObject("Star", star);
			container.addRenderCache(star, new StarRenderCache());
		}
	}
	
	private void loadStarData(double magLimit) throws IOException {
		//Counter Variable
		int i, j, k;
		
		StellarSky.INSTANCE.getLogger().info("Loading Bright Stars Data...");
		
		stars.clear();
		
		//Read
		str=new byte[NumStar*Bufsize];
		InputStream brs=BgStar.class.getResourceAsStream("/data/bsc5.dat");
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
			
			String name = new String(star_value).substring(4, 14);
			int number = StellarMath.btoi(star_value, 0, 5);
			
			double mag;
			
			double value = StellarMath.sgnize(star_value[102], StellarMath.btoD(star_value, 103, 4));

			if(star_value[107] == 'H')
			{
				mag = value;
				/*V = OpticsHelper.getMagFromMult(
						Wavelength.V.getWidth() / Wavelength.visible.getWidth()
						* OpticsHelper.getMultFromMag(mag));*/
			} else {
				mag = OpticsHelper.getMagFromMult(
						Wavelength.visible.getWidth() / Wavelength.V.getWidth()
						* OpticsHelper.getMultFromMag(value));
			}
			
			double B_V;
						
			if(star_value[110] != ' ')
				B_V=StellarMath.sgnize(star_value[109], StellarMath.btoD(star_value, 110, 4));
			else B_V = 0.4;
			

			//J2000
			double RA=StellarMath.btoi(star_value, 75, 2)*15.0f
					+StellarMath.btoi(star_value, 77, 2)/4.0f
					+StellarMath.btoi(star_value, 79, 2)/240.0f
					+StellarMath.btoi(star_value, 82, 1)/2400.0f;

			double Dec=StellarMath.sgnize(star_value[83],
					StellarMath.btoi(star_value, 84, 2)
					+StellarMath.btoi(star_value, 86, 2)/60.0f
					+StellarMath.btoi(star_value, 88, 2)/3600.0f);

			Vector3 pos = new SpCoord(RA, Dec).getVec();
			EqtoEc.transform(pos);

			if(mag > magLimit)
				continue;
			
			star_value=null;
			
	    	stars.add(new BgStar(name, number, mag, B_V, pos));
	    }
	    
	    str=null;
	    
	    StellarSky.INSTANCE.getLogger().info("Bright Stars are Loaded!");
	    
	    IsInitialized=true;
	}
}
