package stellarium.client.gui.clock;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.property.ConfigPropertyBoolean;
import stellarapi.api.lib.config.property.ConfigPropertyString;
import stellarium.client.gui.PerOverlaySettings;

public class ClockSettings extends PerOverlaySettings {
	
	boolean isFixed = false;
	EnumViewMode viewMode = EnumViewMode.HHMM;
	
	private ConfigPropertyBoolean propFixed;
	private ConfigPropertyString propViewMode;
	
	public ClockSettings() {
		this.propFixed = new ConfigPropertyBoolean("Fixed", "", this.isFixed);
		this.propViewMode = new ConfigPropertyString("Mode_HUD_Time_View", "", viewMode.getName());
		
		this.addConfigProperty(this.propFixed);
		this.addConfigProperty(this.propViewMode);
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
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		this.isFixed = propFixed.getBoolean();
		this.viewMode = EnumViewMode.getModeForName(propViewMode.getString());
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		propFixed.setBoolean(this.isFixed);
		propViewMode.setString(viewMode.getName());
		super.saveToConfig(config, category);
	}
}
