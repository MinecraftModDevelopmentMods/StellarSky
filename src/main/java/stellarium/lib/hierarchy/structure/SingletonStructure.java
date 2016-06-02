package stellarium.lib.hierarchy.structure;

import java.util.Iterator;

import com.google.common.collect.Iterators;

public enum SingletonStructure implements IHierarchyStructure {
	INSTANCE;

	@Override
	public Iterator iteratorFor(Object target) {
		return Iterators.forArray(target);
	}

}
