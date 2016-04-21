package stellarium.world;

import javafx.scene.transform.Rotate;
import net.minecraft.world.World;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.Wavelength;
import stellarium.common.CommonSettings;
import stellarium.stellars.Optics;
import stellarium.util.math.StellarMath;

public class NonRefractiveSkySet implements IStellarSkySet {
	
	private boolean hideObjectsUnderHorizon;
	private float lightPollutionFactor, dispersionFactor;
	
	private Wavelength.WaveInterpolation interpolation;
	
	public NonRefractiveSkySet(PerDimensionSettings settings) {
		this.hideObjectsUnderHorizon = settings.hideObjectsUnderHorizon();
		this.dispersionFactor = (float) settings.getSkyDispersionRate();
		this.lightPollutionFactor = (float) settings.getLightPollutionRate();
		
		this.interpolation = new Wavelength.WaveInterpolation(
				new Wavelength[] {Wavelength.V, Wavelength.B},
				new double[] {StellarMath.MagToLumWithoutSize(Optics.ext_coeff_V),
						StellarMath.MagToLumWithoutSize(Optics.ext_coeff_B_V + Optics.ext_coeff_V)});
	}

	@Override
	public void applyAtmRefraction(SpCoord coord) { }

	@Override
	public void disapplyAtmRefraction(SpCoord coord) { }

	@Override
	public float calculateAirmass(SpCoord coord) {
		return 0.0f;
	}

	@Override
	public boolean hideObjectsUnderHorizon() {
		return this.hideObjectsUnderHorizon;
	}

	@Override
	public float getAbsorptionFactor(float partialTicks) {
		//Assume that there is no absorption.
		return 0.0f;
	}

	@Override
	public float getDispersionFactor(Wavelength wavelength, float partialTicks) {
		return this.dispersionFactor;
	}

	@Override
	public float getExtinctionRate(Wavelength wavelength) {
		return (float) Math.pow(10.0, -interpolation.getInterpolated(wavelength) / 2.5);
	}

	@Override
	public float getLightPollutionFactor(Wavelength wavelength, float partialTicks) {
		return this.lightPollutionFactor;
	}

	@Override
	public double getSeeing(Wavelength wavelength) {
		return 0.02;
	}

}
