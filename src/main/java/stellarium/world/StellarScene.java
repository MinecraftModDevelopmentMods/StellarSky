package stellarium.world;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import stellarapi.api.SAPIReferences;
import stellarapi.api.celestials.CelestialCollection;
import stellarapi.api.celestials.CelestialObject;
import stellarapi.api.celestials.IEffectorType;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.pack.ICelestialScene;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarapi.api.view.IAtmosphereEffect;
import stellarapi.api.view.ICCoordinates;
import stellarapi.api.world.ICelestialHelper;
import stellarapi.api.world.worldset.WorldSet;
import stellarapi.example.CelestialHelperSimple;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.layer.StellarLayer;

public final class StellarScene implements ICelestialScene {
	private final StellarManager manager;
	private final World world;
	private final WorldSet worldSet;

	private PerDimensionSettings settings;
	private IStellarSkySet skyset;
	private StellarCoordinates coordinate;
	private List<CelestialObject> foundSuns = Lists.newArrayList();
	private List<CelestialObject> foundMoons = Lists.newArrayList();

	@Deprecated
	public static StellarScene getScene(World world) {
		ICelestialScene scene = SAPIReferences.getActivePack(world);
		return (scene instanceof StellarScene)? (StellarScene) scene : null;
	}

	public StellarScene(World world, WorldSet worldSet, PerDimensionSettings settings) {
		this.world = world;
		this.worldSet = worldSet;
		this.manager = StellarManager.getManager(world);
		this.settings = settings;
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

		// Writes Stellar Manager.
		// TODO Stellar API Separate networking code and serialization code
		nbt.setTag("main", StellarManager.getManager(this.world).serializeNBT());
		settings.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		// When it's the default world and there's the manager nbt, read it.
		if(world.provider.getDimension() == 0 || world.isRemote) {
			if(nbt.hasKey("main", 10)) {
				manager.deserializeNBT(nbt.getCompoundTag("main"));
			}
		}

		if(world.isRemote) {
			manager.setup(StellarSky.PROXY.getClientCelestialManager().copyFromClient());
		}

		if(manager.isLocked() || world.isRemote) {
			this.settings = new PerDimensionSettings(this.worldSet);
			settings.readFromNBT(nbt);
		} else {
			this.loadSettingsFromConfig();
		}
	}

	public List<CelestialObject> getSuns() {
		return this.foundSuns;
	}

	public List<CelestialObject> getMoons() {
		return this.foundMoons;
	}

	public void update(World world, long currentTick, long currentUniversalTick) {
		coordinate.update(manager.getSkyYear(currentTick));
	}


	@Override
	public void prepare() {
		// TODO AAA horizontal period is dependent on coordinates - can't cache them in object
		foundSuns.clear();
		foundMoons.clear();

		String dimName = world.provider.getDimensionType().getName();
		StellarSky.INSTANCE.getLogger().info(String.format("Initializing Dimension Settings on Dimension %s...", dimName));
		if(settings.allowRefraction())
			this.skyset = new RefractiveSkySet(this.settings);
		else this.skyset = new NonRefractiveSkySet(this.settings);
		this.coordinate = new StellarCoordinates(manager.getSettings(), this.settings);
		coordinate.update(manager.getSkyYear(0.0));

		StellarSky.INSTANCE.getLogger().info(String.format("Initialized Dimension Settings on Dimension %s.", dimName));


		StellarSky.INSTANCE.getLogger().info("Evaluating Stellar Collections from Celestial State...");

		StellarSky.INSTANCE.getLogger().info("Starting Test Update.");
		manager.update(0.0);
		StellarSky.INSTANCE.getLogger().info("Test Update Ended.");

		for(StellarCollection container : manager.getCelestialManager().getLayers()) {
			StellarLayer type = container.getType();
			type.initialUpdate(container);

			foundSuns.addAll(type.getSuns(container));
			foundMoons.addAll(type.getMoons(container));
		}

		if(world.isRemote)
			StellarSky.PROXY.setupDimensionLoad(this);

		StellarSky.INSTANCE.getLogger().info("Evaluated Stellar Collections.");
	}

	@Override
	public void onRegisterCollection(Consumer<CelestialCollection> colRegistry,
			BiConsumer<IEffectorType, CelestialObject> effRegistry) {
		for(CelestialCollection col : manager.getCelestialManager().getLayers())
			colRegistry.accept(col);

		for(CelestialObject sun : this.foundSuns)
			effRegistry.accept(IEffectorType.Light, sun);

		for(CelestialObject moon : this.foundMoons)
			effRegistry.accept(IEffectorType.Tide, moon);
	}

	@Override
	public ICCoordinates createCoordinates() {
		return this.coordinate;
	}

	@Override
	public IAtmosphereEffect createAtmosphereEffect() {
		return this.skyset;
	}

	@Override
	public ICelestialHelper createCelestialHelper() {
		if(this.getSettings().doesPatchProvider()) {
			return new CelestialHelperSimple((float)this.getSettings().getSunlightMultiplier(), 1.0f,
					this.getSuns().get(0), this.getMoons().get(0), this.coordinate, this.skyset);
		} else return null;
	}

	@Override
	public IAdaptiveRenderer createSkyRenderer() {
		return StellarSky.PROXY.setupSkyRenderer(this.world, this.worldSet, settings.getSkyRendererType());
	}
}
