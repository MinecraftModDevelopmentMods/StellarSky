package stellarium.config.element;

public interface IEnumElement extends IPropElement {

	public void setValRange(String... str);
	
	
	public void setValue(int index);
	
	public void setValue(String val);
	
	
	public String getValue();
	
	public int getIndex();
	
}
