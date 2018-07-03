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
import stellarapi.api.ICelestialCoordinates;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialCollectionType;
import stellarapi.api.celestials.ICelestialCollection;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;

public class StellarCollection<Obj extends StellarObject> implements ICelestialCollection {

	private IStellarLayerType<Obj, IConfigHandler, INBTConfig> type;
	private StellarObjectContainer<Obj> container;
	
	private ICelestialCoordinates coordinate;
	private ISkyEffect sky;
	
	private Map<Obj, IPerWorldImage<Obj>> imageMap = Maps.newHashMap();
	
	private CelestialPeriod yearPeriod;
	
	public StellarCollection(StellarObjectContainer<Obj> container, ICelestialCoordinates coordinate, ISkyEffect sky, CelestialPeriod yearPeriod) {
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
		QueryStellarObject query = new QueryStellarObject(pos, radius);

		Predicate<ICelestialObject> inRange = type.conditionInRange(pos, radius);
		if(inRange == null)
			inRange = query;

		Iterable<IPerWorldImage<Obj>> saved = Iterables.filter(imageMap.values(), inRange);

		return ImmutableSet.copyOf(saved);
	}

	public IPerWorldImage<Obj> loadImageFor(Obj object) {
		if(imageMap.containsKey(object))
			return imageMap.get(object);
		else return null;
	}

	@Override
	public ICelestialObject getNearerObject(SpCoord pos, ICelestialObject obj1, ICelestialObject obj2) {
		Comparator<ICelestialObject> comparator = type.getDistanceComparator(pos);
		if(comparator == null)
			return (pos.distanceTo(obj1.getCurrentHorizontalPos())
					< pos.distanceTo(obj2.getCurrentHorizontalPos()))? obj1 : obj2;
		else return comparator.compare(obj1, obj2) < 0? obj1 : obj2;
	}
	
	public void addImages(Set<Obj> addedSet, Map<Obj, Callable<IPerWorldImage<Obj>>> imageTypeMap) {
		try {
			for(Obj object : addedSet) {
				IPerWorldImage<Obj> image = imageTypeMap.get(object).call();
				image.initialize(object, this.coordinate, this.sky, this.yearPeriod);
				imageMap.put(object, image);
			}
		} catch (Exception exc) {
			Throwables.propagate(exc);
		}
	}

	public void update() {
		for(Map.Entry<Obj, IPerWorldImage<Obj>> entry : imageMap.entrySet())
			entry.getValue().updateCache(entry.getKey(), coordinate, sky);
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
