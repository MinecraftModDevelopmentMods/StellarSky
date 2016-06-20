package stellarium.lib.hierarchy;

public interface IIDEvaluator<T> {
	
	/**
	 * Evaluates id for certain field element.
	 * @param elementIndex the index for the field element
	 * @param defaultId the specified id
	 * */
	public T evaluateID(int elementIndex, String defaultId);

}