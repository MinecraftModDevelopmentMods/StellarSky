package stellarium.stellars.layer;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialCollectionType;
import stellarapi.api.celestials.ICelestialCollection;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarium.stellars.layer.query.CacheStellarObject;
import stellarium.stellars.layer.query.ILayerTempManager;
import stellarium.stellars.layer.query.IMetadataManager;
import stellarium.stellars.layer.query.QueryStellarObject;

public class StellarCollection<Obj extends StellarObject> implements ICelestialCollection {

	private IStellarLayerType<Obj, IConfigHandler, INBTConfig> type;
	private StellarObjectContainer container;
	private ICelestialCoordinate coordinate;
	private ISkyEffect sky;
	private Map<Obj, IPerWorldImage> imageMap = Maps.newHashMap();
	private CacheStellarObject<Obj, IPerWorldImage> cache;
	private CelestialPeriod yearPeriod;

	public StellarCollection(StellarObjectContainer container, ICelestialCoordinate coordinate, ISkyEffect sky, CelestialPeriod yearPeriod) {
		this.type = container.getType();
		this.container = container;
		this.coordinate = coordinate;
		this.sky = sky;
		this.yearPeriod = yearPeriod;
		
		ILayerTempManager<Obj> temp = type.getTempLoadManager();
		this.cache = temp != null? new CacheStellarObject(
				new WorldImageManager(temp), temp) : null;
	}
	
	@Override
	public String getName() {
		return type.getName();
	}

	@Override
	public ImmutableSet<? extends IPerWorldImage> getObjects() {
		return ImmutableSet.copyOf(imageMap.values());
	}

	@Override
	public ImmutableSet<? extends IPerWorldImage> getObjectInRange(SpCoord pos, double radius) {
		if(this.cache == null) {
			Predicate<ICelestialObject> inRange = type.conditionInRange(pos, radius);
			if(inRange == null)
				inRange = new QueryStellarObject(pos, radius);
			return ImmutableSet.copyOf(Iterables.filter(imageMap.values(), inRange));
		} else {
			return ImmutableSet.copyOf(cache.query(new QueryStellarObject(pos, radius)));
		}
	}
	
	private class WorldImageManager implements IMetadataManager<Obj, IPerWorldImage> {
		private ILayerTempManager<Obj> temp;
		
		public WorldImageManager(ILayerTempManager<Obj> temp) {
			this.temp = temp;
		}

		@Override
		public IPerWorldImage loadMetadata(Obj object) {
			return temp.loadImage(object);
		}

		@Override
		public void updateMetadata(Obj object, IPerWorldImage metadata) {
			metadata.updateCache(object, coordinate, sky);
		}
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
		for(Map.Entry<Obj, IPerWorldImage> entry : imageMap.entrySet())
			entry.getValue().updateCache(entry.getKey(), this.coordinate, this.sky);
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
