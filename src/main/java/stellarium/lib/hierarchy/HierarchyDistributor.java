package stellarium.lib.hierarchy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import stellarium.lib.hierarchy.structure.IHierarchyStructure;
import stellarium.lib.hierarchy.structure.MapStructure;
import stellarium.lib.hierarchy.structure.RAListStructure;

public enum HierarchyDistributor {
	INSTANCE;

	private Map<Class<?>, HierarchyWrapper> wrapperMap = Maps.newHashMap();

	Map<Class<?>, IHierarchyStructure> defaultStrMap = Maps.newHashMap();
	Map<String, IHierarchyStructure> structureMap = Maps.newHashMap();
	Map<String, IIDEvaluator> evaluatorMap = Maps.newHashMap();

	HierarchyDistributor() {
		this.registerStructure(List.class, new RAListStructure());
		this.registerStructure(Map.class, new MapStructure());
	}

	void register(Class<?> hierarchyType) {
		if(!hierarchyType.isAnnotationPresent(Hierarchy.class)) {
			throw new IllegalArgumentException(
					String.format("The type %s does not contain @Hierarchy", hierarchyType));
		}

		wrapperMap.put(hierarchyType, new HierarchyWrapper(hierarchyType));
	}

	public <T> void registerStructure(Class<T> structureType, IHierarchyStructure<T> structure) {
		defaultStrMap.put(structureType, structure);
	}
	
	public void registerStructure(String structureId, IHierarchyStructure structure) {
		structureMap.put(structureId, structure);
	}
	
	public void registerEvaluator(String evaluatorId, IIDEvaluator evaluator) {
		evaluatorMap.put(evaluatorId, evaluator);
	}
	
	
	/**
	 * Gets set of id for the hierarchy type.
	 * Only for external use, like metadata.
	 * */
	public <T> Map<T, IFieldElementDescription> fields(Class<?> hierarchyType) {
		return this.getSafely(hierarchyType).fields();
	}
	
	/**
	 * Gets Iterator of elements for the field.
	 * Only for external use, like metadata.
	 * */
	public Iterator iteratorFor(Object instance, Object fieldId) {
		Preconditions.checkNotNull(instance);
		return this.getSafely(instance.getClass()).elementIteOnField(instance, fieldId);
	}


	HierarchyWrapper get(Class<?> hierarchyType) {
		return wrapperMap.get(hierarchyType);
	}

	HierarchyWrapper getSafely(Class<?> hierarchyType) {
		if(!wrapperMap.containsKey(hierarchyType))
			this.register(hierarchyType);
		return wrapperMap.get(hierarchyType);
	}
}