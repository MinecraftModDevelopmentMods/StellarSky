package stellarium.render.stellars.atmosphere;

import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.SimpleConfigHandler;

public class AtmosphereRenderSettings extends SimpleConfigHandler {

	/**
	 * Size of atmosphere cache texture
	 * */
	public int cacheLong, cacheLat;
	
	/**
	 * Number of atmosphere fragments
	 * */
	public int fragLong, fragLat;
	
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

}
