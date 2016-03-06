package stellarium.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Vec3;

public interface ISkyRenderLayer {
	
	public void render(float partialTicks, WorldClient world, Minecraft mc);

}
