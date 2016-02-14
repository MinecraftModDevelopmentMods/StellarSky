package stellarium.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import stellarium.StellarSky;
import stellarium.config.EnumViewMode;

public class StellarClientHook {
		
	@SubscribeEvent
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(event.type == RenderGameOverlayEvent.ElementType.ALL) {
			ClientSettings setting = StellarSky.proxy.getClientSettings();
			EnumViewMode viewMode = setting.getViewMode();
			if(!viewMode.showOnHUD())
				return;
			
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

			double currentTick = Minecraft.getMinecraft().theWorld.getWorldTime();
			double time = StellarSky.getManager().getSkyTime(currentTick);
			double date = currentTick / StellarSky.getManager().day + StellarSky.getManager().latitudeOverworld / 180.0;
			double year = date / StellarSky.getManager().year;
			
			int yr = (int)Math.floor(year);
			int day = (int)Math.floor(date - yr * StellarSky.getManager().year);
			int tick = (int)Math.floor((date - yr * StellarSky.getManager().year - day)*StellarSky.getManager().day);
			
			int minute = (int)Math.floor(tick / setting.minuteLength);
			int hour = minute / setting.anHourToMinute;
			minute %= setting.anHourToMinute;
			
			int totalminute = (int)Math.floor(StellarSky.getManager().day / setting.minuteLength);
			int totalhour = totalminute / setting.anHourToMinute;
			totalminute %= setting.anHourToMinute;
			
			int yOffset = 0;
			
			this.drawString(fontRenderer, "hud.text.year", 5, 10*(yOffset++)+5,
					String.format("%d", yr));
			this.drawString(fontRenderer, "hud.text.day", 5, 10*(yOffset++)+5,
					String.format("%-7d", day),
					String.format("%.2f", StellarSky.getManager().year));
			
			if(viewMode.showTick())
				this.drawString(fontRenderer, "hud.text.tick", 5, 10*(yOffset++)+5,
						String.format("%-6d", tick),
						String.format("%.2f", StellarSky.getManager().day));
			else this.drawString(fontRenderer, "hud.text.time", 5, 10*(yOffset++)+5,
					String.format("%3d", hour), 
					String.format("%02d", minute),
					String.format("%3d", totalhour),
					String.format("%02d", totalminute));
		}
	}
	
	private void drawString(FontRenderer fontRenderer, String str, int x, int y, String... obj) {
		fontRenderer.drawString(I18n.format(str, (Object[])obj), x, y, 0xff888888);
	}
}
