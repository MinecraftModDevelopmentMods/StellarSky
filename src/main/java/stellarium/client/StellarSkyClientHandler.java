package stellarium.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import stellarium.StellarSky;
import stellarium.api.IHourProvider;
import stellarium.api.StellarSkyAPI;
import stellarium.command.CommandLock;
import stellarium.stellars.StellarManager;
import stellarium.world.StellarDimensionManager;

public class StellarSkyClientHandler {
	
	@SubscribeEvent
	public void onInitGui(InitGuiEvent.Post event) {
		if(event.gui instanceof GuiOptions)
		{
			if(event.gui.mc.theWorld != null) {
				EntityPlayer player = event.gui.mc.thePlayer;
				if(!new CommandLock().canCommandSenderUseCommand(player))
					return;
				
				boolean locked = StellarManager.getManager(true).isLocked();
				EnumLockBtnPosition position = StellarSky.proxy.getClientSettings().getBtnPosition();
				GuiButton guibutton = new GuiButton(30, position.getPosX(event.gui.width), position.getPosY(event.gui.height), 150, 20,
						locked? I18n.format("stellarsky.gui.unlock") : I18n.format("stellarsky.gui.lock"));
				event.buttonList.add(guibutton);
			}
		}
	}
	
	@SubscribeEvent
	public void onButtonActivated(ActionPerformedEvent.Pre event) {
		if(event.gui instanceof GuiOptions)
		{
			if(event.button.id == 30)
			{
				boolean locked = StellarManager.getManager(true).isLocked();
				event.gui.mc.thePlayer.sendChatMessage(String.format("/locksky %s", !locked));
				StellarManager.getManager(true).setLocked(!locked);
				locked = !locked;
				event.button.displayString = locked? I18n.format("stellarsky.gui.unlock") : I18n.format("stellarsky.gui.lock");
			}
		}
	}
}
