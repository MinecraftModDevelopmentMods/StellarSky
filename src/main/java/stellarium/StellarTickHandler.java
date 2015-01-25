package stellarium;

import java.util.EnumSet;

import stellarium.stellars.StellarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public class StellarTickHandler {
	
	@SubscribeEvent
	public void tickStart(TickEvent.ClientTickEvent e) {
		if(e.phase == Phase.START){
			World world = StellarSky.proxy.getDefWorld();
			
			if(world != null)
				StellarSky.getManager().Update(world.getWorldTime(),
						world.provider.isSurfaceWorld());
		}
	}
		
	@SubscribeEvent
	public void tickStart(TickEvent.WorldTickEvent e) {
		if(e.phase == Phase.START){
			if(!(StellarSky.getManager().side == Side.SERVER
					&& StellarSky.getManager().serverEnabled))
				return;
			
			if(e.world != null)
				StellarSky.getManager().Update(e.world.getWorldTime(),
						e.world.provider.isSurfaceWorld());
		}
	}

}
