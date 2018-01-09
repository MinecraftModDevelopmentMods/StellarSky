package stellarium.world;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import stellarapi.api.ICelestialCoordinates;
import stellarapi.api.ICelestialScene;
import stellarapi.api.ISkyEffect;
import stellarapi.api.SAPIReferences;
import stellarapi.api.celestials.ICelestialCollection;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.celestials.IEffectorType;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.world.worldset.WorldSet;
import stellarium.StellarSky;
import stellarium.api.ICelestialHelper;
import stellarium.api.StellarSkyAPI;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.layer.StellarObjectContainer;

public final class StellarScene implements ICelestialScene {
	private final StellarManager manager;
	private final World world;
	private final WorldSet worldSet;

	private PerDimensionSettings settings;
	private IStellarSkySet skyset;
	private StellarCoordinate coordinate;
	private List<StellarCollection> collections = Lists.newArrayList();
	private List<ICelestialObject> foundSuns = Lists.newArrayList();
	private List<ICelestialObject> foundMoons = Lists.newArrayList();

	public static StellarScene getScene(World world) {
		ICelestialScene scene = SAPIReferences.getActivePack(world);
		return (scene instanceof StellarScene)? (StellarScene) scene : null;
	}

	public StellarScene(World world, WorldSet worldSet) {
		this.world = world;
		this.worldSet = worldSet;
		this.manager = StellarManager.getManager(world);
	}

	public PerDimensionSettings getSettings() {
		return this.settings;
	}

	private void loadSettingsFromConfig() {
		this.settings = (PerDimensionSettings) ((INBTConfig) StellarSky.PROXY.getDimensionSettings().getSubConfig(worldSet.name)).copy();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();

		// When it's the first one
		if(world.provider.getDimension() == 0 || world.isRemote) {
			nbt.setTag("main", StellarManager.getManager(this.world).serializeNBT());
		}

		settings.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if(world.provider.getDimension() == 0 || world.isRemote) {
			if(nbt.hasKey("main", 10)) {
				StellarManager.getManager(this.world).deserializeNBT(nbt.getCompoundTag("main"));
			}
		}

		if(manager.isLocked() || world.isRemote) {
			this.settings = new PerDimensionSettings(this.worldSet);
			settings.readFromNBT(nbt);
		} else {
			this.loadSettingsFromConfig();
		}

		if(world.isRemote)
			manager.setup(StellarSky.PROXY.getClientCelestialManager().copyFromClient());
	}

	public List<StellarCollection> getCollections() {
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
	}


	@Override
	public void prepare() {
		collections.clear();
		foundSuns.clear();
		foundMoons.clear();

		if(this.settings == null) {
			// TODO Deal with these in general sense
			this.loadSettingsFromConfig();
			manager.setup(StellarSky.PROXY.getClientCelestialManager().copyFromClient());
		}

		String dimName = world.provider.getDimensionType().getName();
		StellarSky.INSTANCE.getLogger().info(String.format("Initializing Dimension Settings on Dimension %s...", dimName));
		if(settings.allowRefraction())
			this.skyset = new RefractiveSkySet(this.settings);
		else this.skyset = new NonRefractiveSkySet(this.settings);
		this.coordinate = new StellarCoordinate(manager.getSettings(), this.settings);
		coordinate.update(manager.getSkyTime(0.0) / manager.getSettings().day / manager.getSettings().year);

		StellarSky.INSTANCE.getLogger().info(String.format("Initialized Dimension Settings on Dimension %s.", dimName));


		StellarSky.INSTANCE.getLogger().info("Evaluating Stellar Collections from Celestial State...");

		StellarSky.INSTANCE.getLogger().info("Starting Test Update.");
		manager.update(0.0);
		StellarSky.INSTANCE.getLogger().info("Test Update Ended.");
		
		for(StellarObjectContainer container : manager.getCelestialManager().getLayers()) {
			StellarCollection collection = new StellarCollection(container, this.coordinate, this.skyset,
					this.coordinate.getYearPeriod());
			container.addCollection(collection);
			collections.add(collection);
			
			foundSuns.addAll(collection.getSuns());
			foundMoons.addAll(collection.getMoons());
		}

		if(world.isRemote)
			StellarSky.PROXY.setupDimensionLoad(this);
		
		StellarSky.INSTANCE.getLogger().info("Evaluated Stellar Collections.");
	}

	@Override
	public void onRegisterCollection(Consumer<ICelestialCollection> colRegistry,
			BiConsumer<IEffectorType, ICelestialObject> effRegistry) {
		for(ICelestialCollection col : this.collections)
			colRegistry.accept(col);

		for(ICelestialObject sun : this.foundSuns)
			effRegistry.accept(IEffectorType.Light, sun);

		for(ICelestialObject moon : this.foundMoons)
			effRegistry.accept(IEffectorType.Tide, moon);
	}

	@Override
	public ICelestialCoordinates createCoordinates() {
		return this.coordinate;
	}

	@Override
	public ISkyEffect createSkyEffect() {
		return this.skyset;
	}

	@Override
	public WorldProvider replaceWorldProvider(WorldProvider provider) {
		if(manager.getSettings().serverEnabled && this.getSettings().doesPatchProvider()) {
			ICelestialHelper helper = new CelestialHelperInside((float)this.getSettings().getSunlightMultiplier(), 1.0f,
					this.getSuns().get(0), this.getMoons().get(0), this.coordinate, this.skyset);
			WorldProvider newProvider = StellarSkyAPI.getReplacedWorldProvider(this.world, provider, helper);
			return newProvider;
		} else return null;
	}
}
