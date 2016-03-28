package stellarium.config;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTSerializable {
	
	public void readFromNBT(NBTTagCompound compound);
	public void writeToNBT(NBTTagCompound compound);

}
