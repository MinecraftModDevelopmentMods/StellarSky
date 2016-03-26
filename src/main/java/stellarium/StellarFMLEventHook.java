package stellarium;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StellarFMLEventHook {
	@SubscribeEvent
	public void onSyncConfig(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.getModID() == StellarSky.modid)
			StellarSky.proxy.getCfgManager().syncFromGUI();
	}
}
