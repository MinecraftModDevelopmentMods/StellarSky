package stellarium.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.config.SimpleHierarchicalNBTConfig;
import stellarapi.api.lib.config.property.ConfigPropertyBoolean;
import stellarapi.api.lib.config.property.ConfigPropertyDouble;
import stellarapi.api.lib.config.property.ConfigPropertyDoubleList;
import stellarapi.api.lib.config.property.ConfigPropertyString;
import stellarium.api.StellarSkyAPI;
import stellarium.stellars.OpticsHelper;

public class PerDimensionSettings extends SimpleHierarchicalNBTConfig {

	private String dimensionName;
	
	public double latitude, longitude;
	
	private ConfigPropertyBoolean propPatchProvider;

	private ConfigPropertyDouble propLatitude, propLongitude;
	
	private ConfigPropertyBoolean propHideObjectsUnderHorizon;
	
	private ConfigPropertyDouble propAtmScaleHeight;
	private ConfigPropertyDouble propAtmTotalHeight;
	private ConfigPropertyDouble propAtmHeightOffset;
	private ConfigPropertyDouble propAtmHeightIncScale;
	
	private ConfigPropertyDoubleList propAtmExtinctionFactor;
	
	private ConfigPropertyBoolean propAllowRefraction;
	private ConfigPropertyDouble propSunlightMultiplier;
	private ConfigPropertyDouble propSkyDispersionRate;
	private ConfigPropertyDouble propLightPollutionRate;
	private ConfigPropertyDouble propMinimumSkyRenderBrightness;
	
	private ConfigPropertyBoolean propLandscapeEnabled;
	
	private ConfigPropertyString propRenderType;
	
	public PerDimensionSettings(String dimensionName) {
		this.dimensionName = dimensionName;
		
		this.propPatchProvider = new ConfigPropertyBoolean("Patch_Provider", "patchProvider", true);
		
        String[] list = StellarSkyAPI.getRenderTypesForDimension(this.dimensionName);
		this.propRenderType = new ConfigPropertyString("Sky_Renderer_Type", "skyRendererType", list[0]);
		
		this.propLatitude = new ConfigPropertyDouble("Latitude", "lattitude", !dimensionName.equals("The End")? 37.5 : -52.5);
		this.propLongitude = new ConfigPropertyDouble("Longitude", "longitude", !dimensionName.equals("The End")? 0.0 : 180.0);
		
		this.propHideObjectsUnderHorizon = new ConfigPropertyBoolean("Hide_Objects_Under_Horizon", "hideObjectsUnderHorizon", !dimensionName.equals("The End"));
		
		this.propAtmScaleHeight = new ConfigPropertyDouble("Atmosphere_Scale_Height", "atmScaleHeight", 1 / 800.0);
		this.propAtmTotalHeight = new ConfigPropertyDouble("Atmosphere_Total_Height", "atmTotalHeight", 20.0 / 800.0);
		this.propAtmHeightOffset = new ConfigPropertyDouble("Atmosphere_Height_Offset", "atmHeightOffset", 0.2);
		this.propAtmHeightIncScale = new ConfigPropertyDouble("Atmosphere_Height_Increase_Scale", "atmHeightIncreaseScale", 1.0);

		this.propAtmExtinctionFactor = new ConfigPropertyDoubleList("Sky_Extinction_Factors", "skyExtinctionFactors",
				dimensionName.equals("The End")? new double[] {0.0, 0.0, 0.0} : new double[] {OpticsHelper.ext_coeff_R, OpticsHelper.ext_coeff_V, OpticsHelper.ext_coeff_B});
		
		this.propAllowRefraction = new ConfigPropertyBoolean("Allow_Atmospheric_Refraction", "allowRefraction", !dimensionName.equals("The End"));

		// TODO Fix Sunlight Multiplier Property
		this.propSunlightMultiplier = new ConfigPropertyDouble("SunLight_Multiplier", "sunlightMultiplier", 1.0);
       	
		this.propSkyDispersionRate = new ConfigPropertyDouble("Sky_Dispersion_Rate", "skyDispersionRate", dimensionName.equals("The End")? 0.0 : 1.0);
       	this.propLightPollutionRate = new ConfigPropertyDouble("Light_Pollution_Rate", "lightPollutionRate", 1.0);
       	this.propMinimumSkyRenderBrightness = new ConfigPropertyDouble("Minimum_Sky_Render_Brightness", "minimumSkyRenderBrightness", 0.2);
       	
       	this.propLandscapeEnabled = new ConfigPropertyBoolean("Landscape_Enabled", "landscapeEnabled", false);
       	
       	this.addConfigProperty(this.propPatchProvider);
       	this.addConfigProperty(this.propRenderType);
       	this.addConfigProperty(this.propLatitude);
       	this.addConfigProperty(this.propLongitude);
       	this.addConfigProperty(this.propHideObjectsUnderHorizon);
       	this.addConfigProperty(this.propAtmScaleHeight);
       	this.addConfigProperty(this.propAtmTotalHeight);
       	this.addConfigProperty(this.propAtmHeightOffset);
       	this.addConfigProperty(this.propAtmHeightIncScale);
       	this.addConfigProperty(this.propAtmExtinctionFactor);
       	this.addConfigProperty(this.propAllowRefraction);
       	this.addConfigProperty(this.propSunlightMultiplier);
       	this.addConfigProperty(this.propSkyDispersionRate);
       	this.addConfigProperty(this.propLightPollutionRate);
       	this.addConfigProperty(this.propMinimumSkyRenderBrightness);
       	this.addConfigProperty(this.propLandscapeEnabled);
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
        		+ "There are 'Overworld Sky', 'End Sky' and 'Skyblock Sky' type by default.");
        propRenderType.setRequiresWorldRestart(true);
        propRenderType.setLanguageKey("config.property.dimension.skyrenderertype");
        propRenderType.setValidValues(list);
		
