package stellarium.render;

import net.minecraft.client.Minecraft;
import stellarium.client.ClientSettings;
import stellarium.stellars.StellarManager;

public interface ICelestialLayer {
	
	public void init(ClientSettings settings);
	
	public void render(Minecraft mc, StellarManager manager, float bglight, float weathereff, double time);

}
