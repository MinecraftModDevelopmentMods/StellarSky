package stellarium.lib.hierarchy;

/**
 * ID evaluator which uses index.
 * */
public class EnumIDEvaluator<K extends Enum<K>> implements IIDEvaluator<K> {

	private Class<K> enumClass;
	
	public EnumIDEvaluator(Class<K> enumClass) {
		this.enumClass = enumClass;
	}
	
	@Override
	public K evaluateID(int elementIndex, String defaultId) {
		return enumClass.getEnumConstants()[elementIndex];
	}

}
