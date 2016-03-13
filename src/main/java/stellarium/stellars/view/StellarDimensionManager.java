package stellarium.stellars.view;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import stellarium.stellars.StellarManager;

public class StellarDimensionManager extends WorldSavedData {
	
	public static String ID = "stellarskydimensiondata";
	
	private StellarManager manager;
	
	private PerDimensionSettings settings;
	private IStellarViewpoint viewpoint;
	
	public static StellarDimensionManager loadOrCreate(World world) {
		WorldSavedData data = world.perWorldStorage.loadData(StellarDimensionManager.class, ID);
		StellarDimensionManager dimManager;
		
		if(!(data instanceof StellarDimensionManager))
		{
			dimManager = new StellarDimensionManager(ID);
			world.mapStorage.setData(ID, dimManager);
			
			dimManager.loadSettingsFromConfig();
		} else dimManager = (StellarDimensionManager) data;
				
		return dimManager;
	}

	public static StellarDimensionManager get(World world) {
		return get(world.perWorldStorage);
	}

	public static StellarDimensionManager get(MapStorage mapStorage) {
		WorldSavedData data = mapStorage.loadData(StellarDimensionManager.class, ID);
		
		if(!(data instanceof StellarDimensionManager)) {
			throw new IllegalStateException(
					String.format("There is illegal data %s in storage!", data));
		}
		
		return (StellarDimensionManager)data;
	}

	public StellarDimensionManager(String id) {
		super(id);
	}
	
	private void loadSettingsFromConfig() {
		// TODO Auto-generated method stub
		
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
