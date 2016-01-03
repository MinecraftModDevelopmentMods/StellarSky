package stellarium;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class StellarFMLEventHook {
	@SubscribeEvent
	public void onSyncConfig(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.modID == StellarSky.modid)
			StellarSky.proxy.getCfgManager().syncFromGUI();
	}
}
