package stellarium.stellars.layer;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.EnumRGBA;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarapi.api.optics.Wavelength;
import stellarium.world.IStellarSkySet;

public final class StellarCacheInfo {
	
	public final Matrix3 projectionToGround;
	public final IOpticalFilter filter;
	
	public final double lgp, mp;
	public final boolean hideObjectsUnderHorizon;
	public final Map<Wavelength, Double> extinctionRate;
	public final Map<Wavelength, Double> resolution;
	
	private final ISkyEffect sky;
	
	public StellarCacheInfo(ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope, IOpticalFilter filter) {
		this.projectionToGround = coordinate.getProjectionToGround();
		this.sky = sky;
		this.filter = filter;
		
		this.hideObjectsUnderHorizon = sky instanceof IStellarSkySet && ((IStellarSkySet) sky).hideObjectsUnderHorizon();
		
		this.lgp = scope.getLGP();
		this.mp = scope.getMP();
		
		ImmutableMap.Builder<Wavelength, Double> extinction = ImmutableMap.builder();
		ImmutableMap.Builder<Wavelength, Double> res = ImmutableMap.builder();
		
		for(EnumRGBA color : EnumRGBA.values()) {
			Wavelength wavelength = Wavelength.colorWaveMap.get(color);
			extinction.put(wavelength, (double)sky.getExtinctionRate(wavelength));
			res.put(wavelength, scope.getResolution(wavelength));
		}
		
		this.extinctionRate = extinction.build();
		this.resolution = res.build();
	}

	public double calculateAirmass(SpCoord appCoord) {
		return sky.calculateAirmass(appCoord);
	}

	public void applyAtmRefraction(SpCoord appCoord) {
		sky.applyAtmRefraction(appCoord);
	}

}
