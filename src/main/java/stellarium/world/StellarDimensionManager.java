package stellarium.world;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.INBTConfig;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.world.landscape.LandscapeCache;

public final class StellarDimensionManager extends WorldSavedData {
	
	public static String ID = "stellarskydimension%s";
	
	private StellarManager manager;
	
	private PerDimensionSettings settings;
	private IStellarSkySet skyset;
	private StellarCoordinate coordinate;
	private List<StellarCollection> collections = Lists.newArrayList();
	private List<ICelestialObject> foundSuns = Lists.newArrayList();
	private List<ICelestialObject> foundMoons = Lists.newArrayList();
	
	private String dimensionName;
	
	private LandscapeCache landscapeCache;
	
	public static StellarDimensionManager loadOrCreate(World world, StellarManager manager, String dimName) {
		WorldSavedData data = world.getPerWorldStorage().loadData(StellarDimensionManager.class, String.format(ID, dimName));
		StellarDimensionManager dimManager;
		
		if(!(data instanceof StellarDimensionManager))
		{
			dimManager = new StellarDimensionManager(String.format(ID, dimName));
			world.getPerWorldStorage().setData(String.format(ID, dimName), dimManager);
			
			dimManager.loadSettingsFromConfig();
		} else
			dimManager = (StellarDimensionManager) data;

		dimManager.manager = manager;
		
		return dimManager;
	}

	public static StellarDimensionManager get(World world) {
		WorldSavedData data = world.getPerWorldStorage().loadData(StellarDimensionManager.class,
				String.format(ID, world.provider.getDimensionName()));
		
		if(!(data instanceof StellarDimensionManager))
			return null;
		
		return (StellarDimensionManager)data;
	}

	public StellarDimensionManager(String id) {
		super(id);
		this.dimensionName = id.replaceFirst("stellarskydimension", "");
	}
	
	public PerDimensionSettings getSettings() {
		return this.settings;
	}
	
	public ICelestialCoordinate getCoordinate() {
		return this.coordinate;
	}
	
	public IStellarSkySet getSkySet() {
		return this.skyset;
	}
	
	private void loadSettingsFromConfig() {
		this.settings = (PerDimensionSettings) ((INBTConfig) StellarSky.proxy.getDimensionSettings().getSubConfig(this.dimensionName)).copy();
		this.markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.syncFromNBT(compound, StellarManager.getServerManager(MinecraftServer.getServer()), false);
	}
	
	public void syncFromNBT(NBTTagCompound compound, StellarManager manager, boolean isRemote) {
		if(manager.isLocked() || isRemote) {
			this.settings = new PerDimensionSettings(this.dimensionName);
			settings.readFromNBT(compound);
		} else {
			this.loadSettingsFromConfig();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		settings.writeToNBT(compound);
	}
	
	public void setup() {
		StellarSky.logger.info(String.format("Initializing Dimension Settings on Dimension %s...", this.dimensionName));
		if(settings.allowRefraction())
			this.skyset = new RefractiveSkySet(this.settings);
		else this.skyset = new NonRefractiveSkySet(this.settings);
		this.coordinate = new StellarCoordinate(manager.getSettings(), this.settings);
		coordinate.update(manager.getSkyTime(0.0) / manager.getSettings().day / manager.getSettings().year);
		
		this.landscapeCache = settings.isLandscapeEnabled()? new LandscapeCache() : null;
		
		StellarSky.logger.info(String.format("Initialized Dimension Settings on Dimension %s.", this.dimensionName));
	}
	
	public Collection<StellarCollection> constructCelestials(ICelestialCoordinate coordinate, ISkyEffect sky) {
		collections.clear();
		foundSuns.clear();
		foundMoons.clear();
		
		StellarSky.logger.info("Starting Initial Update to Construct Images...");
		manager.update(0.0);
		StellarSky.logger.info("Initial Update Ended.");
		
		for(StellarObjectContainer container : manager.getCelestialManager().getLayers()) {
			StellarCollection collection = new StellarCollection(container, coordinate, sky,
					this.coordinate.getYearPeriod());
			container.addCollection(collection);
			collections.add(collection);
			
			foundSuns.addAll(collection.getSuns());
			foundMoons.addAll(collection.getMoons());
		}
		return this.collections;
	}
	
	public List<ICelestialObject> getSuns() {
		return this.foundSuns;
	}
	
	public List<ICelestialObject> getMoons() {
		return this.foundMoons;
	}
	
	public void update(World world, long currentTick, long currentUniversalTick) {
		double skyTime = manager.getSkyTime(currentTick);
		coordinate.update(skyTime / manager.getSettings().day / manager.getSettings().year);
		
		for(int i = 0; i < collections.size(); i++) {
			StellarCollection collection = collections.get(i);
			StellarObjectContainer container = manager.getCelestialManager().getLayers().get(i);
			container.updateCollection(collection, currentUniversalTick);
		}
		
		if(world.isRemote && this.landscapeCache != null)
			landscapeCache.updateCache();
	}

	public LandscapeCache getLandscapeCache() {
		return this.landscapeCache;
	}
}
