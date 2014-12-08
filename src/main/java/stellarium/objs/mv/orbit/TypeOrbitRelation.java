package stellarium.objs.mv.orbit;

import stellarium.config.IConfigCategory;
import stellarium.config.IMConfigProperty;
import stellarium.config.IPropertyRelation;

public class TypeOrbitRelation implements IPropertyRelation {

	IConfigCategory cat;
	
	IMConfigProperty<IOrbitType> prop;
	
	public TypeOrbitRelation(IConfigCategory pcat)
	{
		cat = pcat;
	}

	@Override
	public void setProps(IMConfigProperty... props) {
		prop = props[0];
	}

	@Override
	public void onEnable(int i) {
		if(prop.getVal() != null)
			prop.getVal().removeConfig(cat);
	}

	@Override
	public void onDisable(int i) {
		if(prop.getVal() != null)
			prop.getVal().formatConfig(cat);
	}

	@Override
	public void onValueChange(int i) {
		//Does Nothing!
	}

}
