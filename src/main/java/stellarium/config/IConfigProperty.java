package stellarium.config;

public interface IConfigProperty<T> {
	
	/**gives the value of this property*/
	public T getVal();
	
	
	/**gives the name of this property*/
	public String getName();
	
	/**
	 * Sets Explanation of this property
	 * @return this
	 * */
	public IConfigProperty<T> setExpl(String expl);
	
	/**
	 * Simulates setting value.
	 * It will behave like the case that the value is set by configuration,
	 * So does nothing when disabled.
	 * */
	public void simSetVal(T val);

	/**
	 * Simulates enable/disable.
	 * It will behave like the case that the property is enabled/diabled.
	 * */
	public void simSetEnabled(boolean enabled);
}
