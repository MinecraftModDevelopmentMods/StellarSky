package stellarium.lib.test;

import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyCall;

@Hierarchy
public class HierarchySubTest {
	String current;

	@HierarchyCall(id = "read", acceptParams = @HierarchyCall.Accept(Integer.class))
	public void read(int value) {
		current = Integer.toHexString(value);
	}
}