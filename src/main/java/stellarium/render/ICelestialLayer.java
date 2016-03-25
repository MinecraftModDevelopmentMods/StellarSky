package stellarium.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import stellarium.client.ClientSettings;
import stellarium.stellars.StellarManager;

public interface ICelestialLayer {
	
	public void init(ClientSettings settings);
	
	public void render(StellarManager manager, StellarRenderInfo info);

}
