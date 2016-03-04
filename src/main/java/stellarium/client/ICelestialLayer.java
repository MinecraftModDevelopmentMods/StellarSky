package stellarium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public interface ICelestialLayer {
	
	public void init(ClientSettings settings);
	
	public void render(Minecraft mc, float bglight, float weathereff, double time);

}
