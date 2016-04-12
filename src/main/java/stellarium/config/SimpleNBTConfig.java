package stellarium.config;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import stellarium.config.property.ConfigProperty;

public abstract class SimpleNBTConfig extends SimpleConfigHandler implements INBTConfig {

	@Override
	public void setupConfig(Configuration config, String category) {
		super.setupConfig(config, category);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		super.saveToConfig(config, category);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		for(ConfigProperty property : this.listProperties)
			property.readFromNBT(compound);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		for(ConfigProperty property : this.listProperties)
			property.writeToNBT(compound);
	}

	@Override
	public abstract INBTConfig copy();
	
	protected void applyCopy(SimpleNBTConfig config) {
		for(ConfigProperty property : config.listProperties)
			property.setAsProperty(mapProperties.get(property.getConfigName()));
	}

}
