package stellarium.stellars;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.optics.IViewScope;
import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.common.CommonSettings;
import stellarium.stellars.layer.CelestialManager;

public final class StellarManager extends WorldSavedData {
	
	private static final String ID = "stellarskymanagerdata";
		
	private CommonSettings settings;
	private CelestialManager celestialManager;
	private boolean locked = false;
	private boolean setup = false;
	
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
		if(this.setup)
			return;
		
		StellarSky.logger.info("Starting Common Initialization...");
		this.celestialManager = manager;
		manager.initializeCommon(this.settings);
		StellarSky.logger.info("Common Initialization Ended.");
		
		StellarSky.logger.info("Starting Initial Update...");
		this.update(0.0);
		StellarSky.logger.info("Initial Update Ended.");
		
		this.setup = true;
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

	public CelestialPeriod getYearPeriod() {
		return new CelestialPeriod("Year", 2 * Math.PI / settings.day / settings.year,
				(settings.dayOffset + settings.tickOffset / settings.day) / settings.year);
	}
	
	
	public void update(double time){
		time = this.getSkyTime(time);
		celestialManager.update(time / settings.day / settings.year);
	}
	
	public void updateClient(ClientSettings clientSettings) {
		ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(StellarSky.proxy.getDefWorld());
		ISkyEffect sky = StellarAPIReference.getSkyEffect(StellarSky.proxy.getDefWorld());
		IViewScope scope = StellarAPIReference.getScope(StellarSky.proxy.getDefViewerEntity());
		
		celestialManager.updateClient(clientSettings, coordinate, sky, scope);
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
		this.markDirty();
	}

	public boolean isLocked() {
		return this.locked;
	}
}
