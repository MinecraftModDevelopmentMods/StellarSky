package stellarium.client.overlay.clock;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.PeriodHelper;
import stellarapi.api.gui.overlay.EnumOverlayMode;
import stellarapi.api.gui.overlay.IOverlayElement;
import stellarapi.api.lib.math.Spmath;
import stellarapi.lib.gui.GuiContent;
import stellarapi.lib.gui.GuiRenderer;
import stellarapi.lib.gui.IGuiPosition;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.RectangleBound;
import stellarapi.lib.gui.util.GuiUtil;
import stellarium.StellarSky;
import stellarium.api.IHourProvider;
import stellarium.api.StellarSkyAPI;
import stellarium.client.ClientSettings;

public class OverlayClock implements IOverlayElement<ClockSettings> {
	
	private static final int HEIGHT = 36;
	private static final int ADDBTN_HEIGHT = 10;
	private static final int ADDITIONAL_HEIGHT = 20;
	private static final int ANIMATION_DURATION = 10;
	
	private Minecraft mc;
	private int animationTick = 0;
	private EnumOverlayMode currentMode = EnumOverlayMode.OVERLAY;

	private ClockSettings settings;
		
	private GuiContent content;
	private OverlayClockControllers controllers;
	
	@Override
	public int getWidth() {
		return settings.viewMode.getGuiWidth();
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}
	
	@Override
	public void initialize(Minecraft mc, ClockSettings settings) {
		this.mc = mc;
		this.settings = settings;
		
		this.controllers = new OverlayClockControllers(settings);
		this.content = new GuiContent(new GuiRenderer(mc),
				controllers.generateElement(this.getWidth(), ADDBTN_HEIGHT+ADDITIONAL_HEIGHT),
				new IGuiPosition() {
			public RectangleBound theBound;

			@Override
			public IRectangleBound getElementBound() {
				return this.theBound;
			}

			@Override
			public IRectangleBound getClipBound() {
				return this.theBound;
			}

			@Override
			public IRectangleBound getAdditionalBound(String boundName) {
				return null;
			}

			@Override
			public void initializeBounds() {
				this.theBound = new RectangleBound(0.0f, getHeight(), getWidth(), ADDITIONAL_HEIGHT);
			}

			@Override
			public void updateBounds() {
				theBound.set(0.0f, getHeight(), getWidth(), ADDITIONAL_HEIGHT);
			}

			@Override
			public void updateAnimation(float partialTicks) { }
		});
	}

	@Override
	public float animationOffsetX(float partialTicks) {
		return 0.0f;
	}

	@Override
	public float animationOffsetY(float partialTicks) {
		partialTicks = settings.isFixed? 0.0f : currentMode.displayed()? -partialTicks : partialTicks;
		return -this.getHeight() * Math.max((this.animationTick + partialTicks) / ANIMATION_DURATION, 0.0f);
	}

	@Override
	public void switchMode(EnumOverlayMode mode) {
		if(mode.displayed() != currentMode.displayed())
		{
			if(!settings.isFixed && mode.displayed())
				this.animationTick = ANIMATION_DURATION;
			else this.animationTick = 0;
			
			if(!mode.displayed())
				controllers.setRollWithForce(true);
		}
		
		this.currentMode = mode;
	}

	@Override
	public void updateOverlay() {
		content.updateTick();

		if(settings.isFixed) {
			this.animationTick = 0;
			return;
		}
		
		if(this.animationTick > 0 && currentMode.displayed())
			this.animationTick--;
		else if(this.animationTick < ANIMATION_DURATION && !currentMode.displayed())
			this.animationTick++;
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int eventButton) {
		if(!currentMode.focused())
			return false;
		
		content.mouseClicked(mouseX, mouseY, eventButton);
		
		return controllers.checkDirty();
	}

	@Override
	public boolean mouseMovedOrUp(int mouseX, int mouseY, int eventButton) {
		if(!currentMode.focused())
			return false;
		
		content.mouseMovedOrUp(mouseX, mouseY, eventButton);

		return controllers.checkDirty();
	}

	@Override
	public boolean keyTyped(char eventChar, int eventKey) {
		if(!currentMode.focused())
			return false;
		
		content.keyTyped(eventChar, eventKey);
		return controllers.checkDirty();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		CelestialPeriod periodDay = PeriodHelper.getDayPeriod(Minecraft.getMinecraft().theWorld);
		CelestialPeriod periodYear = PeriodHelper.getYearPeriod(Minecraft.getMinecraft().theWorld);
		if(periodDay == null || periodYear == null)
			return;
		
		if(currentMode.focused())
			content.render(mouseX, mouseY, partialTicks);
		
		ClientSettings setting = StellarSky.proxy.getClientSettings();

		FontRenderer fontRenderer = mc.fontRenderer;
		
		GL11.glColor4f(0.3f, 0.3f, 0.3f, settings.alpha);
		GuiUtil.drawRectSimple(0, 0, this.getWidth(), HEIGHT);
		
		long currentTick = Minecraft.getMinecraft().theWorld.getWorldTime();
		
		double dayOffset = periodDay.getOffset(currentTick, partialTicks);
		double yearOffset = Spmath.fmod(periodYear.getOffset(currentTick, partialTicks)+0.25, 1.0);
		
		double daylength = periodDay.getPeriodLength();
		double yearlength = periodYear.getPeriodLength();
		double yearToDay = yearlength / daylength;
		
		double fixedYearOffset = Spmath.fmod(yearOffset - dayOffset / yearToDay, 1.0);
		double year = currentTick / yearlength - fixedYearOffset;
		
		int yr = (int)Math.floor(year) + 2;
		int day = (int)Math.floor(fixedYearOffset * yearToDay) + 1;
		int tick = (int)Math.floor(dayOffset * daylength);
		
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
				String.format("%-5d", day),
				String.format("%.2f", yearToDay));

		if(settings.viewMode.showTick())
			this.drawString(fontRenderer, "hud.text.tick", 5, 10*(yOffset++)+5,
					String.format("%-6d", tick),
					String.format("%.2f", daylength));
		else this.drawString(fontRenderer, "hud.text.time", 5, 10*(yOffset++)+5,
				String.format("%2d", hour), 
				String.format("%02d", minute),
				String.format("%3d", totalhour),
				String.format("%02d", restMinuteInDay),
				String.format("%02d", totalminute));
	}
	
	private void drawString(FontRenderer fontRenderer, String str, int x, int y, String... obj) {
		fontRenderer.drawString(I18n.format(str, (Object[])obj), x, y, 0xffaaaaaa);
	}
}