       	propLatitude.setComment("Latitude on this world, in Degrees.");
       	propLatitude.setRequiresWorldRestart(true);
       	propLatitude.setLanguageKey("config.property.dimension.latitude");
       	propLatitude.setMaxValue(90.0);
       	propLatitude.setMinValue(-90.0);

       	propLongitude.setComment("Longitude on this world, in Degrees. (East is +)");
       	propLongitude.setRequiresWorldRestart(true);
       	propLongitude.setLanguageKey("config.property.dimension.longitude");
       	propLongitude.setMaxValue(360.0);
       	propLongitude.setMinValue(0.0);
        
        propHideObjectsUnderHorizon.setComment("Determine whether or not hide objects under horizon.");
        propHideObjectsUnderHorizon.setRequiresWorldRestart(true);
        propHideObjectsUnderHorizon.setLanguageKey("config.property.dimension.hidehorizonobj");
        
       	propAtmScaleHeight.setComment("Scale Height of the atmosphere relative to the radius.\n"
       			+ "This determines the thickness of the atmosphere.");
       	propAtmScaleHeight.setRequiresWorldRestart(true);
       	propAtmScaleHeight.setLanguageKey("config.property.dimension.atmscaleheight");
       	propAtmScaleHeight.setMinValue(1.0e-4);
       	propAtmScaleHeight.setMaxValue(1.0);
       	
       	propAtmTotalHeight.setComment("Total Height of the atmosphere relative to the radius.\n"
       			+ "This determines the accuracy of the atmosphere, relative to the scale height.");
       	propAtmTotalHeight.setRequiresWorldRestart(true);
       	propAtmTotalHeight.setLanguageKey("config.property.dimension.atmtotalheight");
       	propAtmTotalHeight.setMinValue(1.0e-4);
       	propAtmTotalHeight.setMaxValue(5.0);
       	
       	propAtmHeightOffset.setComment("Height on horizon in the Atmosphere, in Scale Height unit.");
       	propAtmHeightOffset.setRequiresWorldRestart(true);
       	propAtmHeightOffset.setLanguageKey("config.property.dimension.atmheightoffset");
       	propAtmHeightOffset.setMinValue(0.0);
       	propAtmHeightOffset.setMaxValue(100.0);
       	
       	propAtmHeightIncScale.setComment("Increase scale of height in the atmosphere, with Default 1.0.");
       	propAtmHeightIncScale.setRequiresWorldRestart(true);
       	propAtmHeightIncScale.setLanguageKey("config.property.dimension.atmheightincscale");
       	propAtmHeightIncScale.setMinValue(-1.0);
       	propAtmHeightIncScale.setMaxValue(10.0);
       	
