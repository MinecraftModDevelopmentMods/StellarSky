package stellarium.objs.mv.cbody;

import stellarium.mech.OpFilter;
import stellarium.mech.Wavelength;
import stellarium.objs.IStellarObj;
import stellarium.objs.mv.additive.CAdditive;
import stellarium.render.ISObjRenderer;

public class CBodyRenderer implements ISObjRenderer {

	@Override
	public void render(IStellarObj obj, double radVsRes, double brightness,
			OpFilter filter) {
		
		if(obj instanceof CBody)
		{
			CBody ob = (CBody) obj;
			ob.getCBodyType().getCBodyRenderer().render(ob, radVsRes, brightness, filter);
			
			if(ob.getEntry().hasAdditive())
			{
				CAdditive add = ob.getEntry().additive();
				add.getAdditiveType().getCAdditiveRenderer().render(add, radVsRes, brightness, filter);
			}
		}
				
	}

}
