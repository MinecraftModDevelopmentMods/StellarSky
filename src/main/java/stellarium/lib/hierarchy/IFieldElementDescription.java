package stellarium.lib.hierarchy;

import stellarium.lib.hierarchy.structure.IHierarchyStructure;

public interface IFieldElementDescription {
	public Class<?> getElementType();
	public IHierarchyStructure getStructure();
}