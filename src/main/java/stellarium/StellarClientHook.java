package stellarium;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class StellarClientHook {
		
	@SubscribeEvent
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(event.type == RenderGameOverlayEvent.ElementType.ALL) {
			int viewMode = StellarSky.getManager().getViewMode();
			if(viewMode == 1)
				return;
			
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

			double currentTick = Minecraft.getMinecraft().theWorld.getWorldTime();
			double time = StellarSky.getManager().getSkyTime(currentTick);
			double date = currentTick / StellarSky.getManager().day + StellarSky.getManager().lattitudeOverworld / 180.0;
			double year = date / StellarSky.getManager().year;
			
			int yr = (int)Math.floor(year);
			int day = (int)Math.floor(date - yr * StellarSky.getManager().year);
			int tick = (int)Math.floor((date - yr * StellarSky.getManager().year - day)*StellarSky.getManager().day);
			
			int second = tick / 20;
			int minute = second / 60;
			second %= 60;
			
			int totalSecond = (int)Math.floor(StellarSky.getManager().day / 20.0);
			int totalMinute = totalSecond / 60;
			totalSecond %= 60;
			
			int yOffset = 0;
			
			this.drawString(fontRenderer, "hud.text.year", 5, 10*(yOffset++)+5, yr);
			this.drawString(fontRenderer, "hud.text.day", 5, 10*(yOffset++)+5, day, StellarSky.getManager().year);
			
			if(viewMode == 2)
				this.drawString(fontRenderer, "hud.text.tick", 5, 10*(yOffset++)+5, tick, StellarSky.getManager().day);
			else this.drawString(fontRenderer, "hud.text.time", 5, 10*(yOffset++)+5, minute, second, totalMinute, totalSecond);
		}
	}
	
	private void drawString(FontRenderer fontRenderer, String str, int x, int y, Object... obj) {
		fontRenderer.drawString(I18n.format(str, obj), x, y, 0xff888888);
	}
}
