package stellarium.config.proptype;

import stellarium.config.IConfigPropHandler;
import stellarium.config.IMConfigProperty;
import stellarium.config.element.EnumPropElement;
import stellarium.config.element.IDoubleElement;
import stellarium.config.element.IIntegerElement;

public class IntegerPropHandler implements IConfigPropHandler<Integer> {

	@Override
	public void onConstruct(IMConfigProperty<Integer> prop) {
		prop.addElement(prop.getName(), EnumPropElement.Integer);
	}

	@Override
	public Integer getValue(IMConfigProperty<Integer> prop) {
		IIntegerElement el = prop.getElement(prop.getName());
		return el.getValue();
	}

	@Override
	public void onSetVal(IMConfigProperty<Integer> prop, Integer val) {
		IIntegerElement el = prop.getElement(prop.getName());
		el.setValue(val);
	}

}
