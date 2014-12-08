package stellarium.objs.mv.cbody;

import stellarium.mech.OpFilter;

public interface ICBodyRenderer {

	public void render(CBody obj, double radVsRes, double brightness, OpFilter filter);
	
}
