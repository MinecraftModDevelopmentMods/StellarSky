package stellarium.stellars;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import stellarapi.api.CelestialPeriod;
import stellarium.StellarSky;
import stellarium.common.ServerSettings;
import stellarium.stellars.layer.CelestialManager;

public final class StellarManager extends WorldSavedData {
	// TODO Remove most of StellarManager with configuration. Or, what's day length?
	private static final String ID = "stellarskymanagerdata";
		
	private ServerSettings settings;
	private CelestialManager celestialManager;
	private boolean locked = false, setup = false;
	
	public StellarManager(String id) {
		super(id);
	}

	public static @Nonnull StellarManager loadOrCreateManager(World world) {		
		WorldSavedData data = world.getMapStorage().getOrLoadData(StellarManager.class, ID);
		
		if(!(data instanceof StellarManager))
		{
			StellarManager manager = new StellarManager(ID);
			world.getMapStorage().setData(ID, manager);
			
			manager.loadSettingsFromConfig();
			
			data = manager;
		}

		return (StellarManager) data;
	}

	public static @Nonnull StellarManager getManager(World world) {
		WorldSavedData data = world.getMapStorage().getOrLoadData(StellarManager.class, ID);

		if(!(data instanceof StellarManager)) {
			throw new IllegalStateException(
					String.format("There is illegal data %s in storage!", data));
		}
		
		return (StellarManager)data;
	}

	
	private void loadSettingsFromConfig() {
		this.settings = (ServerSettings) StellarSky.PROXY.getServerSettings().copy();
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
			this.settings = new ServerSettings();
			settings.readFromNBT(compound);
		} else {
			this.loadSettingsFromConfig();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("locked", this.locked);
		settings.writeToNBT(compound);
		return compound;
	}


	public void setup(CelestialManager manager) {
		if(!this.setup) {
			StellarSky.INSTANCE.getLogger().info("Starting Common Initialization...");
			this.celestialManager = manager;
			StellarSky.PROXY.setupStellarLoad(this);
			manager.initializeCommon(this.settings);
			StellarSky.INSTANCE.getLogger().info("Common Initialization Ended.");
		}
		
		this.setup = true;
	}
	
	public ServerSettings getSettings() {
		return this.settings;
	}
	
	public CelestialManager getCelestialManager() {
		return this.celestialManager;
	}
	
	public double getSkyTime(double currentTick) {
		return currentTick + (settings.yearOffset * settings.year + settings.dayOffset)
				* settings.day + settings.tickOffset;
	}

	public CelestialPeriod getYearPeriod() {
		return new CelestialPeriod("Year", settings.day * settings.year,
				(settings.yearOffset * settings.year + settings.dayOffset)
				* settings.day + settings.tickOffset);
	}
	
	
	public void update(double time){
		time = this.getSkyTime(time);
		celestialManager.update(time / settings.day / settings.year);
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
		this.markDirty();
	}

	public boolean isLocked() {
		return this.locked;
	}

	public boolean hasSetup() {
		return this.setup;
	}

	public static boolean hasSetup(World world) {
		return StellarManager.getManager(world).hasSetup();
	}
}
