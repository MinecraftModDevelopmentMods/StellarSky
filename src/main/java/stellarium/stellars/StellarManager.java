package stellarium.stellars;

import java.io.IOException;

import com.google.common.base.Throwables;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.common.CommonSettings;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.view.IStellarViewpoint;

public final class StellarManager extends WorldSavedData {
	
	private static final String ID = "stellarskymanagerdata";
		
	private CommonSettings settings;
	private CelestialManager celestialManager;
	private boolean locked = false;
	
	public StellarManager(String id) {
		super(id);
	}
	
	public static StellarManager loadOrCreateManager(World world) {
		if(!world.isRemote && StellarSky.proxy.getDefWorld(world.isRemote) != null)
			world = StellarSky.proxy.getDefWorld(world.isRemote);
		
		WorldSavedData data = world.mapStorage.loadData(StellarManager.class, ID);
		
		if(!(data instanceof StellarManager))
		{
			StellarManager manager = new StellarManager(ID);
			world.mapStorage.setData(ID, manager);
			
			manager.loadSettingsFromConfig();
			
			data = manager;
		}
				
		return (StellarManager) data;
	}
	
	public static boolean hasManager(World loadedWorld, boolean isRemote) {
		World world = isRemote? loadedWorld : StellarSky.proxy.getDefWorld(isRemote);
		if(world == null)
			return false;
		return (world.mapStorage.loadData(StellarManager.class, ID) instanceof StellarManager);
	}

	public static StellarManager getManager(boolean isRemote) {
		World world = StellarSky.proxy.getDefWorld(isRemote);
		WorldSavedData data = world.mapStorage.loadData(StellarManager.class, ID);
		
		if(!(data instanceof StellarManager)) {
			throw new IllegalStateException(
					String.format("There is illegal data %s in storage!", data));
		}
		
		return (StellarManager)data;
	}
	
	private void loadSettingsFromConfig() {
		this.settings = (CommonSettings) StellarSky.proxy.commonSettings.copy();
		this.markDirty();
	}
	
	public void handleServerWithoutMod() {
		settings.setDefault();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.syncFromNBT(compound, false);
	}

	public void syncFromNBT(NBTTagCompound compound, boolean isRemote) {
		this.locked = compound.getBoolean("locked");
		if(this.locked || isRemote)
		{
			this.settings = new CommonSettings();
			settings.readFromNBT(compound);
		} else {
			this.loadSettingsFromConfig();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("locked", this.locked);
		settings.writeToNBT(compound);
	}
	
	
	public void setup(CelestialManager manager) {
		StellarSky.logger.info("Starting Common Initialization...");
		this.celestialManager = manager;
		manager.initializeCommon(this.settings);
		StellarSky.logger.info("Common Initialization Ended.");
	}
	
	public CommonSettings getSettings() {
		return this.settings;
	}
	
	public CelestialManager getCelestialManager() {
		return this.celestialManager;
	}
	
	public double getSkyTime(double currentTick) {
		return currentTick + (settings.yearOffset * settings.year + settings.dayOffset)
				* settings.day + settings.tickOffset;
	}
	
	private boolean updated = false;
	
	public void update(double time){
		time = this.getSkyTime(time);
		celestialManager.update(time / settings.day / settings.year);
		this.updated = true;
	}
	
	public void updateClient(ClientSettings clientSettings, IStellarViewpoint renderViewpoint) {
		celestialManager.updateClient(clientSettings, renderViewpoint);
	}
	
	public boolean updated() {
		return this.updated;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
		this.markDirty();
	}

	public boolean isLocked() {
		return this.locked;
	}
}
