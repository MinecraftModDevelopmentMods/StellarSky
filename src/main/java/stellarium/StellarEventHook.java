package stellarium;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.world.WorldEvent;

public class StellarEventHook {
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e)
	{
		if(StellarSky.getManager().side != Side.CLIENT)
			return;
		
		if(e.world.provider.dimensionId == 0 || e.world.provider.dimensionId == -1)
		{
			e.world.provider.setSkyRenderer(new DrawSky());
		}
	}
	
}
