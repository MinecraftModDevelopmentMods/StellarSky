package stellarium.stellars.view;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.config.IConfigHandler;

public class PerDimensionSettings implements IConfigHandler {

	public double latitude, longitude;
	public boolean patchProvider;
	public boolean hideObjectsUnderHorizon;
	
	private Property propLattitude, propLongitude;
	private Property propPatchProvider;
	private Property propHideObjectsUnderHorizon;


	@Override
	public void setupConfig(Configuration config, String category) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		// TODO Auto-generated method stub
		
	}

}
