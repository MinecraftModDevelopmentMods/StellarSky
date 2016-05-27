package stellarium.view;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarapi.api.optics.Wavelength;

public class ViewerInfo {
	
	public final ICelestialCoordinate coordinate;
	public final ISkyEffect sky;
	
	public final double multiplyingPower;
	
	public final Vector3 colorMultiplier = new Vector3();
	public final double brightnessMultiplier;

	public final Vector3 resolutionColor = new Vector3();
	public final double resolutionGeneral;
	
	public ViewerInfo(ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope, IOpticalFilter filter) {
		this.coordinate = coordinate;
		this.sky = sky;
		
		this.multiplyingPower = scope.getMP();
		
		this.brightnessMultiplier = scope.getLGP() * filter.getFilterEfficiency(Wavelength.visible);
		colorMultiplier.set(
				scope.getLGP() * filter.getFilterEfficiency(Wavelength.red),
				scope.getLGP() * filter.getFilterEfficiency(Wavelength.V),
				scope.getLGP() * filter.getFilterEfficiency(Wavelength.B)
				);
		
		
		resolutionColor.set(
				Spmath.Radians(Math.max(scope.getResolution(Wavelength.red), sky.getSeeing(Wavelength.red))),
				Spmath.Radians(Math.max(scope.getResolution(Wavelength.V), sky.getSeeing(Wavelength.V))),
				Spmath.Radians(Math.max(scope.getResolution(Wavelength.B), sky.getSeeing(Wavelength.B)))
				);
		this.resolutionGeneral = Spmath.Radians(Math.max(scope.getResolution(Wavelength.visible), sky.getSeeing(Wavelength.visible)));
	}

}
