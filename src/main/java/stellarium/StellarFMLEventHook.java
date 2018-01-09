package stellarium;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class StellarFMLEventHook {
	@SubscribeEvent
	public void onSyncConfig(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(StellarSkyReferences.MODID.equals(event.getModID()))
			StellarSky.INSTANCE.getCelestialConfigManager().syncFromGUI();
	}
}
