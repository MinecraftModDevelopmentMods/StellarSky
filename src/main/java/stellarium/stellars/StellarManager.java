package stellarium.stellars;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.common.ServerSettings;
import stellarium.stellars.layer.CelestialManager;

public final class StellarManager extends WorldSavedData {
	
	private static final String ID = "stellarskymanagerdata";
		
	private ServerSettings settings;
	private CelestialManager celestialManager;
	private boolean locked = false, setup = false;
	
	public StellarManager(String id) {
		super(id);
	}
	
	public static StellarManager loadOrCreateClientManager(World world) {
		return loadOrCreateManager(world);
	}
	
	public static StellarManager loadOrCreateServerManager(MinecraftServer server) {
		return loadOrCreateManager(server.getEntityWorld());
	}
	
	private static StellarManager loadOrCreateManager(World world) {		
		WorldSavedData data = world.getMapStorage().loadData(StellarManager.class, ID);
		
		if(!(data instanceof StellarManager))
		{
			StellarManager manager = new StellarManager(ID);
			world.getMapStorage().setData(ID, manager);
			
			manager.loadSettingsFromConfig();
			
			data = manager;
		}
				
		return (StellarManager) data;
	}
	
	public static boolean hasClientManager() {
		World world = StellarSky.proxy.getDefWorld();
		if(world == null)
			return false;
		return (world.getMapStorage().loadData(StellarManager.class, ID) instanceof StellarManager);
	}

	public static boolean hasServerManager(MinecraftServer server) {
		World world = server.getEntityWorld();
		if(world == null)
			return false;
		return (world.getMapStorage().loadData(StellarManager.class, ID) instanceof StellarManager);
	}

	public static StellarManager getClientManager() {
		World world = StellarSky.proxy.getDefWorld();
		return getManager(world);
	}
	
	public static StellarManager getServerManager(MinecraftServer server) {
		World world = server.getEntityWorld();
		return getManager(world);
	}
	
	private static StellarManager getManager(World world) {
		WorldSavedData data = world.getMapStorage().loadData(StellarManager.class, ID);
		
		if(!(data instanceof StellarManager)) {
			throw new IllegalStateException(
					String.format("There is illegal data %s in storage!", data));
		}
		
		return (StellarManager)data;
	}

	
	private void loadSettingsFromConfig() {
		this.settings = (ServerSettings) StellarSky.proxy.getServerSettings().copy();
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
	public void writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("locked", this.locked);
		settings.writeToNBT(compound);
	}
	
	
	public void setup(CelestialManager manager) {
		if(!this.setup) {
			StellarSky.logger.info("Starting Common Initialization...");
			this.celestialManager = manager;
			manager.initializeCommon(this.settings);
			StellarSky.logger.info("Common Initialization Ended.");
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
		IOpticalFilter filter = StellarAPIReference.getFilter(StellarSky.proxy.getDefViewerEntity());
		
		celestialManager.updateClient(clientSettings, coordinate, sky, scope, filter);
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
		if(world.isRemote)
			return StellarManager.getClientManager().hasSetup();
		else return StellarManager.getServerManager(MinecraftServer.getServer()).hasSetup();
	}
}
