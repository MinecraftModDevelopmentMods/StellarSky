package stellarium.client;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleHierarchicalConfig;
import stellarapi.api.lib.config.property.ConfigPropertyDouble;
import stellarapi.api.lib.config.property.ConfigPropertyInteger;
import stellarapi.api.lib.config.property.ConfigPropertyString;
import stellarium.StellarSky;
import stellarium.stellars.layer.CelestialLayerRegistry;

public class ClientSettings extends SimpleHierarchicalConfig {
	
	public float mag_Limit;
	public double minuteLength;
	public int anHourToMinute;
	
	private ConfigPropertyDouble propMagLimit, propTurb;
	private ConfigPropertyDouble propMinuteLength;
	private ConfigPropertyInteger propHourToMinute;
	private ConfigPropertyString propViewMode;
	private ConfigPropertyString propLockBtnPosition;
	
	private EnumViewMode viewMode = EnumViewMode.EMPTY;
	private EnumLockBtnPosition btnPosition = EnumLockBtnPosition.UPRIGHT;
	
	private boolean isDirty = false;
	
	public ClientSettings() {
		CelestialLayerRegistry.getInstance().composeSettings(this);
		
		this.propMagLimit = new ConfigPropertyDouble("Mag_Limit", "", 4.0);
		this.propTurb = new ConfigPropertyDouble("Twinkling(Turbulance)", "", 1.0);
		this.propMinuteLength = new ConfigPropertyDouble("Minute_Length", "", 16.666);
		this.propHourToMinute = new ConfigPropertyInteger("Hour_Length", "", 60);
		this.propViewMode = new ConfigPropertyString("Mode_HUD_Time_View", "", viewMode.getName());
		this.propLockBtnPosition = new ConfigPropertyString("Lock_Button_Position", "", btnPosition.getName());
		
		this.addConfigProperty(this.propMagLimit);
		this.addConfigProperty(this.propTurb);
		this.addConfigProperty(this.propMinuteLength);
		this.addConfigProperty(this.propHourToMinute);
		this.addConfigProperty(this.propViewMode);
		this.addConfigProperty(this.propLockBtnPosition);
	}
	
	@Override
	public void setupConfig(Configuration config, String category) {
        config.setCategoryComment(category, "Configurations for client modifications.\n"
        		+ "Most of them are for rendering/view.");
        config.setCategoryLanguageKey(category, "config.category.client");
        config.setCategoryRequiresMcRestart(category, false);
		
        super.setupConfig(config, category);
        
        propMagLimit.setComment("Limit of magnitude can be seen on naked eye.\n" +
        		"If you want to increase FPS, lower the Mag_Limit.\n" +
        		"(Realistic = 6.5, Default = 4.0)\n" +
        		"The lower you set it, the fewer stars you will see\n" +
        		"but the better FPS you will get");
        propMagLimit.setRequiresMcRestart(true);
        propMagLimit.setLanguageKey("config.property.client.maglimit");

        propTurb.setComment("Degree of the twinkling effect of star.\n"
        		+ "It determines the turbulance of atmosphere, which actually cause the twinkling effect. "
				+ "The greater the value, the more the stars will twinkle. Default is 1.0. To disable set to 0.0");
        propTurb.setRequiresMcRestart(false);
        propTurb.setLanguageKey("config.property.client.turbulance");

        propMinuteLength.setComment("Number of ticks in a minute. (The minute & hour is displayed on HUD as HH:MM format)");
        propMinuteLength.setRequiresMcRestart(false);
        propMinuteLength.setLanguageKey("config.property.client.minutelength");        

        propHourToMinute.setComment("Number of minutes in an hour. (The minute & hour is displayed on HUD as HH:MM format)");
        propHourToMinute.setRequiresMcRestart(false);
        propHourToMinute.setLanguageKey("config.property.client.hourlength");

        propViewMode.setValidValues(EnumViewMode.names);
        propViewMode.setComment("Mode for HUD time view.\n"
        		+ " 3 modes available: empty, hhmm, tick.\n"
        		+ "Can also be changed in-game using key.");
        propViewMode.setRequiresMcRestart(false);
        propViewMode.setLanguageKey("config.property.client.modeview");
        
        propLockBtnPosition.setValidValues(EnumLockBtnPosition.names);
        propLockBtnPosition.setComment("Position of sky lock button.\n"
        		+ "Now there are upright and downleft.");
        propLockBtnPosition.setRequiresMcRestart(false);
        propLockBtnPosition.setLanguageKey("config.property.client.lockbtnpos");
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		
		this.mag_Limit=(float)propMagLimit.getDouble();
        this.minuteLength = propMinuteLength.getDouble();
        this.anHourToMinute = propHourToMinute.getInt();
        
        this.setViewMode(EnumViewMode.getModeForName(propViewMode.getString()));
        this.btnPosition = EnumLockBtnPosition.getModeForName(propLockBtnPosition.getString());
        
        this.isDirty = true;
	}
	
	@Override
	public void saveToConfig(Configuration config, String category) {
		propViewMode.setString(viewMode.getName());
		propLockBtnPosition.setString(btnPosition.getName());
		super.saveToConfig(config, category);
	}
	
	public double getTurbulance() {
		return propTurb.getDouble() * 4.0;
	}
	
	public boolean checkDirty() {
		boolean flag = this.isDirty;
		this.isDirty = false;
		return flag;
	}
	
	public void incrementViewMode() {
		this.setViewMode(viewMode.nextMode());
		StellarSky.proxy.getCfgManager().syncFromFields();
	}
	
	public EnumViewMode getViewMode() {
		return this.viewMode;
	}
	
	private void setViewMode(EnumViewMode mode) {
		this.viewMode = mode;
	}
	
	public EnumLockBtnPosition getBtnPosition() {
		return this.btnPosition;
	}
}
