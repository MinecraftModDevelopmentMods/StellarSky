package stellarium.view;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import stellarapi.api.ICelestialCoordinates;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarapi.api.optics.Wavelength;

public class ViewerInfo {
	public final Vector3 currentPosition;

	public final ICelestialCoordinates coordinate;
	public final ISkyEffect sky;

	public final double multiplyingPower;

	public final Vector3 colorMultiplier = new Vector3();
	public final double brightnessMultiplier;

	public final Vector3 resolutionColor = new Vector3();
	public final double resolutionGeneral;

	public ViewerInfo(ICelestialCoordinates coordinate, ISkyEffect sky, IViewScope scope, IOpticalFilter filter, Entity viewer) {
		this.coordinate = coordinate;
		this.sky = sky;

		this.currentPosition = new Vector3(viewer.posX, viewer.posY, viewer.posZ);

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

	public float getHeight(World world) {
		return (float)((currentPosition.getY() - world.getHorizon()) / world.getHeight());
	}

}
