package stellarium.objs.mv.orbit;

import java.util.List;

import com.google.common.collect.Lists;

import stellarium.config.IConfigPropHandler;
import stellarium.config.IMConfigProperty;
import stellarium.config.element.EnumPropElement;
import stellarium.config.element.IEnumElement;
import stellarium.construct.CPropLangStrs;
import stellarium.construct.CPropLangStrsCBody;
import stellarium.construct.CTypeRegistry;

public class TypeOrbitPropHandler implements IConfigPropHandler<IOrbitType> {

	@Override
	public void onConstruct(IMConfigProperty<IOrbitType> prop) {
		prop.addElement(prop.getName(), EnumPropElement.Enum);

		IEnumElement pee = prop.getElement(prop.getName());
		
		List<String> nameList = Lists.newArrayList(CTypeRegistry.instance().getRegOrbTypeNames());
		nameList.remove(CPropLangStrsCBody.storb);
		nameList.add(0, CPropLangStrs.def);
		pee.setValRange(nameList.toArray(new String[0]));
	}

	@Override
	public IOrbitType getValue(IMConfigProperty<IOrbitType> prop) {
		IEnumElement pee = prop.getElement(prop.getName());
		if(pee.getIndex() == 0)
			return null;
		return CTypeRegistry.instance().getOrbType(pee.getValue());
	}

	@Override
	public void onSetVal(IMConfigProperty<IOrbitType> prop, IOrbitType val) {
		IEnumElement pee = prop.getElement(prop.getName());
		if(val == null)
			pee.setValue(0);
		pee.setValue(val.getTypeName());
	}

}
