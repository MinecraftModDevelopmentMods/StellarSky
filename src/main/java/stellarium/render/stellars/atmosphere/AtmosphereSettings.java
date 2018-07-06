package stellarium.render.stellars.atmosphere;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleConfigHandler;
import stellarapi.api.lib.config.property.ConfigPropertyBoolean;
import stellarapi.api.lib.config.property.ConfigPropertyInteger;

public class AtmosphereSettings extends SimpleConfigHandler {
	public static final String KEY = "atmosphere";

	private ConfigPropertyInteger propFragAtmSize;
	private ConfigPropertyInteger propFragScreenSize;
	private ConfigPropertyBoolean propInterpolation;

	/**
	 * Number of atmosphere fragments
	 * */
	public int fragLong = 256, fragLat = 128;

	/**
	 * Number of screen fragments
	 * */
	public int fragScreen = 64;

	public boolean isInterpolated = false;

	private boolean isChanged = true;

	public AtmosphereSettings() {
		this.propFragAtmSize = new ConfigPropertyInteger("Atmosphere_Fragment_Number", "", 128);
		this.propFragScreenSize = new ConfigPropertyInteger("Screen_Fragment_Number", "", 64);
		this.propInterpolation = new ConfigPropertyBoolean("Atmosphere_Interpolation", "", false);

		this.addConfigProperty(this.propFragAtmSize);
		this.addConfigProperty(this.propFragScreenSize);
		this.addConfigProperty(this.propInterpolation);
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for the atmosphere accuracy.");
		config.setCategoryLanguageKey(category, "config.category.atmosphere");
		config.setCategoryRequiresMcRestart(category, false);

		super.setupConfig(config, category);

		propFragAtmSize.setComment("Atmosphere is drawn with fragments\n" +
				"Less fragments will increase FPS, but the atmosphere will become more defective.");
		propFragAtmSize.setRequiresMcRestart(false);
		propFragAtmSize.setLanguageKey("config.property.atmosphere.fragnumber");
		propFragAtmSize.setMinValue(8);
		propFragAtmSize.setMaxValue(512);

		propFragScreenSize.setComment("Number of screen fragments which is used for atmosphere rendering.");
		propFragScreenSize.setRequiresMcRestart(false);
		propFragScreenSize.setLanguageKey("config.property.atmosphere.fragscreen");
		propFragScreenSize.setMinValue(8);
		propFragScreenSize.setMaxValue(256);

		propInterpolation.setComment("Enabling this will interpolate texture for better view, but there could be some trivial issues like 'black line' with this.");
		propInterpolation.setRequiresMcRestart(false);
		propInterpolation.setLanguageKey("config.property.atmosphere.interpolation");
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);

		int fragNumber = propFragAtmSize.getInt();
		if(this.fragLat != fragNumber) {
			this.isChanged = true;
			this.fragLat = fragNumber;
			this.fragLong = 2 * fragNumber;
		}

		int fragScreen = propFragScreenSize.getInt();
		if(this.fragScreen != fragScreen) {
			this.isChanged = true;
			this.fragScreen = fragScreen;
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
		return ret;
	}

}
