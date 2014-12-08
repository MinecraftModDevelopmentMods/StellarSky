package stellarium.config;

public interface IConfigPropHandler<T> {
	
	/**called when constructing the property.*/
	public void onConstruct(IMConfigProperty<T> prop);
	
	/**
	 * called when the property element value is changed,
	 * so the property value has to be modified.
	 * */
	public T getValue(IMConfigProperty<T> prop);

	/**
	 * called when the property value is being set.
	 * */
	public void onSetVal(IMConfigProperty<T> prop, T val);
}
