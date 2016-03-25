package stellarium.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public interface ISkyRenderLayer {
	
	public void initialize();
	public void render(float partialTicks, WorldClient world, Minecraft mc);

}
