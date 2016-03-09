package stellarium.stellars.view;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EProjection;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.SpCoord;

public interface IStellarViewpoint {
	public EProjection getProjection();
	
	public void applyAtmRefraction(SpCoord coord);
	
	public void disapplyAtmRefraction(SpCoord coord);
	
	public double getAirmass(IValRef<EVector> vector);
}
