package stellarium.render;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import stellarium.api.EnumSkyProperty;
import stellarium.api.ISkyRenderType;
import stellarium.api.ISkyType;

public class SkyTypeEnd implements ISkyType {

	private List<ISkyRenderType> types = Lists.<ISkyRenderType>newArrayList(new SkyRenderTypeEnd());

	@Override
	public void addRenderType(ISkyRenderType type) {
		if(type.getName().contains("Overworld"))
			return;
		
		for(ISkyRenderType typeIn : this.types) {
			if(type.getName().equals(typeIn.getName()))
				return;
		}
		
		types.add(type);
	}
	
	private boolean updateFlag = true;

	@Override
	public boolean needUpdate() {
		if(this.updateFlag) {
			this.updateFlag = false;
			return true;
		} else return false;
	}

	@Override
	public ImmutableList<ISkyRenderType> possibleTypes() {
		return ImmutableList.copyOf(this.types);
	}

	@Override
	public double getDefaultDouble(EnumSkyProperty property) {
		switch(property) {
		case Lattitude:
			return -52.5;
		case Longitude:
			return 180.0;
		case SkyDispersionRate:
			return 0.0;
		case SkyRenderBrightness:
			return 0.3;
		default:
			break;
		}
		return 0;
	}

	@Override
	public double[] getDefaultDoubleList(EnumSkyProperty property) {
		if(property == EnumSkyProperty.SkyExtinctionFactors)
			return new double[] {0, 0, 0};
		return null;
	}

	@Override
	public boolean getDefaultBoolean(EnumSkyProperty property) {
		if(property == EnumSkyProperty.HideObjectsUnderHorizon)
			return false;
		else if(property == EnumSkyProperty.AllowRefraction)
			return false;
		return false;
	}

}
