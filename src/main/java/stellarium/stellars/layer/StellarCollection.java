package stellarium.stellars.layer;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.server.MinecraftServer;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialCollectionType;
import stellarapi.api.celestials.ICelestialCollection;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarium.stellars.layer.query.ILayerTempManager;
import stellarium.stellars.layer.query.MetadataQueryCache;
import stellarium.stellars.layer.query.QueryStellarObject;
import stellarium.stellars.layer.update.IMetadataUpdater;
import stellarium.stellars.layer.update.IUpdateTracked;
import stellarium.stellars.layer.update.MetadataUpdateTracker;

public class StellarCollection<Obj extends StellarObject> implements ICelestialCollection {

	private IStellarLayerType<Obj, IConfigHandler, INBTConfig> type;
	private StellarObjectContainer container;
	
	private ICelestialCoordinate coordinate;
	private ISkyEffect sky;
	
	private Map<Obj, IPerWorldImage> imageMap = Maps.newHashMap();
	
	private MetadataQueryCache<Obj, IUpdateTracked<IPerWorldImage>> cache;
	private MetadataUpdateTracker<Obj, IPerWorldImage> updateTracker;
	
	private CelestialPeriod yearPeriod;

	public StellarCollection(StellarObjectContainer container, ICelestialCoordinate coordinate, ISkyEffect sky, CelestialPeriod yearPeriod) {
		this.type = container.getType();
		this.container = container;
		this.coordinate = coordinate;
		this.sky = sky;
		this.yearPeriod = yearPeriod;
		
		this.updateTracker = new MetadataUpdateTracker(new ImageUpdater());
		
		ILayerTempManager<Obj> temp = type.getTempLoadManager();
		this.cache = temp != null?
				new MetadataQueryCache(new ImageBuilder(temp), temp) : null;
	}
	
	@Override
	public String getName() {
		return type.getName();
	}

	private class ImageBuilder implements Function<Obj, IUpdateTracked<IPerWorldImage>> {
		private ILayerTempManager<Obj> temp;
		
		public ImageBuilder(ILayerTempManager<Obj> temp) {
			this.temp = temp;
		}

		@Override
		public IUpdateTracked<IPerWorldImage> apply(Obj object) {
			IPerWorldImage image = temp.loadImage(object);
			image.initialize(object, coordinate, sky, yearPeriod);
			return updateTracker.createTracker(object, image);
		}
	}

	private class ImageUpdater implements IMetadataUpdater<Obj, IPerWorldImage> {
		@Override
		public long getCurrentTime() {
			return MinecraftServer.getServer().getEntityWorld().getTotalWorldTime();
		}

		@Override
		public void update(Obj object, IPerWorldImage metadata) {
			metadata.updateCache(object, coordinate, sky);
		}
	}
	

	@Override
	public ImmutableSet<? extends IPerWorldImage> getObjects() {
		return ImmutableSet.copyOf(imageMap.values());
	}

	@Override
	public ImmutableSet<? extends IPerWorldImage> getObjectInRange(SpCoord pos, double radius) {
		// TODO Controlling Transformations
		QueryStellarObject query = new QueryStellarObject(pos, radius);

		Predicate<ICelestialObject> inRange = type.conditionInRange(pos, radius);
		if(inRange == null)
			inRange = query;

		Iterable<IPerWorldImage> saved = Iterables.filter(imageMap.values(), inRange);

		if(cache != null) {
			return ImmutableSet.copyOf(
					Iterables.concat(saved,
							updateTracker.addUpdateOnIteration(cache.query(query))));
		} else return ImmutableSet.copyOf(saved);
	}

	public IPerWorldImage loadImageFor(Obj object) {
		if(imageMap.containsKey(object))
			return imageMap.get(object);
		else return cache.lazyLoader().apply(object).getMetadata();
	}

	@Override
	public ICelestialObject getNearerObject(SpCoord pos, ICelestialObject obj1, ICelestialObject obj2) {
		Comparator<ICelestialObject> comparator = type.getDistanceComparator(pos);
		if(comparator == null)
			return (pos.distanceTo(obj1.getCurrentHorizontalPos())
					< pos.distanceTo(obj2.getCurrentHorizontalPos()))? obj1 : obj2;
		else return comparator.compare(obj1, obj2) < 0? obj1 : obj2;
	}
	
	public void addImages(Set<Obj> addedSet, Map<Obj, Callable<IPerWorldImage>> imageTypeMap) {
		try {
			for(Obj object : addedSet) {
				IPerWorldImage image = imageTypeMap.get(object).call();
				image.initialize(object, this.coordinate, this.sky, this.yearPeriod);
				imageMap.put(object, image);
			}
		} catch (Exception exc) {
			Throwables.propagate(exc);
		}
	}
	
	public void removeImages(Set<Obj> removedSet) {
		for(Obj object : removedSet)
			imageMap.remove(object);
	}

	public void update() {
		updateTracker.updateMap(this.imageMap);
	}

	
	public Collection<IPerWorldImage> getSuns() {
		List<IPerWorldImage> list = Lists.newArrayList();
		Collection<Obj> suns = type.getSuns(this.container);
		if(suns == null) return list;
		for(Obj sun : suns)
			list.add(imageMap.get(sun));
		return list;
	}
	
	public Collection<IPerWorldImage> getMoons() {
		List<IPerWorldImage> list = Lists.newArrayList();
		Collection<Obj> moons = type.getMoons(this.container);
		if(moons == null) return list;
		for(Obj moon : moons)
			list.add(imageMap.get(moon));
		return list;
	}

	
	@Override
	public int searchOrder() {
		return type.searchOrder();
	}

	@Override
	public boolean isBackground() {
		return type.isBackground();
	}

	@Override
	public EnumCelestialCollectionType getCollectionType() {
		return type.getCollectionType();
	}

}
