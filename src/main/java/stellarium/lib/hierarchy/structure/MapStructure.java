package stellarium.lib.hierarchy.structure;

import java.util.Iterator;
import java.util.Map;

public class MapStructure implements IHierarchyStructure<Map> {

	@Override
	public Iterator iteratorFor(Map target) {
		return target.values().iterator();
	}


}