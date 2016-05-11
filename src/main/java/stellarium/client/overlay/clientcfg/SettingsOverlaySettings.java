package stellarium.client.overlay.clientcfg;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.gui.overlay.PerOverlaySettings;
import stellarapi.api.lib.config.property.ConfigPropertyBoolean;

/**Naming is somewhat screwed ={*/
public class SettingsOverlaySettings extends PerOverlaySettings {
	
	boolean isFixed = false;

	private ConfigPropertyBoolean propFixed;
	
	public SettingsOverlaySettings() {
		this.propFixed = new ConfigPropertyBoolean("Fixed", "", this.isFixed);
		
		this.addConfigProperty(this.propFixed);
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Clock Overlay Settings.");
		config.setCategoryLanguageKey(category, "config.category.gui.clock");
		config.setCategoryRequiresMcRestart(category, false);
		
		super.setupConfig(config, category);
		
		propFixed.setComment("Determine if client settings will be fixed on HUD or not.");
		propFixed.setRequiresMcRestart(false);
		//propFixed.setLanguageKey("config.property.gui.clock.fixed");
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		this.isFixed = propFixed.getBoolean();
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		propFixed.setBoolean(this.isFixed);
		
		super.saveToConfig(config, category);
	}
}
