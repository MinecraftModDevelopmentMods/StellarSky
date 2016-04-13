package stellarium.stellars.view;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import stellarium.api.StellarSkyAPI;
import stellarium.config.INBTConfig;
import stellarium.config.SimpleHierarchicalNBTConfig;
import stellarium.config.property.ConfigPropertyBoolean;
import stellarium.config.property.ConfigPropertyDouble;
import stellarium.config.property.ConfigPropertyString;

public class PerDimensionSettings extends SimpleHierarchicalNBTConfig {

	private String dimensionName;
	
	public double latitude, longitude;
	
	private ConfigPropertyDouble propLatitude, propLongitude;
	private ConfigPropertyBoolean propPatchProvider;
	private ConfigPropertyBoolean propHideObjectsUnderHorizon;
	private ConfigPropertyBoolean propAllowRefraction;
	private ConfigPropertyDouble propSunlightMultiplier;
	private ConfigPropertyDouble propSkyDispersionRate;
	private ConfigPropertyDouble propLightPollutionRate;
	private ConfigPropertyString propRenderType;
	
	public PerDimensionResourceSettings resourceSettings;

	public PerDimensionSettings(String dimensionName) {
		this.dimensionName = dimensionName;
		
		this.propPatchProvider = new ConfigPropertyBoolean("Patch_Provider", "patchProvider", true);
		
        String[] list = StellarSkyAPI.getRenderTypesForDimension(this.dimensionName);
		this.propRenderType = new ConfigPropertyString("Sky_Renderer_Type", "skyRendererType", list[0]);
		
		this.propLatitude = new ConfigPropertyDouble("Latitude", "lattitude", !dimensionName.equals("The End")? 37.5 : -52.5);
		this.propLongitude = new ConfigPropertyDouble("Longitude", "longitude", !dimensionName.equals("The End")? 0.0 : 180.0);
		this.propHideObjectsUnderHorizon = new ConfigPropertyBoolean("Hide_Objects_Under_Horizon", "hideObjectsUnderHorizon", !dimensionName.equals("The End"));
		this.propAllowRefraction = new ConfigPropertyBoolean("Allow_Atmospheric_Refraction", "allowRefraction", !dimensionName.equals("The End"));
       	this.propSunlightMultiplier = new ConfigPropertyDouble("SunLight_Multiplier", "sunlightMultiplier", 1.0);
       	this.propSkyDispersionRate = new ConfigPropertyDouble("Sky_Dispersion_Rate", "skyDispersionRate", 1.0);
       	this.propLightPollutionRate = new ConfigPropertyDouble("Light_Pollution_Rate", "lightPollutionRate", 1.0);
       	
       	this.addConfigProperty(this.propPatchProvider);
       	this.addConfigProperty(this.propRenderType);
       	this.addConfigProperty(this.propLatitude);
       	this.addConfigProperty(this.propLongitude);
       	this.addConfigProperty(this.propHideObjectsUnderHorizon);
       	this.addConfigProperty(this.propAllowRefraction);
       	this.addConfigProperty(this.propSunlightMultiplier);
       	this.addConfigProperty(this.propSkyDispersionRate);
       	this.addConfigProperty(this.propLightPollutionRate);
       	
       	this.putSubConfig("ResourceSettings", this.resourceSettings = new PerDimensionResourceSettings());
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for this dimension.");
		config.setCategoryRequiresWorldRestart(category, true);
		
		super.setupConfig(config, category);
		
		propPatchProvider.setComment("Determine whether or not patch provider. Cannot adjust longitude and latitude when this is false.");
		propPatchProvider.setRequiresWorldRestart(true);
		propPatchProvider.setLanguageKey("config.property.dimension.patchprovider");
        
        String[] list = StellarSkyAPI.getRenderTypesForDimension(this.dimensionName);
        propRenderType.setComment("Sky renderer type for this dimension.\n"
        		+ "There are 'Overworld Sky' and 'End Sky' type by default.");
        propRenderType.setRequiresWorldRestart(true);
        propRenderType.setLanguageKey("config.property.dimension.skyrenderertype");
        propRenderType.setValidValues(list);
		
       	propLatitude.setComment("Latitude on this world, in Degrees.");
       	propLatitude.setRequiresWorldRestart(true);
       	propLatitude.setLanguageKey("config.property.dimension.latitude");

       	propLongitude.setComment("Longitude on this world, in Degrees. (East is +)");
       	propLongitude.setRequiresWorldRestart(true);
       	propLongitude.setLanguageKey("config.property.dimension.longitude");
        
        propHideObjectsUnderHorizon.setComment("Determine whether or not hide objects under horizon.");
        propHideObjectsUnderHorizon.setRequiresWorldRestart(true);
        propHideObjectsUnderHorizon.setLanguageKey("config.property.dimension.hidehorizonobj");
        
        propAllowRefraction.setComment("Determine whether or not apply the atmospheric refraction.");
        propAllowRefraction.setRequiresWorldRestart(true);
        propAllowRefraction.setLanguageKey("config.property.dimension.allowrefraction");
        
        propSunlightMultiplier.setComment("Relative amount of sunlight on the dimension.\n"
        		+ "Setting this to 0.0 will make the world very dark.");
        propSunlightMultiplier.setRequiresWorldRestart(true);
        propSunlightMultiplier.setLanguageKey("config.property.dimension.sunlightmultiplier");
        
        propSkyDispersionRate.setComment("Relative strength of sky dispersion on the dimension.\n"
        		+ "The effect is similar with sunlight multiplier on client, but usually don't affect the server, e.g. do not spawn mobs.");
        propSkyDispersionRate.setRequiresWorldRestart(true);
        propSkyDispersionRate.setLanguageKey("config.property.dimension.skydispersionrate");
	
        propLightPollutionRate.setComment("Relative strength of light pollution on the dimension.\n"
        		+ "Only affects the sky color and visibility of stars/milky way.");
        propLightPollutionRate.setRequiresWorldRestart(true);
        propLightPollutionRate.setLanguageKey("config.property.dimension.lightpollutionrate");
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		
		if(!this.doesPatchProvider())
		{
			propLatitude.setAsDefault();
			propLongitude.setAsDefault();
			propAllowRefraction.setAsDefault();
			propSunlightMultiplier.setAsDefault();
		}
       	
       	this.latitude = propLatitude.getDouble();
   		this.longitude = propLongitude.getDouble();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

       	this.latitude = propLatitude.getDouble();
   		this.longitude = propLongitude.getDouble();
	}

	
	public boolean doesPatchProvider() {
		return propPatchProvider.getBoolean();
	}
	
	public boolean allowRefraction() {
		return propAllowRefraction.getBoolean();
	}
	
	public boolean hideObjectsUnderHorizon() {
		return propHideObjectsUnderHorizon.getBoolean();
	}
	
	public double getSunlightMultiplier() {
		return propSunlightMultiplier.getDouble();
	}
	
	public double getSkyDispersionRate() {
		return propSkyDispersionRate.getDouble();
	}
	
	public double getLightPollutionRate() {
		return propLightPollutionRate.getDouble();
	}
	
	public String getSkyRendererType() {
		return propRenderType.getString();
	}

	@Override
	public INBTConfig copy() {
		PerDimensionSettings settings = new PerDimensionSettings(this.dimensionName);
		this.applyCopy(settings);
		settings.latitude = this.latitude;
		settings.longitude = this.longitude;
		return settings;
	}

}