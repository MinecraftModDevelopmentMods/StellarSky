package stellarium.view;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarapi.api.view.IAtmosphereEffect;
import stellarapi.api.view.ICCoordinates;

public class ViewerInfo {
	public final Vector3 currentPosition;

	public final ICCoordinates coordinate;
	public final IAtmosphereEffect sky;

	public final double multiplyingPower;

	public final Vector3 colorMultiplier = new Vector3();
	public final double brightnessMultiplier;

	public ViewerInfo(ICCoordinates coordinate, IAtmosphereEffect sky, Entity viewer) {
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
	}

	public float getHeight(World world) {
		return (float)((currentPosition.getY() - world.getHorizon()) / world.getHeight());
	}

}
