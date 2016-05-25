package stellarium.render.atmosphere;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;

public class ViewerInfo {
	
	public final ICelestialCoordinate coordinate;
	public final ISkyEffect sky;
	public final IViewScope scope;
	public final IOpticalFilter filter;
	
	public ViewerInfo(ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope, IOpticalFilter filter) {
		this.coordinate = coordinate;
		this.sky = sky;
		this.scope = scope;
		this.filter = filter;
	}

}
