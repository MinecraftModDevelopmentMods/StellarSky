package stellarium.client.overlay.clock;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.gui.overlay.PerOverlaySettings;
import stellarapi.api.lib.config.property.ConfigPropertyBoolean;
import stellarapi.api.lib.config.property.ConfigPropertyDouble;
import stellarapi.api.lib.config.property.ConfigPropertyInteger;
import stellarapi.api.lib.config.property.ConfigPropertyString;

public class ClockSettings extends PerOverlaySettings {
	
	boolean isFixed = true;
	EnumViewMode viewMode = EnumViewMode.HHMM;
	float alpha = 0.5f;
	boolean textShadow = false;
	double daylengthToHour = 24.0;
	int startingYear = 1;
	int dateOffset = 0;
	
	float wordHue = 0.0f;
	float wordBrightness = 0.7f;
	float wordSaturation = 0.0f;
	
	private ConfigPropertyBoolean propFixed;
	private ConfigPropertyString propViewMode;
	private ConfigPropertyDouble propAlpha;
	private ConfigPropertyDouble propWordBrightness;
	private ConfigPropertyDouble propWordHue;
	private ConfigPropertyDouble propWordSaturation;
	private ConfigPropertyBoolean propTextShadow;
	
	private ConfigPropertyDouble propDaylengthInHour;
	private ConfigPropertyInteger propStartingYear, propClockDateOffset;

	public ClockSettings() {
		this.propFixed = new ConfigPropertyBoolean("Fixed", "", this.isFixed);
		this.propViewMode = new ConfigPropertyString("Mode_HUD_Time_View", "", viewMode.getName());
		this.propAlpha = new ConfigPropertyDouble("Transparency", "", this.alpha);
		this.propTextShadow = new ConfigPropertyBoolean("Shadow", "", this.textShadow);
		this.propWordBrightness = new ConfigPropertyDouble("Brightness_Words", "", this.wordBrightness);
		this.propWordHue = new ConfigPropertyDouble("Hue_Words", "", this.wordBrightness);
		this.propWordSaturation = new ConfigPropertyDouble("Saturation_Words", "", this.wordBrightness);
		
		this.propDaylengthInHour = new ConfigPropertyDouble("Daylength_In_Hour", "", this.daylengthToHour);
        this.propStartingYear = new ConfigPropertyInteger("Starting_Year", "", 1);
        this.propClockDateOffset = new ConfigPropertyInteger("Clock_Date_Offset", "", 0);
		
		this.addConfigProperty(this.propFixed);
		this.addConfigProperty(this.propViewMode);
		this.addConfigProperty(this.propTextShadow);
		this.addConfigProperty(this.propAlpha);
		
		this.addConfigProperty(this.propWordBrightness);
		this.addConfigProperty(this.propWordHue);
		this.addConfigProperty(this.propWordSaturation);
		
		this.addConfigProperty(this.propDaylengthInHour);

       	this.addConfigProperty(this.propStartingYear);
       	this.addConfigProperty(this.propClockDateOffset);
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Clock Overlay Settings.");
		config.setCategoryLanguageKey(category, "config.category.gui.clock");
		config.setCategoryRequiresMcRestart(category, false);
		
		super.setupConfig(config, category);
		
		propFixed.setComment("Determine if clock will be fixed on HUD or not.");
		propFixed.setRequiresMcRestart(false);
		//propFixed.setLanguageKey("config.property.gui.clock.fixed");
		
		propViewMode.setValidValues(EnumViewMode.names);
		propViewMode.setComment("Mode for HUD time view. There are 'hhmm' and 'tick'");
		propViewMode.setRequiresMcRestart(false);
		//propViewMode.setLanguageKey("config.property.gui.clock.viewmode");
		
		propAlpha.setComment("Determine transparency of the clock.");
		propAlpha.setRequiresMcRestart(false);
		//propAlpha.setLanguageKey("config.property.gui.clock.alpha");
		
		propWordBrightness.setComment("Determine brightness of the words.");
		propWordBrightness.setRequiresMcRestart(false);
		
		propWordHue.setComment("Determine hue of the words.");
		propWordHue.setRequiresMcRestart(false);
		
		propWordSaturation.setComment("Determine saturation of the words.");
		propWordSaturation.setRequiresMcRestart(false);
		
		propTextShadow.setComment("Determine if the texts will be shadowed.");
		propTextShadow.setRequiresMcRestart(false);
		//propTextShadow.setLanguageKey("");
		
		
		propDaylengthInHour.setComment("Day length in hour, 1 day = 24 hour by default.");
		propDaylengthInHour.setRequiresMcRestart(false);
        //propMinuteLength.setLanguageKey("config.property.client.minutelength");
		
       	propStartingYear.setComment("Starting year displayed on the clock.");
       	propStartingYear.setRequiresMcRestart(false);

       	propClockDateOffset.setComment("Offset on the displayed date. Control displayed date with this cfg option.");
       	propClockDateOffset.setRequiresMcRestart(false);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		this.isFixed = propFixed.getBoolean();
		this.viewMode = EnumViewMode.getModeForName(propViewMode.getString());
		this.alpha = (float) propAlpha.getDouble();
		this.textShadow = propTextShadow.getBoolean();
		
		this.wordBrightness = (float) propWordBrightness.getDouble();
		this.wordHue = (float) propWordHue.getDouble();
		this.wordSaturation = (float) propWordSaturation.getDouble();
		
		this.daylengthToHour = propDaylengthInHour.getDouble();
		this.startingYear = propStartingYear.getInt();
		this.dateOffset = propClockDateOffset.getInt();
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		propFixed.setBoolean(this.isFixed);
		propViewMode.setString(viewMode.getName());
		propAlpha.setDouble(this.alpha);
		propTextShadow.setBoolean(this.textShadow);
		
		propWordBrightness.setDouble(this.wordBrightness);
		propWordHue.setDouble(this.wordHue);
		propWordSaturation.setDouble(this.wordSaturation);
		
		propDaylengthInHour.setDouble(this.daylengthToHour);
		propStartingYear.setInt(this.startingYear);
		propClockDateOffset.setInt(this.dateOffset);
		
		super.saveToConfig(config, category);
	}
}
