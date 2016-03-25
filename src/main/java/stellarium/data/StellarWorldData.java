package stellarium.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class StellarWorldData extends WorldSavedData {
	
	private static String ID = "stellarskyconfig";

	public StellarWorldData() {
		super(ID);
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

}
