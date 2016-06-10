package stellarium.render.stellars.atmosphere;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleConfigHandler;
import stellarapi.api.lib.config.property.ConfigPropertyInteger;

public class AtmosphereSettings extends SimpleConfigHandler {
	
	public static final String KEY = "atmosphere";
	
	private ConfigPropertyInteger propCacheSizeLevel;
	private ConfigPropertyInteger propFragSize;

	/**
	 * Size of atmosphere cache texture
	 * */
	public int cacheLong = 1024, cacheLat = 512;
	
	/**
	 * Number of atmosphere fragments
	 * */
	public int fragLong = 256, fragLat = 128;
	
	private boolean isChanged = true;
	
	public AtmosphereSettings() {
		this.propCacheSizeLevel = new ConfigPropertyInteger("Atmosphere_Cache_Level", "", 9);
		this.propFragSize = new ConfigPropertyInteger("Atmosphere_Fragment_Number", "", 128);
	}
	
	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for the atmosphere accuracy.");
		config.setCategoryLanguageKey(category, "config.category.atmosphere");
		config.setCategoryRequiresMcRestart(category, false);
		
		super.setupConfig(config, category);
		
		propCacheSizeLevel.setComment("Level of atmosphere cache texture size. Less level will increase FPS, but the atmosphere will become EXPONENTIALLY defective.");
		propCacheSizeLevel.setRequiresWorldRestart(true);
		propCacheSizeLevel.setLanguageKey("config.property.atmosphere.cachelevel");
		propCacheSizeLevel.setMinValue(6);
		propCacheSizeLevel.setMaxValue(15);
       	
		propFragSize.setComment("Atmosphere is drawn with fragments\n" +
        		"Less fragments will increase FPS, but the atmosphere will become more defective.");
		propFragSize.setRequiresWorldRestart(true);
		propFragSize.setLanguageKey("config.property.atmosphere.fragnumber");
		propFragSize.setMinValue(8);
       	propFragSize.setMaxValue(512);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		
		int cacheTextureSize = 1 << propCacheSizeLevel.getInt();
		if(this.cacheLat != cacheTextureSize) {
			this.isChanged = true;
			this.cacheLat = cacheTextureSize;
			this.cacheLong = 2 * cacheTextureSize;
		}
		
		int fragNumber = propFragSize.getInt();
		if(this.fragLat != fragNumber) {
			this.isChanged = true;
			this.fragLat = fragNumber;
			this.fragLong = 2 * fragNumber;
		}
	}
	
	@Override
	public void saveToConfig(Configuration config, String category) {
		super.saveToConfig(config, category);
	}

	public boolean checkChange() {
		boolean ret = this.isChanged;
		this.isChanged = false;
		return true;
	}

}
