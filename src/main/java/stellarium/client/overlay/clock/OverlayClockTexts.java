package stellarium.client.overlay.clock;

import java.awt.Color;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.PeriodHelper;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IFontHelper;
import stellarapi.lib.gui.IGuiPosition;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.dynamic.tooltip.GuiHasTooltip;
import stellarapi.lib.gui.list.GuiHasFixedList;
import stellarapi.lib.gui.list.IHasFixedListController;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.font.ModelFont;
import stellarapi.lib.gui.model.font.TextStyle;
import stellarapi.lib.gui.simple.GuiEmptyElement;
import stellarapi.lib.gui.simple.ISimpleController;
import stellarapi.lib.gui.simple.ISimpleRenderController;
import stellarapi.lib.gui.spacing.GuiSimpleSpacing;
import stellarapi.lib.gui.spacing.ISimpleSpacingController;
import stellarapi.lib.gui.text.GuiTextField;
import stellarapi.lib.gui.text.ITextFieldController;
import stellarapi.lib.gui.text.ITextInternalController;

public class OverlayClockTexts {

	private ClockSettings settings;
	private boolean isDirty = false;
	private ModelFont font, fontShaded;
	
	private static final float TEXT_HEIGHT = 10;
	private static final float SPACINGX = 3;
	
	private boolean focused;
	
	private int yr, day;
	private double yearToDay;
	
	private int tick;
	private double daylength;

	private int hour, minute;
	private int totalhour, restMinuteInDay, totalminute;
	
	private float partialTicksBefore = 0.0f;
	
	public OverlayClockTexts(ClockSettings settings) {
		this.settings = settings;
		this.font = new ModelFont(false);
		this.fontShaded = new ModelFont(false);
		fontShaded.setStyle(new TextStyle().setShaded(true));
	}

	public GuiElement generateElement(GuiHasTooltip registry) {
		GuiElement simpleSpace = new GuiElement<ISimpleController>(new GuiEmptyElement(), new ISimpleController(){});
		
		GuiElement textFieldYear = new GuiElement<ITextFieldController>(new GuiTextField(), new TextFieldGeneric(new Year()));
		GuiElement textFieldDay = new GuiElement<ITextFieldController>(new GuiTextField(), new TextFieldGeneric(new Day()));
		GuiElement textFieldAdditional = new GuiElement<ITextFieldController>(new GuiTextField(), new TextFieldGeneric(new Additional()));
		
		GuiElement texts = new GuiElement<IHasFixedListController>(
				new GuiHasFixedList(simpleSpace,
						Pair.of(textFieldYear, TEXT_HEIGHT),
						Pair.of(textFieldDay, TEXT_HEIGHT),
						Pair.of(textFieldAdditional, TEXT_HEIGHT)),
				new InnerController());
		
		GuiElement spacedTexts = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacing(texts),
				new BackgroundSpacingController());
		
