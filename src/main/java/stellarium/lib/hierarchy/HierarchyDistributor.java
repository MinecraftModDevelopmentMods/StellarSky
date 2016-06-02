package stellarium.lib.hierarchy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	 * Triggers Hierarchical Call.
	 * @param instance the hierarchy instance
	 * @param callId specifies the call
	 * @param parameters the parameters for this call
	 * */
	public void call(Object instance, String callId, Object... parameters) {
		this.getSafely(instance.getClass()).call(instance, callId, parameters);
	}
	
	/**
	 * Triggers Call for elements on certain field.
	 * @param instance the parent hierarchy instance
	 * @param fieldId specifies the field
	 * @param callId specifies the call
	 * @param subParams the parameters for sub-elements on the field
	 * */
	public void callFor(Object instance, Object fieldId, String callId, Object... subParams) {
		this.getSafely(instance.getClass()).callFor(instance, fieldId, callId, subParams);
	}
	
	
	/**
	 * Gets set of id for the hierarchy type.
	 * Only for external use, like metadata.
	 * */
	public Set ids(Class<?> hierarchyType) {
		return this.getSafely(hierarchyType).elementIds();
	}
	
	/**
	 * Gets Iterator of elements for the field.
	 * Only for external use, like metadata.
	 * */
	public Iterator iteratorFor(Object instance, Object fieldId) {
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