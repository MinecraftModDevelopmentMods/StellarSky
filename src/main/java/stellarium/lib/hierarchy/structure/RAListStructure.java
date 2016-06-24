package stellarium.lib.hierarchy.structure;

import java.util.Iterator;
import java.util.List;

/**
 * Structure for lists.
 * */
public class RAListStructure implements IHierarchyStructure<List> {

	@Override
	public Iterator iteratorFor(List target) {
		return target.iterator();
	}

}