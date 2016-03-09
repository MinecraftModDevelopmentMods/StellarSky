package stellarium.stellars.view;

import net.minecraft.world.World;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EProjection;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.SpCoord;

public interface IStellarViewpoint {
	public void update(World world);
	
	public EProjection getProjection(float partialTicks);
	
	public void applyAtmRefraction(SpCoord coord);
	
	public void disapplyAtmRefraction(SpCoord coord);
	
	public double getAirmass(IValRef<EVector> vector);
}
