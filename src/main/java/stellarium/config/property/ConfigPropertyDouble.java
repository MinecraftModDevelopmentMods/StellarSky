package stellarium.config.property;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property.Type;

public class ConfigPropertyDouble extends ConfigProperty {
	
	private double defaultValue;
	private double currentValue;
	
	public ConfigPropertyDouble(String configKey, String dataKey, double defaultValue) {
		super(configKey, dataKey);
		this.currentValue = this.defaultValue = defaultValue;
	}
	
	public double getDouble() {
		return this.currentValue;
	}
	
	public void setDouble(double value) {
		this.currentValue = value;
	}
	
	@Override
	protected String getDefaultValue() {
		return Double.toString(this.defaultValue);
	}
	
	@Override
	public void setAsDefault() {
		this.currentValue = this.defaultValue;
	}
	
	@Override
	public void setAsProperty(ConfigProperty property) {
		if(property instanceof ConfigPropertyDouble)
			this.currentValue = ((ConfigPropertyDouble) property).currentValue;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if(compound.hasKey(this.dataKey))
			this.currentValue = compound.getDouble(this.dataKey);
		else this.currentValue = this.defaultValue;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setDouble(this.dataKey, this.currentValue);
	}

	@Override
	public void loadFromConfig() {
		this.currentValue = property.getDouble(this.defaultValue);
	}
	
	@Override
	public void saveToConfig() {
		property.set(this.currentValue);
	}

	@Override
	protected Type getType() {
		return Type.DOUBLE;
	}
}
