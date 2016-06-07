package stellarium.render.stellars.atmosphere;

import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.SimpleConfigHandler;

public class AtmosphereSettings extends SimpleConfigHandler {
	
	public static final String KEY = "atmosphere";

	/**
	 * Size of atmosphere cache texture
	 * */
	public int cacheLong = 1024, cacheLat = 512;
	
	/**
	 * Number of atmosphere fragments
	 * */
	public int fragLong = 256, fragLat = 128;
	
	private boolean isChanged = true;
	
	@Override
	public void setupConfig(Configuration config, String category) {
		super.setupConfig(config, category);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		
	}
	
	@Override
	public void saveToConfig(Configuration config, String category) {
		super.saveToConfig(config, category);
	}

	public boolean checkChange() {
		/*boolean ret = this.isChanged;
		this.isChanged = false;*/
		return true;
	}

}
