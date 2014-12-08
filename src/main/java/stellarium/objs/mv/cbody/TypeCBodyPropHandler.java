package stellarium.objs.mv.cbody;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import stellarium.config.IConfigPropHandler;
import stellarium.config.IMConfigProperty;
import stellarium.config.element.EnumPropElement;
import stellarium.config.element.IEnumElement;
import stellarium.construct.CPropLangStrs;
import stellarium.construct.CTypeRegistry;

public class TypeCBodyPropHandler implements IConfigPropHandler<ICBodyType> {

	@Override
	public void onConstruct(IMConfigProperty<ICBodyType> prop) {
		prop.addElement(prop.getName(), EnumPropElement.Enum);

		IEnumElement pee = prop.getElement(prop.getName());
		
		List<String> nameList = Lists.newArrayList(CTypeRegistry.instance().getRegCBodyTypeNames());
		nameList.add(0, CPropLangStrs.def);
		pee.setValRange(nameList.toArray(new String[0]));
		
	}

	@Override
	public ICBodyType getValue(IMConfigProperty<ICBodyType> prop) {
		IEnumElement pee = prop.getElement(prop.getName());
		if(pee.getIndex() == 0)
			return null;
		return CTypeRegistry.instance().getCBodyType(pee.getValue());
	}

	@Override
	public void onSetVal(IMConfigProperty<ICBodyType> prop, ICBodyType val) {
		IEnumElement pee = prop.getElement(prop.getName());
		if(val == null)
			pee.setValue(0);
		pee.setValue(val.getTypeName());
	}

}
