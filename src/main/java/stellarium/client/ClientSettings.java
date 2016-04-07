package stellarium.client;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.StellarSky;
import stellarium.config.HierarchicalConfig;
import stellarium.stellars.layer.CelestialLayerRegistry;

public class ClientSettings extends HierarchicalConfig {
	
	public float mag_Limit;
	public float milkywayBrightness;
	public int imgFrac, imgFracMilkyway;
	public float turb;
	public double minuteLength;
	public int anHourToMinute;
	
	private Property propMagLimit, propTurb, propMoonFrac;
	private Property propMilkywayFrac, propMilkywayBrightness;
	private Property propMinuteLength, propHourToMinute;
	private Property propViewMode;
	private Property propLockBtnPosition;
	
	private EnumViewMode viewMode = EnumViewMode.EMPTY;
	private EnumLockBtnPosition btnPosition = EnumLockBtnPosition.UPRIGHT;
	
	private boolean isDirty = false;
	
	public ClientSettings() {
		CelestialLayerRegistry.getInstance().composeSettings(this);
	}
	
	public void incrementViewMode() {
		this.viewMode = viewMode.nextMode();
		StellarSky.proxy.getCfgManager().syncFromFields();
	}
	
	public EnumViewMode getViewMode() {
		return this.viewMode;
	}
	
	public void setViewMode(EnumViewMode mode) {
		this.viewMode = mode;
	}
	
	public EnumLockBtnPosition getBtnPosition() {
		return this.btnPosition;
	}
	
	@Override
	public void setupConfig(Configuration config, String category) {
        config.setCategoryComment(category, "Configurations for client modifications.\n"
        		+ "Most of them are for rendering/view.");
        config.setCategoryLanguageKey(category, "config.category.client");
        config.setCategoryRequiresMcRestart(category, false);
		
        propMagLimit=config.get(category, "Mag_Limit", 4.0);
        propMagLimit.comment="Limit of magnitude can be seen on naked eye.\n" +
        		"If you want to increase FPS, lower the Mag_Limit.\n" +
        		"(Realistic = 6.5, Default = 4.0)\n" +
        		"The lower you set it, the fewer stars you will see\n" +
        		"but the better FPS you will get";
        propMagLimit.setRequiresMcRestart(true);
        propMagLimit.setLanguageKey("config.property.client.maglimit");
        
        propMilkywayBrightness=config.get(category, "Milkyway_Brightness", 1.5);
        propMilkywayBrightness.comment="Brightness of milky way.\n"
        		+ "For real world it should be 1.0 or lower, but default is set to 1.5 for visual effect.";
        propMilkywayBrightness.setRequiresMcRestart(false);
        propMilkywayBrightness.setLanguageKey("config.property.client.milkywaybrightness");

        propTurb=config.get(category, "Twinkling(Turbulance)", 1.0);
        propTurb.comment="Degree of the twinkling effect of star.\n"
        		+ "It determines the turbulance of atmosphere, which actually cause the twinkling effect. "
				+ "The greater the value, the more the stars will twinkle. Default is 1.0. To disable set to 0.0";
        propTurb.setRequiresMcRestart(false);
        propTurb.setLanguageKey("config.property.client.turbulance");
        
        propMoonFrac=config.get(category, "Moon_Fragments_Number", 16);
        propMoonFrac.comment="Moon is drawn with fragments\n" +
        		"Less fragments will increase FPS, but the moon will become more defective";
        propMoonFrac.setRequiresMcRestart(false);
        propMoonFrac.setLanguageKey("config.property.client.moonfrac");
        
        propMilkywayFrac=config.get(category, "Milkyway_Fragments_Number", 32);
        propMilkywayFrac.comment="Milky way is drawn with fragments\n" +
        		"Less fragments will increase FPS, but the milky way will become more defective";
        propMilkywayFrac.setRequiresMcRestart(false);
        propMilkywayFrac.setLanguageKey("config.property.client.milkywayfrac");
        
        propMinuteLength = config.get(category, "Minute_Length", 16.666);
        propMinuteLength.comment = "Number of ticks in a minute. (The minute & hour is displayed on HUD as HH:MM format)";
        propMinuteLength.setRequiresMcRestart(false);
        propMinuteLength.setLanguageKey("config.property.client.minutelength");
        
        propHourToMinute = config.get(category, "Hour_Length", 60);
        propHourToMinute.comment = "Number of minutes in an hour. (The minute & hour is displayed on HUD as HH:MM format)";
        propHourToMinute.setRequiresMcRestart(false);
        propHourToMinute.setLanguageKey("config.property.client.hourlength");
        
        propViewMode = config.get(category, "Mode_HUD_Time_View", viewMode.getName())
        		.setValidValues(EnumViewMode.names);
        propViewMode.comment = "Mode for HUD time view.\n"
        		+ " 3 modes available: empty, hhmm, tick.\n"
        		+ "Can also be changed in-game using key.";
        propViewMode.setRequiresMcRestart(false);
        propViewMode.setLanguageKey("config.property.client.modeview");
                
        
        propLockBtnPosition = config.get(category, "Lock_Button_Position", btnPosition.getName())
        		.setValidValues(EnumLockBtnPosition.names);
        propLockBtnPosition.comment = "Position of sky lock button.\n"
        		+ "Now there are upright and downleft.";
        propLockBtnPosition.setRequiresMcRestart(false);
        propLockBtnPosition.setLanguageKey("config.property.client.lockbtnpos"); 
        
        
        List<String> propNameList = Arrays.asList(propMagLimit.getName(),
        		propMoonFrac.getName(), propMilkywayFrac.getName(),
        		propTurb.getName(), propMilkywayBrightness.getName(),
        		propViewMode.getName(),
        		propMinuteLength.getName(), propHourToMinute.getName(),
        		propLockBtnPosition.getName());
        config.setCategoryPropertyOrder(category, propNameList);
        
        super.setupConfig(config, category);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.mag_Limit=(float)propMagLimit.getDouble();
        //Scaling
		this.turb=(float)propTurb.getDouble() * 4.0f;
		this.milkywayBrightness = (float) propMilkywayBrightness.getDouble();
        this.imgFrac=propMoonFrac.getInt();
        this.imgFracMilkyway = propMilkywayFrac.getInt();
        this.minuteLength = propMinuteLength.getDouble();
        this.anHourToMinute = propHourToMinute.getInt();
        
        this.setViewMode(EnumViewMode.getModeForName(propViewMode.getString()));
        this.btnPosition = EnumLockBtnPosition.getModeForName(propLockBtnPosition.getString());
        
        super.loadFromConfig(config, category);
        
        this.isDirty = true;
	}
	
	public boolean checkDirty() {
		boolean flag = this.isDirty;
		this.isDirty = false;
		return flag;
	}
	
}
