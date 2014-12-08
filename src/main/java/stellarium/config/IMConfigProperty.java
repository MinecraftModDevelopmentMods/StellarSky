package stellarium.config;

import stellarium.config.element.EnumPropElement;
import stellarium.config.element.IPropElement;

/**
 * Modifiable Configuration Property - this is for Configuration handler & Property Relations.
 * */
public interface IMConfigProperty<T> extends IConfigProperty<T> {
	
	/**adds property element with certain type*/
	public void addElement(String subname, EnumPropElement e);
	
	/**enable/disable property. would not call onEnable, etc.*/
	public void setEnabled(boolean enable);
	
	/**gets property element*/
	@SuppressWarnings("hiding")
	public <T extends IPropElement> T getElement(String subname);
	
	/**
	 * sets the value of this property.
	 * NOTE: this will call {@link IConfigPropHandler#onSetVal}
	 * */
	public void setVal(T val);
	
}
