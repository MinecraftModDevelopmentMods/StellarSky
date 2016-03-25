package stellarium.render;

import stellarium.client.ClientSettings;
import stellarium.stellars.StellarManager;

public interface ICelestialLayer {
	
	public void init(ClientSettings settings);
	
	public void render(StellarManager manager, StellarRenderInfo info);

}
