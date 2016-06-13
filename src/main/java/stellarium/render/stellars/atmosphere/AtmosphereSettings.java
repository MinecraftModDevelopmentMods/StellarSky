package stellarium.render.stellars.atmosphere;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleConfigHandler;
import stellarapi.api.lib.config.property.ConfigPropertyBoolean;
import stellarapi.api.lib.config.property.ConfigPropertyInteger;

public class AtmosphereSettings extends SimpleConfigHandler {
	
	public static final String KEY = "atmosphere";
	
	private ConfigPropertyInteger propCacheSizeLevel;
	private ConfigPropertyInteger propFragSize;
	private ConfigPropertyBoolean propInterpolation;

	/**
	 * Size of atmosphere cache texture
	 * */
	public int cacheLong = 1024, cacheLat = 512;
	
	/**
	 * Number of atmosphere fragments
	 * */
	public int fragLong = 256, fragLat = 128;
	
	public boolean isInterpolated = false;
	
	private boolean isChanged = true;
	
	public AtmosphereSettings() {
		this.propCacheSizeLevel = new ConfigPropertyInteger("Atmosphere_Cache_Level", "", 9);
		this.propFragSize = new ConfigPropertyInteger("Atmosphere_Fragment_Number", "", 128);
		this.propInterpolation = new ConfigPropertyBoolean("Atmosphere_Interpolation", "", false);

		this.addConfigProperty(this.propCacheSizeLevel);
		this.addConfigProperty(this.propFragSize);
		this.addConfigProperty(this.propInterpolation);
	}
	
	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for the atmosphere accuracy.");
		config.setCategoryLanguageKey(category, "config.category.atmosphere");
		config.setCategoryRequiresMcRestart(category, false);
		
		super.setupConfig(config, category);
		
		propCacheSizeLevel.setComment("Level of atmosphere cache texture size. Less level will increase FPS, but the atmosphere will become EXPONENTIALLY defective.");
		propCacheSizeLevel.setRequiresMcRestart(false);
		propCacheSizeLevel.setLanguageKey("config.property.atmosphere.cachelevel");
		propCacheSizeLevel.setMinValue(6);
		propCacheSizeLevel.setMaxValue(15);
       	
		propFragSize.setComment("Atmosphere is drawn with fragments\n" +
        		"Less fragments will increase FPS, but the atmosphere will become more defective.");
		propFragSize.setRequiresMcRestart(false);
		propFragSize.setLanguageKey("config.property.atmosphere.fragnumber");
		propFragSize.setMinValue(8);
       	propFragSize.setMaxValue(512);
       	
       	propInterpolation.setComment("Enabling this will interpolate texture for better view, but there could be some trivial issues like 'black line' with this.");
       	propInterpolation.setRequiresMcRestart(false);
       	propInterpolation.setLanguageKey("config.property.atmosphere.interpolation");
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
		
		boolean interpolated = propInterpolation.getBoolean();
		if(this.isInterpolated != interpolated) {
			this.isChanged = true;
			this.isInterpolated = interpolated;
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
