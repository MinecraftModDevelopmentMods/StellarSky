package stellarium.world;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.lib.config.INBTConfig;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.layer.StellarObjectContainer;

public final class StellarDimensionManager extends WorldSavedData {
	
	public static String ID = "stellarskydimension%s";
	
	private StellarManager manager;
	
	private PerDimensionSettings settings;
	private IStellarSkySet skyset;
	private StellarCoordinate coordinate;
	private List<StellarCollection> collections = Lists.newArrayList();
	
	private String dimensionName;
	
	public static StellarDimensionManager loadOrCreate(World world, StellarManager manager, String dimName) {
		WorldSavedData data = world.perWorldStorage.loadData(StellarDimensionManager.class, String.format(ID, dimName));
		StellarDimensionManager dimManager;
		
		if(!(data instanceof StellarDimensionManager))
		{
			dimManager = new StellarDimensionManager(String.format(ID, dimName));
			world.perWorldStorage.setData(String.format(ID, dimName), dimManager);
			
			dimManager.loadSettingsFromConfig();
		} else
			dimManager = (StellarDimensionManager) data;

		dimManager.manager = manager;
		
		return dimManager;
	}

	public static StellarDimensionManager get(World world) {
		WorldSavedData data = world.perWorldStorage.loadData(StellarDimensionManager.class,
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
		this.settings = (PerDimensionSettings) ((INBTConfig) StellarSky.proxy.dimensionSettings.getSubConfig(this.dimensionName)).copy();
		this.markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.syncFromNBT(compound, false);
	}
	
	public void syncFromNBT(NBTTagCompound compound, boolean isRemote) {
		if(StellarManager.getManager(isRemote).isLocked() || isRemote)
		{
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
		StellarSky.logger.info("Initializing Dimension Settings...");
		if(settings.allowRefraction())
			this.skyset = new RefractiveSkySet(this.settings);
		else this.skyset = new NonRefractiveSkySet(this.settings);
		this.coordinate = new StellarCoordinate(manager.getSettings(), this.settings);
		StellarSky.logger.info("Initialized Dimension Settings.");
	}
	
	public Collection<StellarCollection> constructCelestials(ICelestialCoordinate coordinate, ISkyEffect sky) {
		for(StellarObjectContainer container : manager.getCelestialManager().getLayers()) {
			StellarCollection collection = new StellarCollection(container, coordinate, sky,
					this.coordinate.getYearPeriod());
			container.addCollection(collection);
			collections.add(collection);
		}
		return this.collections;
	}
	
	public void update(World world, double currentTick) {
		double skyTime = manager.getSkyTime(currentTick);
		coordinate.update(world, skyTime / manager.getSettings().day / manager.getSettings().year);
		
		ISkyEffect skyEffect = StellarAPIReference.getSkyEffect(world);
		
		for(int i = 0; i < collections.size(); i++) {
			StellarCollection collection = collections.get(i);
			StellarObjectContainer container = manager.getCelestialManager().getLayers().get(i);
			container.updateCollection(collection);
		}
	}
}
