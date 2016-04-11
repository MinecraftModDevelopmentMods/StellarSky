package stellarium.stellars.layer;

import java.io.IOException;

import sciapi.api.value.euclidian.EVector;
import stellarium.config.IConfigHandler;
import stellarium.config.INBTConfig;

public interface ICelestialLayerCommon<T extends INBTConfig, S extends IConfigHandler> extends ICelestialLayer<S> {
	
	public void initializeCommon(boolean isRemote, T config) throws IOException;

	public boolean provideSun();
	public EVector getSunEcRPos();
	
	public boolean provideMoon();
	public EVector getMoonEcRPos();
	
	/**
	 * @return {LunarMonthLength, MoonPhase, MoonphaseTime}
	 * */
	public double[] getMoonFactors();
}
