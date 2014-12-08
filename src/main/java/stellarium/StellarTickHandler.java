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
	
	Side side;
	
	public StellarTickHandler(Side pside)
	{
		side = pside;
	}

	@SubscribeEvent
	public void tickStart(TickEvent.WorldTickEvent e) {
		if(e.phase == Phase.START && side == Side.SERVER){
			World world = e.world;
			StellarManager.Update(world.getWorldTime(), world.provider.isSurfaceWorld());
		}
	}
	
	@SubscribeEvent
	public void tickStart(TickEvent.ClientTickEvent e) {
		if(e.phase == Phase.START){
			World world = Minecraft.getMinecraft().theWorld;
			
			if(world != null)
				StellarManager.Update(world.getWorldTime(), world.provider.isSurfaceWorld());
		}
	}

}
