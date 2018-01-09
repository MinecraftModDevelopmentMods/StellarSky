package stellarium;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class StellarSkyReferences {
	
	public static final String MODID = "stellarsky";
	public static final String VERSION = "@VERSION@";
	
	public static final String CELESTIAL_SETTINGS = "CelestialSettings.cfg";
	public static final String GUI_SETTINGS = "GuiSettings.cfg";

	// MAYBE Change the resource id. In case Stellarium becomes a thing.
	public static final String RESOURCE_ID = "stellarium";
	
	public static Configuration getConfiguration(File main, String sub) {
		return new Configuration(new File(new File(main, StellarSkyReferences.MODID), sub));
	}

}
