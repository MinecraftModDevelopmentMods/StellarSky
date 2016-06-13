package stellarium.lib.hierarchy.structure;

import java.util.Iterator;

public interface IHierarchyStructure<Target> {
	/**
	 * Gets iterator for this structure
	 * @param target the target
	 * */
	public Iterator iteratorFor(Target target);

}