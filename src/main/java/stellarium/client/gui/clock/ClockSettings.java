package stellarium.client.gui.clock;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.property.ConfigPropertyBoolean;
import stellarapi.api.lib.config.property.ConfigPropertyDouble;
import stellarapi.api.lib.config.property.ConfigPropertyString;
import stellarium.client.gui.PerOverlaySettings;

public class ClockSettings extends PerOverlaySettings {
	
	boolean isFixed = false;
	EnumViewMode viewMode = EnumViewMode.HHMM;
	float alpha = 0.5f;
	
	private ConfigPropertyBoolean propFixed;
	private ConfigPropertyString propViewMode;
	private ConfigPropertyDouble propAlpha;
	
	public ClockSettings() {
		this.propFixed = new ConfigPropertyBoolean("Fixed", "", this.isFixed);
		this.propViewMode = new ConfigPropertyString("Mode_HUD_Time_View", "", viewMode.getName());
		this.propAlpha = new ConfigPropertyDouble("Transparency", "", this.alpha);
		
		this.addConfigProperty(this.propFixed);
		this.addConfigProperty(this.propViewMode);
		this.addConfigProperty(this.propAlpha);
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Clock Overlay Settings.");
		config.setCategoryLanguageKey(category, "config.category.gui.clock");
		config.setCategoryRequiresMcRestart(category, false);
		
		super.setupConfig(config, category);
		
		propFixed.setComment("Determine if clock will be fixed on HUD or not.");
		propFixed.setRequiresMcRestart(false);
		propFixed.setLanguageKey("config.property.gui.clock.fixed");
		
		propViewMode.setValidValues(EnumViewMode.names);
		propViewMode.setComment("Mode for HUD time view. There are 'hhmm' and 'tick'");
		propViewMode.setRequiresMcRestart(false);
		propViewMode.setLanguageKey("config.property.gui.clock.viewmode");
		
		propAlpha.setComment("Determine transparency of the clock.");
		propAlpha.setRequiresMcRestart(false);
		propAlpha.setLanguageKey("config.property.gui.clock.alpha");
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		this.isFixed = propFixed.getBoolean();
		this.viewMode = EnumViewMode.getModeForName(propViewMode.getString());
		this.alpha = (float) propAlpha.getDouble();
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		propFixed.setBoolean(this.isFixed);
		propViewMode.setString(viewMode.getName());
		propAlpha.setDouble(this.alpha);
		super.saveToConfig(config, category);
	}
}
