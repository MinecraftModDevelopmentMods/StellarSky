package stellarium.config.proptype;

import stellarium.config.IConfigPropHandler;
import stellarium.config.IMConfigProperty;
import stellarium.config.element.EnumPropElement;
import stellarium.config.element.IStringElement;

public class StringPropHandler implements IConfigPropHandler<String> {

	@Override
	public void onConstruct(IMConfigProperty<String> prop) {
		prop.addElement(prop.getName(), EnumPropElement.String);
	}

	@Override
	public String getValue(IMConfigProperty<String> prop) {
		IStringElement el = prop.getElement(prop.getName());
		return el.getValue();
	}

	@Override
	public void onSetVal(IMConfigProperty<String> prop, String val) {
		IStringElement el = prop.getElement(prop.getName());
		el.setValue(val);
	}

}
