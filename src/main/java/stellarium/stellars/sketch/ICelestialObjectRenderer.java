package stellarium.stellars.sketch;

import net.minecraft.client.Minecraft;
import stellarium.stellars.StellarManager;

public interface ICelestialObjectRenderer<Cache extends IRenderCache> {
	
	public void render(Minecraft mc, Cache cache, float bglight, float weathereff, float partialTicks);

}
