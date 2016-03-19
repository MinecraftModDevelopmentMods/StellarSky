package stellarium.stellars.entry;

import java.util.List;

import com.google.common.collect.Lists;

public class CelestialEntry {
	
	private CelestialEntry parentEntry;
	private List<CelestialEntry> childEntryList = Lists.newArrayList();
	private ICelestialPosition position;
	private ICelestialBody body;//There is bodyless entry.
	
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
