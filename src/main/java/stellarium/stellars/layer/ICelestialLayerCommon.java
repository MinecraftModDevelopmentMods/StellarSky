package stellarium.stellars.layer;

import sciapi.api.value.euclidian.EVector;
import stellarium.config.INBTConfig;

public interface ICelestialLayerCommon<T extends INBTConfig> extends ICelestialLayer<T> {
	public boolean provideSun();
	public EVector getSunEcRPos();
	
	public boolean provideMoon();
	public EVector getMoonEcRPos();
	
	/**
	 * @return {LunarMonthLength, MoonPhase, MoonphaseTime}
	 * */
	public double[] getMoonFactors();
}