       	propAtmExtinctionFactor.setComment("Extinction Factor for RVB(or RGB) of the atmosphere,"
       			+ "affects both sky rendering and extinction of stellar objects.");
       	propAtmExtinctionFactor.setRequiresWorldRestart(true);
       	propAtmExtinctionFactor.setLanguageKey("config.property.dimension.atmextinctionfactor");
       	propAtmExtinctionFactor.setIsListLengthFixed(true);
       	propAtmExtinctionFactor.setMaxListLength(3);
       	
        propAllowRefraction.setComment("Determine whether or not apply the atmospheric refraction.");
        propAllowRefraction.setRequiresWorldRestart(true);
        propAllowRefraction.setLanguageKey("config.property.dimension.allowrefraction");
        
        propSunlightMultiplier.setComment("Relative amount of sunlight on the dimension.\n"
        		+ "Setting this to 0.0 will make the world very dark.");
        propSunlightMultiplier.setRequiresWorldRestart(true);
        propSunlightMultiplier.setLanguageKey("config.property.dimension.sunlightmultiplier");
        propSunlightMultiplier.setMinValue(0.0);
        propSunlightMultiplier.setMaxValue(1.0);
        
        propSkyDispersionRate.setComment("Relative strength of sky dispersion on the dimension.\n"
        		+ "The effect is similar with sunlight multiplier on client, but usually don't affect the server, e.g. do not spawn mobs.");
        propSkyDispersionRate.setRequiresWorldRestart(true);
        propSkyDispersionRate.setLanguageKey("config.property.dimension.skydispersionrate");
        propSkyDispersionRate.setMinValue(0.0);
        propSkyDispersionRate.setMaxValue(1.0);
        
        propLightPollutionRate.setComment("Relative strength of light pollution on the dimension.\n"
        		+ "Only affects the sky color and visibility of stars/milky way.");
        propLightPollutionRate.setRequiresWorldRestart(true);
        propLightPollutionRate.setLanguageKey("config.property.dimension.lightpollutionrate");
        propLightPollutionRate.setMinValue(0.0);
        propLightPollutionRate.setMaxValue(1.0);
        
        propMinimumSkyRenderBrightness.setComment("Minimum brightness of skylight which (only) affects the rendering.");
        propMinimumSkyRenderBrightness.setRequiresWorldRestart(true);
        propMinimumSkyRenderBrightness.setLanguageKey("config.property.dimension.minimumskybrightness");
        propMinimumSkyRenderBrightness.setMinValue(0.0);
        propMinimumSkyRenderBrightness.setMaxValue(1.0);
        
        propLandscapeEnabled.setComment("Whether landscape rendering on this world is enabled.");
        propLandscapeEnabled.setRequiresWorldRestart(true);
        propLandscapeEnabled.setLanguageKey("config.property.dimension.enablelandscape");
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		
		if(!this.doesPatchProvider()) {
			propLatitude.setAsDefault();
			propLongitude.setAsDefault();
			propAtmExtinctionFactor.setAsDefault();
			propAllowRefraction.setAsDefault();
			propSunlightMultiplier.setAsDefault();
			propSkyDispersionRate.setAsDefault();
			propLightPollutionRate.setAsDefault();
			propMinimumSkyRenderBrightness.setAsDefault();
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
	
	public double getMinimumSkyRenderBrightness() {
		return propMinimumSkyRenderBrightness.getDouble();
	}
	
	public String getSkyRendererType() {
		return propRenderType.getString();
	}
	
	public boolean isLandscapeEnabled() {
		return propLandscapeEnabled.getBoolean();
	}
	
	public double getInnerRadius() {
		return 1.0 / propAtmScaleHeight.getDouble();
	}
	
	public double getOuterRadius() {
		return (1.0 + propAtmTotalHeight.getDouble()) / propAtmScaleHeight.getDouble();
	}
	
	public double getHeightOffset() {
		return propAtmHeightOffset.getDouble();
	}
	
	public double getHeightIncScale() {
		return propAtmHeightIncScale.getDouble();
	}
	
	public double[] extinctionRates() {
		return propAtmExtinctionFactor.getDoubleList();
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