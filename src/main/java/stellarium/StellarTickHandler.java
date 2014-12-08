package stellarium;

import java.util.EnumSet;

import stellarium.stellars.StellarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public class StellarTickHandler {
	
	public boolean disabled = false;
	
	@SubscribeEvent
	public void tickStart(TickEvent.ClientTickEvent e) {
		if(disabled)
			return;
		
		if(e.phase == Phase.START){
			World world = Minecraft.getMinecraft().theWorld;
			
			if(world != null)
				StellarManager.Update(world.getWorldTime(), world.provider.isSurfaceWorld());
		}
	}

}
