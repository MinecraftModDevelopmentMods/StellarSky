package stellarium.lib.test;

import java.util.List;

import com.google.common.collect.Lists;

import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyCall;
import stellarium.lib.hierarchy.HierarchyElement;
import stellarium.lib.hierarchy.HierarchyCall.Accept;

@Hierarchy
public class HierarchyTest {

	@HierarchyElement(type = HierarchySubTest.class)
	private List<HierarchySubTest> subElems = Lists.newArrayList(new HierarchySubTest());

	private String context = "";

	@HierarchyCall(id = "read")
	public void read(String context, int value) {
		this.context = context;
	}

}