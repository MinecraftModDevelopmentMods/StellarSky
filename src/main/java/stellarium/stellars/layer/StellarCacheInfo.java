package stellarium.stellars.layer;

import java.util.Map;

import com.google.common.collect.Maps;

import stellarapi.api.ICelestialCoordinates;
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
	private final Map<EnumRGBA, Double> extinctionRate;
	private final Map<EnumRGBA, Double> resolution;
	
	private final ISkyEffect sky;
	
	public StellarCacheInfo(ICelestialCoordinates coordinate, ISkyEffect sky, IViewScope scope, IOpticalFilter filter) {
		this.projectionToGround = coordinate.getProjectionToGround();
		this.sky = sky;
		this.filter = filter;
		
		this.hideObjectsUnderHorizon = sky instanceof IStellarSkySet && ((IStellarSkySet) sky).hideObjectsUnderHorizon();
		
		this.lgp = scope.getLGP();
		this.mp = scope.getMP();
		
		this.extinctionRate = Maps.newEnumMap(EnumRGBA.class);
		this.resolution = Maps.newEnumMap(EnumRGBA.class);
		
		for(EnumRGBA color : EnumRGBA.values()) {
			Wavelength wavelength = Wavelength.colorWaveMap.get(color);
			extinctionRate.put(color, (double)sky.getExtinctionRate(wavelength));
			resolution.put(color, scope.getResolution(wavelength));
		}
	}

	public double calculateAirmass(SpCoord appCoord) {
		return sky.calculateAirmass(appCoord);
	}

	public void applyAtmRefraction(SpCoord appCoord) {
		sky.applyAtmRefraction(appCoord);
	}
	
	public double getExtinctionRate(EnumRGBA color) {
		return extinctionRate.get(color);
	}
	
	public double getResolution(EnumRGBA color) {
		return resolution.get(color);
	}

}
