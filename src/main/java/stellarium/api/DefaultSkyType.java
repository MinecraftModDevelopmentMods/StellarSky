package stellarium.api;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DefaultSkyType implements ISkyType {
	
	private List<ISkyRenderType> types = Lists.<ISkyRenderType>newArrayList(new SkyRenderTypeSurface());

	@Override
	public void addRenderType(ISkyRenderType type) {
		for(ISkyRenderType typeIn : this.types) {
			if(type.getName().equals(typeIn.getName()))
				return;
		}
		
		types.add(type);
	}

	@Override
	public ImmutableList<ISkyRenderType> possibleTypes() {
		return ImmutableList.copyOf(this.types);
	}

	@Override
	public double getDefaultDouble(EnumSkyProperty property) {
		switch(property) {
		case Lattitude:
			return 37.5;
		case Longitude:
			return 0.0;
		case SkyDispersionRate:
			return 1.0;
		case SkyRenderBrightness:
			return 0.2;
		default:
			break;
		}
		return 0;
	}

	@Override
	public double[] getDefaultDoubleList(EnumSkyProperty property) {
		if(property == EnumSkyProperty.SkyExtinctionFactors)
			return new double[] {0.1, 0.2, 0.3};
		return null;
	}

	@Override
	public boolean getDefaultBoolean(EnumSkyProperty property) {
		if(property == EnumSkyProperty.HideObjectsUnderHorizon)
			return true;
		else if(property == EnumSkyProperty.AllowRefraction)
			return true;
		return false;
	}
	
	
	private boolean needUpdate = true;

	@Override
	public boolean needUpdate() {
		if(this.needUpdate) {
			this.needUpdate = false;
			return true;
		}
		return false;
	}

}