		return spacedTexts;
	}
	
	private class BackgroundSpacingController implements ISimpleSpacingController {
		@Override
		public String setupSpacingRenderer(IRenderer renderer) {
			renderer.bindModel(ModelSimpleRect.getInstance());
			renderer.color(0.3f, 0.3f, 0.3f, settings.alpha);
			return "";
		}
		@Override
		public float getSpacingX() { return SPACINGX; }
		@Override
		public float getSpacingY() { return TEXT_HEIGHT/2; }
	}
	
	public boolean checkDirty() {
		boolean flag = this.isDirty;
		this.isDirty = false;
		return flag;
	}
	
	public void update(float partialTicks) {
		if(Minecraft.getMinecraft().world == null)
			return;
		
		if(partialTicks != 0.0f && this.partialTicksBefore == partialTicks)
			return;
		this.partialTicksBefore = partialTicks;
		
		CelestialPeriod periodDay = PeriodHelper.getDayPeriod(Minecraft.getMinecraft().world);
		CelestialPeriod periodYear = PeriodHelper.getYearPeriod(Minecraft.getMinecraft().world);
		
		if(periodDay == null || periodYear == null)
			return;
		
		long currentTick = Minecraft.getMinecraft().world.getWorldTime();
		
		double dayOffset = periodDay.getOffset(currentTick, partialTicks);
		double yearOffset = periodYear.getBiasedOffset(currentTick, partialTicks, 0.25);
		
		this.daylength = periodDay.getPeriodLength();
		double yearlength = periodYear.getPeriodLength();
		this.yearToDay = yearlength / daylength;
		
		double fixedYearOffset = (yearOffset - dayOffset / yearToDay)%1.0;
		double year = currentTick / yearlength - fixedYearOffset;
		
		this.yr = (int)Math.floor(year) + 1 + settings.startingYear;
		this.day = (int)Math.floor(fixedYearOffset * yearToDay + settings.dateOffset) + 1;
		this.tick = (int)Math.floor(dayOffset * daylength);
		
		this.hour = (int)Math.floor(dayOffset * settings.daylengthToHour);
		this.minute = (int)Math.floor(dayOffset * settings.daylengthToHour * 60) - this.hour * 60;

		this.totalhour = (int)Math.floor(settings.daylengthToHour);
		this.totalminute = (int)Math.floor(settings.daylengthToHour*60.0) - this.totalhour * 60;
	}
	
	public class InnerController implements IHasFixedListController {
		@Override
		public boolean isHorizontal() { return false; }
		@Override
		public String setupRenderer(IRenderer renderer) { return null; }
		@Override
		public boolean isModifiableFirst() { return false; }
		@Override
		public IGuiPosition wrapFixedPosition(IGuiPosition position, IGuiPosition listPos) { return position; }
		@Override
		public IGuiPosition wrapModifiablePosition(IGuiPosition position, IGuiPosition listPos) { return position; }
	}
	
	public class Year implements IStringGetter {
		@Override
		public String getString() {
			return I18n.format("hud.text.year", " " + yr);
		}
	}
	
	public class Day implements IStringGetter {
		@Override
		public String getString() {
			String ad = "  ";
			return I18n.format("hud.text.day", String.format(ad + "%-5d", day), String.format("%.2f", yearToDay));
		}
	}
	
	public class Additional implements IStringGetter {
		@Override
		public String getString() {
			switch(settings.viewMode) {
			case TICK:
				return I18n.format("hud.text.tick", String.format("%-6d", tick), String.format("%.2f", daylength));
			case HHMM:
				return I18n.format("hud.text.time",
					String.format("%3d", hour), 
					String.format("%02d ", minute),
					String.format("%3d", totalhour),
					String.format("%02d", restMinuteInDay));
			case AMPM:
				return 2 * hour < totalhour? I18n.format("hud.text.timeam",
						String.format("%3d", processHourAMPM(hour)), 
						String.format("%02d", minute),
						String.format("%3d", totalhour),
						String.format("%02d", restMinuteInDay)) :
							I18n.format("hud.text.timepm",
								String.format("%3d", processHourAMPM(hour - (totalhour + 1) / 2)), 
								String.format("%02d", minute),
								String.format("%3d", totalhour),
								String.format("%02d", restMinuteInDay));
			}

			return "";
		}
		
		private int processHourAMPM(int hour) {
			return hour != 0? hour : (totalhour + 1) / 2;
		}
	}
	
	private interface IStringGetter {
		public String getString();
	}
	
	public class TextFieldGeneric implements ITextFieldController {
		
		private IStringGetter getter;
		
		public TextFieldGeneric(IStringGetter getter) {
			this.getter = getter;
		}
		
		@Override
		public ITextInternalController getTextController() {
			return new Internal();
		}

		@Override
		public ISimpleRenderController getBackground() {
			return null;
		}

		@Override
		public float getSpacingX() {
			return 5.0f;
		}

		@Override
		public float getSpacingY() {
			return 1.0f;
		}
		
		public class Internal implements ITextInternalController {
			@Override
			public int maxStringLength() {
				return Integer.MAX_VALUE;
			}

			@Override
			public IFontHelper getFontHelper() {
				return font;
			}

			@Override
			public boolean canModify() {
				return false;
			}

			@Override
			public boolean canLoseFocus() {
				return true;
			}

			@Override
			public void notifySelection(int cursor, int selection) {
				cursor = 0;
			}

			@Override
			public String updateText(String text) {
				return getter.getString();
			}
			
			@Override
			public boolean notifyText(String text, int cursor, int selection, boolean focused) {
				return !OverlayClockTexts.this.focused;
			}

			@Override
			public float getCursorSpacing() {
				return 0.1f;
			}

			@Override
			public void setupRendererFocused(IRenderer renderer) {
				renderer.bindModel(getCurrentModel());
			}

			@Override
			public void setupText(String text, IRenderer renderer) {
				//renderer.bindModel(font);
				renderer.pushSettingTillNextRender();
				Color color = Color.getHSBColor(settings.wordHue,
						settings.wordSaturation, settings.wordBrightness-0.1f);
				renderer.color(color.getRed()/255.0f,
						color.getGreen()/255.0f,
						color.getBlue()/255.0f, 1.0f);
			}

			@Override
			public void setupHighlightedText(String selection, IRenderer renderer) {
				renderer.pushSettingTillNextRender();
				Color color = Color.getHSBColor(settings.wordHue,
						settings.wordSaturation, settings.wordBrightness-0.1f);
				renderer.color(color.getRed()/255.0f,
						color.getGreen()/255.0f,
						color.getBlue()/255.0f, 1.0f);
				renderer.color(1.0f, 1.2f, 1.0f, 1.0f);
			}

			@Override
			public String setupHighlightedOverlay(String selection, IRenderer renderer) {
				renderer.bindModel(ModelSimpleRect.getInstance());
				renderer.pushSettingTillNextRender();
				renderer.color(1.0f, 1.0f, 1.0f, 0.2f);
				return "";
			}

			@Override
			public String setupRendererCursor(int cursorCounter, IRenderer renderer) {
				//renderer.bindModel(ModelSimpleRect.getInstance());
				return "";
			}

			@Override
			public String setupRendererUnfocused(String text, IRenderer renderer) {
				update(renderer.getPartialTicks());
				Color color = Color.getHSBColor(settings.wordHue,
						settings.wordSaturation, settings.wordBrightness);
				renderer.bindModel(getCurrentModel());
				renderer.color(color.getRed()/255.0f,
						color.getGreen()/255.0f,
						color.getBlue()/255.0f, 1.0f);
				return text;
			}
		}
	}
	
	private ModelFont getCurrentModel() {
		return settings.textShadow? fontShaded : font;
	}
	
	public void setFocused(boolean focused) {
		this.focused = focused;
	}
}
