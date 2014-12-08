package stellarium.config.proptype;

import stellarium.config.IConfigPropHandler;
import stellarium.config.IMConfigProperty;
import stellarium.config.element.EnumPropElement;
import stellarium.config.element.IDoubleElement;

public class UDoublePropHandler implements IConfigPropHandler<Double> {

	@Override
	public void onConstruct(IMConfigProperty<Double> prop) {
		prop.addElement(prop.getName(), EnumPropElement.Double);
	}

	@Override
	public Double getValue(IMConfigProperty<Double> prop) {
		IDoubleElement el = prop.getElement(prop.getName());
		return Math.abs(el.getValue());
	}

	@Override
	public void onSetVal(IMConfigProperty<Double> prop, Double val) {
		IDoubleElement el = prop.getElement(prop.getName());
		el.setValue(Math.abs(val));
	}

}
