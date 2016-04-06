package stellarium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stellarium.StellarSky;
import stellarium.api.IHourProvider;
import stellarium.api.StellarSkyAPI;
import stellarium.command.CommandLock;
import stellarium.stellars.StellarManager;
import stellarium.stellars.view.StellarDimensionManager;

public class StellarSkyClientHandler {
		
	@SubscribeEvent
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			ClientSettings setting = StellarSky.proxy.getClientSettings();
			EnumViewMode viewMode = setting.getViewMode();
			if(!viewMode.showOnHUD())
				return;
			
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
			StellarManager manager = StellarManager.getManager(true);
			StellarDimensionManager dimManager = StellarDimensionManager.get(Minecraft.getMinecraft().theWorld);
			
			if(dimManager == null)
				return;
			
			double currentTick = Minecraft.getMinecraft().theWorld.getWorldTime();
			double time = manager.getSkyTime(currentTick)+1000.0;
			double daylength = manager.getSettings().day;
			double yearlength = manager.getSettings().year;
			double date = time / daylength + dimManager.getSettings().longitude / 180.0;
			double year = date / yearlength;
			
			int yr = (int)Math.floor(year);
			int day = (int)Math.floor(date - Math.floor(yr * yearlength));
			int tick = (int)Math.floor((date - Math.floor(yr * yearlength) - day)*daylength);
			
			IHourProvider provider = StellarSkyAPI.getCurrentHourProvider();
			
			int hour = provider.getCurrentHour(daylength, tick);
			int minute = provider.getCurrentMinute(daylength, tick, hour);
			
			int totalhour = provider.getTotalHour(daylength);
			int totalminute = provider.getTotalMinute(daylength, totalhour);
			int restMinuteInDay = provider.getRestMinuteInDay(daylength, totalhour);
			
			int yOffset = 0;
			
			this.drawString(fontRenderer, "hud.text.year", 5, 10*(yOffset++)+5,
					String.format("%d", yr));
			this.drawString(fontRenderer, "hud.text.day", 5, 10*(yOffset++)+5,
					String.format("%7d", day),
					String.format("%.2f", yearlength));
			
			if(viewMode.showTick())
				this.drawString(fontRenderer, "hud.text.tick", 5, 10*(yOffset++)+5,
						String.format("%-6d", tick),
						String.format("%.2f", daylength));
			else this.drawString(fontRenderer, "hud.text.time", 5, 10*(yOffset++)+5,
					String.format("%3d", hour), 
					String.format("%02d", minute),
					String.format("%3d", totalhour),
					String.format("%02d", restMinuteInDay),
					String.format("%02d", totalminute));
		}
	}
	
	private void drawString(FontRenderer fontRenderer, String str, int x, int y, String... obj) {
		fontRenderer.drawString(I18n.format(str, (Object[])obj), x, y, 0xff888888);
	}
	
	@SubscribeEvent
	public void onInitGui(InitGuiEvent.Post event) {
		if(event.getGui() instanceof GuiOptions)
		{
			if(event.getGui().mc.theWorld != null) {
				EntityPlayer player = event.getGui().mc.thePlayer;
				if(!new CommandLock().checkPermission(FMLCommonHandler.instance().getMinecraftServerInstance(), player))
					return;
				
				boolean locked = StellarManager.getManager(true).isLocked();
				EnumLockBtnPosition position = StellarSky.proxy.getClientSettings().getBtnPosition();
				GuiButton guibutton = new GuiButton(30, position.getPosX(event.getGui().width), position.getPosY(event.getGui().height), 150, 20,
						locked? I18n.format("stellarsky.gui.unlock") : I18n.format("stellarsky.gui.lock"));
				event.getButtonList().add(guibutton);
			}
		}
	}
	
	@SubscribeEvent
	public void onButtonActivated(ActionPerformedEvent.Pre event) {
		if(event.getGui() instanceof GuiOptions)
		{
			if(event.getButton().id == 30)
			{
				boolean locked = StellarManager.getManager(true).isLocked();
				event.getGui().mc.thePlayer.sendChatMessage(String.format("/locksky %s", !locked));
				StellarManager.getManager(true).setLocked(!locked);
				locked = !locked;
				event.getButton().displayString = locked? I18n.format("stellarsky.gui.unlock") : I18n.format("stellarsky.gui.lock");
			}
		}
	}
}
