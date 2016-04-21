package stellarium.stellars.layer;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialCollectionType;
import stellarapi.api.celestials.ICelestialCollection;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;

public class StellarCollection<Obj extends StellarObject> implements ICelestialCollection {

	private IStellarLayerType<Obj, IConfigHandler, INBTConfig> type;
	private StellarObjectContainer container;
	private ICelestialCoordinate coordinate;
	private ISkyEffect sky;
	private Map<Obj, IPerWorldImage> imageMap;
	private CelestialPeriod yearPeriod;
		
	public StellarCollection(StellarObjectContainer container, ICelestialCoordinate coordinate, ISkyEffect sky, CelestialPeriod yearPeriod) {
		this.type = container.getType();
		this.container = container;
		this.coordinate = coordinate;
		this.sky = sky;
		this.yearPeriod = yearPeriod;
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
		Map<Obj, IPerWorldImage> updateMap = type.temporalLoadImagesInRange(pos, radius);
		if(updateMap == null) {
			Predicate<ICelestialObject> inRange = type.conditionInRange(pos, radius);
			if(inRange == null)
				inRange = new PredicateInRange(pos, radius);
			return ImmutableSet.copyOf(Iterables.filter(imageMap.values(), inRange));
		} else {			
			for(Map.Entry<Obj, IPerWorldImage> entry : updateMap.entrySet())
				entry.getValue().updateCache(entry.getKey(), this.coordinate, this.sky);
			return ImmutableSet.copyOf(updateMap.values());
		}
	}
	
	private class PredicateInRange implements Predicate<ICelestialObject> {
		private SpCoord pos;
		private double radius;
		
		public PredicateInRange(SpCoord pos, double radius) {
			this.pos = pos;
			this.radius = radius;
		}
		
		@Override
		public boolean apply(ICelestialObject input) {
			return pos.distanceTo(input.getCurrentHorizontalPos()) < radius;
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
	
	public void addImages(Set<Obj> addedSet, Map<Obj, IPerWorldImageType> imageTypeMap) {
		for(Obj object : addedSet)
		{
			IPerWorldImage image = imageTypeMap.get(object).generateImage();
			image.initialize(object, this.coordinate, this.sky, this.yearPeriod);
			imageMap.put(object, image);
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
