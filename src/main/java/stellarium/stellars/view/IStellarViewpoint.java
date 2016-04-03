package stellarium.stellars.view;

import net.minecraft.world.World;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EProjection;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.SpCoord;

public interface IStellarViewpoint {
	
	public void update(World world, double year);
	
	public EProjection getProjection();
	
	public EProjection projectionToEquatorial();
	
	public void applyAtmRefraction(SpCoord coord);
	
	public void disapplyAtmRefraction(SpCoord coord);
	
	public double getAirmass(IValRef<EVector> vector, boolean isRefractionApplied);
	
	public boolean hideObjectsUnderHorizon();
}
