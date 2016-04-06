package stellarium.stellars.system;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.config.HierarchicalNBTConfig;
import stellarium.config.INBTConfig;

public class SolarSystemSettings implements INBTConfig {
	
	public double moonSizeMultiplier, moonBrightnessMultiplier;
	
	private Property propMoonSize, propMoonBrightness;
	
	public SolarSystemSettings() { }
	
	public SolarSystemSettings(SolarSystemSettings settingsToCopy) {
		this.moonSizeMultiplier = settingsToCopy.moonSizeMultiplier;
		this.moonBrightnessMultiplier = settingsToCopy.moonBrightnessMultiplier;
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for solar system.");
		config.setCategoryLanguageKey(category, "config.category.solarsystem");
		config.setCategoryRequiresWorldRestart(category, true);
		
		List<String> propNameList = Lists.newArrayList();
        
       	propMoonSize = config.get(category, "Moon_Size", 1.0);
       	propMoonSize.setComment("Size of moon. (Default size is 1.0)");
       	propMoonSize.setRequiresWorldRestart(true);
       	propMoonSize.setLanguageKey("config.property.server.moonsize");
        propNameList.add(propMoonSize.getName());
        
       	propMoonBrightness = config.get(category, "Moon_Brightness", 1.0);
       	propMoonBrightness.setComment("Brightness of moon. (Default brightness is 1.0)");
       	propMoonBrightness.setRequiresWorldRestart(true);
       	propMoonBrightness.setLanguageKey("config.property.server.moonbrightness");
        propNameList.add(propMoonBrightness.getName());
        
        config.setCategoryPropertyOrder(category, propNameList);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
       	this.moonSizeMultiplier = propMoonSize.getDouble();
       	this.moonBrightnessMultiplier = propMoonBrightness.getDouble();
	}

	
	public void readFromNBT(NBTTagCompound compound) {
       	this.moonSizeMultiplier = compound.getDouble("moonSize");
       	this.moonBrightnessMultiplier = compound.getDouble("moonBrightness");
	}

	
	public void writeToNBT(NBTTagCompound compound) {
       	compound.setDouble("moonSize", this.moonSizeMultiplier);
       	compound.setDouble("moonBrightness", this.moonBrightnessMultiplier);
	}

	@Override
	public INBTConfig copy() {
		return new SolarSystemSettings(this);
	}

}
