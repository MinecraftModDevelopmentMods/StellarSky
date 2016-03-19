package stellarium.stellars.entry;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Specific entry for object.
 * An entry contains position information,
 * and can contain lighting, rendering, effect information.
 * */
public class CelestialEntry {
	
	private CelestialEntry parentEntry;
	private List<CelestialEntry> childEntryList = Lists.newArrayList();
	private ICelestialPosition position;
	private ICelestialBody body;
	
	public CelestialEntry addEntry(ICelestialPosition pos, ICelestialBody body) {
		CelestialEntry entry = new CelestialEntry();
		entry.position = pos;
		entry.body = body;
		entry.parentEntry = this;
		childEntryList.add(entry);
		
		return entry;
	}
	
	public CelestialEntry getParentEntry() {
		return this.parentEntry;
	}
	
	public List<CelestialEntry> getChildEntries() {
		return this.childEntryList;
	}

}
