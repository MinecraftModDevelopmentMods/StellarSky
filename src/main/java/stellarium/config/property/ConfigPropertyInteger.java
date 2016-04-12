package stellarium.config.property;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property.Type;

public class ConfigPropertyInteger extends ConfigProperty {
	
	private int defaultValue;
	private int currentValue;
	
	public ConfigPropertyInteger(String configKey, String dataKey, int defaultValue) {
		super(configKey, dataKey);
		this.currentValue = this.defaultValue = defaultValue;
	}
	
	public int getInt() {
		return this.currentValue;
	}
	
	public void setInt(int value) {
		this.currentValue = value;
	}
	
	@Override
	protected String getDefaultValue() {
		return Integer.toString(this.defaultValue);
	}
	
	@Override
	public void setAsDefault() {
		this.currentValue = this.defaultValue;
	}
	
	@Override
	public void setAsProperty(ConfigProperty property) {
		if(property instanceof ConfigPropertyInteger)
			this.currentValue = ((ConfigPropertyInteger) property).currentValue;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if(compound.hasKey(this.dataKey))
			this.currentValue = compound.getInteger(this.dataKey);
		else this.currentValue = this.defaultValue;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setInteger(this.dataKey, this.currentValue);
	}

	@Override
	public void loadFromConfig() {
		this.currentValue = property.getInt(this.defaultValue);
	}
	
	@Override
	public void saveToConfig() {
		property.set(this.currentValue);
	}

	@Override
	protected Type getType() {
		return Type.INTEGER;
	}
}
