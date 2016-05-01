package stellarium.client.gui.clock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.PeriodHelper;
import stellarapi.api.lib.math.Spmath;
import stellarium.StellarSky;
import stellarium.StellarSkyResources;
import stellarium.api.IHourProvider;
import stellarium.api.StellarSkyAPI;
import stellarium.client.ClientSettings;
import stellarium.client.EnumKey;
import stellarium.client.gui.EnumOverlayMode;
import stellarium.client.gui.IGuiOverlay;
import stellarium.client.gui.content.button.GuiButtonColorable;
import stellarium.client.gui.content.button.GuiButtonTextured;

public class OverlayClock implements IGuiOverlay<ClockSettings> {
	
	private static final int HEIGHT = 36;
	private static final int ADDBTN_HEIGHT = 10;
	private static final int ADDITIONAL_HEIGHT = 20;
	private static final int ANIMATION_DURATION = 10;
	
	private Minecraft mc;
	private int animationTick = 0;
	private EnumOverlayMode currentMode = EnumOverlayMode.OVERLAY;

	private ClockSettings settings;
	private GuiButtonTextured buttonScroll;
	private GuiButtonColorable buttonFix, buttonToggle;
	
	private boolean scroll = false;
	
	@Override
	public int getWidth() {
		return settings.viewMode.getGuiWidth();
	}

	@Override
	public int getHeight() {
		return currentMode.focused()? HEIGHT + ADDBTN_HEIGHT + (this.scroll? ADDITIONAL_HEIGHT : 0) : HEIGHT;
	}
	
	@Override
	public void initialize(Minecraft mc, ClockSettings settings) {
		this.mc = mc;
		this.settings = settings;
		
		this.buttonScroll = new GuiButtonTextured(0, 0, HEIGHT, 160, ADDBTN_HEIGHT, StellarSkyResources.unroll);
		this.buttonFix = new GuiButtonColorable(1, 0, HEIGHT + ADDBTN_HEIGHT, 80, ADDITIONAL_HEIGHT, settings.isFixed? "Unfix" : "Fix");
		this.buttonToggle = new GuiButtonColorable(2, 80, HEIGHT + ADDBTN_HEIGHT, 80, ADDITIONAL_HEIGHT, settings.viewMode.getName());
		
		buttonScroll.width = this.getWidth();
		buttonToggle.xPosition = buttonToggle.width = buttonFix.width = this.getWidth() / 2;
		buttonFix.xPosition = 0;
		buttonScroll.alpha = 0.5f;
		buttonFix.alpha = 0.5f;
		buttonToggle.alpha = 0.5f;
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
		}
		this.currentMode = mode;
	}

	@Override
	public void updateOverlay() {
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
		
		if(buttonScroll.mousePressed(mc, mouseX, mouseY))
		{
			this.scroll = !this.scroll;
			buttonScroll.displayString = this.scroll? "Roll" : "Unroll";
		}
		
		if(this.scroll && buttonFix.mousePressed(mc, mouseX, mouseY))
		{
			settings.isFixed = !settings.isFixed;
			buttonFix.displayString = settings.isFixed? "Unfix" : "Fix";
			return true;
		}

		if(this.scroll && buttonToggle.mousePressed(mc, mouseX, mouseY)) {
			settings.viewMode = settings.viewMode.nextMode();
			buttonToggle.displayString = settings.viewMode.getName();
			buttonScroll.width = this.getWidth();
			buttonToggle.xPosition = buttonToggle.width = buttonFix.width = this.getWidth() / 2;
			buttonFix.xPosition = 0;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean mouseMovedOrUp(int mouseX, int mouseY, int eventButton) {
		if(!currentMode.focused())
			return false;
		
		if(this.scroll) {
			buttonFix.mouseReleased(mouseX, mouseY);
			buttonToggle.mouseReleased(mouseX, mouseY);
		}

		return false;
	}

	@Override
	public boolean keyTyped(EnumKey key, char eventChar) {
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		CelestialPeriod periodDay = PeriodHelper.getDayPeriod(Minecraft.getMinecraft().theWorld);
		CelestialPeriod periodYear = PeriodHelper.getYearPeriod(Minecraft.getMinecraft().theWorld);
		if(periodDay == null || periodYear == null)
			return;
		
		if(currentMode.focused()) {
			buttonScroll.drawButton(mc, mouseX, mouseY);
			if(this.scroll) {
				buttonFix.drawButton(mc, mouseX, mouseY);
				buttonToggle.drawButton(mc, mouseX, mouseY);
			}
		}
		
		ClientSettings setting = StellarSky.proxy.getClientSettings();

		FontRenderer fontRenderer = mc.fontRenderer;
		
		Gui.drawRect(0, 0, this.getWidth(), HEIGHT, 0x77333333);
		
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
