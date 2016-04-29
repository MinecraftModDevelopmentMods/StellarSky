package stellarium;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class StellarSkyReferences {
	
	public static final String modid = "stellarsky";
	public static final String version = "@VERSION@";
	
	public static final String celestialSettings = "CelestialSettings.cfg";
	public static final String guiSettings = "GuiSettings.cfg";
	
	public static Configuration getConfiguration(File main, String sub) {
		return new Configuration(new File(new File(main, StellarSkyReferences.modid), sub));
	}

}
