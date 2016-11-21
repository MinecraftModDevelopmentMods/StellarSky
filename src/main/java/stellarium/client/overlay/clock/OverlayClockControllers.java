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
import stellarapi.lib.gui.PositionWrapped;
import stellarapi.lib.gui.animation.GuiRollableFluent;
import stellarapi.lib.gui.animation.IRollableFluentController;
import stellarapi.lib.gui.button.GuiButtonDraggable;
import stellarapi.lib.gui.button.GuiButtonSimple;
import stellarapi.lib.gui.button.IButtonController;
import stellarapi.lib.gui.button.IButtonDraggableController;
import stellarapi.lib.gui.dynamic.tooltip.GuiHasTooltip;
import stellarapi.lib.gui.dynamic.tooltip.ITooltipElementController;
import stellarapi.lib.gui.dynamic.tooltip.StringFormat;
import stellarapi.lib.gui.list.GuiHasFixedList;
import stellarapi.lib.gui.list.IHasFixedListController;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.font.ModelFont;
import stellarapi.lib.gui.scroll.GuiScrollBar;
import stellarapi.lib.gui.scroll.IScrollBarController;
import stellarapi.lib.gui.simple.GuiEmptyElement;
import stellarapi.lib.gui.simple.ISimpleController;
import stellarapi.lib.gui.simple.ISimpleRenderController;
import stellarapi.lib.gui.spacing.GuiSimpleSpacing;
import stellarapi.lib.gui.spacing.ISimpleSpacingController;
import stellarapi.lib.gui.text.GuiTextField;
import stellarapi.lib.gui.text.ITextFieldController;
import stellarapi.lib.gui.text.ITextInternalController;
import stellarium.client.overlay.clock.model.ModelFixButton;
import stellarium.client.overlay.clock.model.ModelGradientScrollRegion;
import stellarium.client.overlay.clock.model.ModelHourFormat;
import stellarium.client.overlay.clock.model.ModelRollButtonWithoutClick;
import stellarium.client.overlay.clock.model.ModelScrollButton;
import stellarium.client.overlay.clock.model.ModelTextShadowButton;
import stellarium.client.overlay.clock.model.ModelViewMode;

public class OverlayClockControllers {

	private ClockSettings settings;
	private RollController roll;
	private boolean forceRoll = true;
	private float rollState;
	private boolean isDirty = false;
	
	private boolean hourFormatAsDay = true;
	private boolean updateFormat = false;
	
	private ModelFont font = new ModelFont(false);
	
	private static final int ANIMATION_DURATION = 5;
	private static final float ROW_HEIGHT = 20;
	private static final float ADDBTN_HEIGHT = 8;
	private static final float BUTTON_WIDTH = 20;
	private static final float SCROLL_WIDTH = 60;
	private static final float SCROLL_BTN_SIZE = 10;
	private static final float SCROLL_REGION_HEIGHT = 5;

	public OverlayClockControllers(ClockSettings settings) {
		this.settings = settings;
	}

	public GuiElement generateElement(GuiHasTooltip tooltipRegistry) {
		GuiElement simpleSpace = new GuiElement<ISimpleController>(new GuiEmptyElement(), new ISimpleController(){});

		GuiElement fix = tooltipRegistry.wrapElement(
				new GuiElement<IButtonController>(new GuiButtonSimple(), new FixController()),
				"config.property.gui.clock.fix.tooltip");
		GuiElement mode = tooltipRegistry.wrapElement(
				new GuiElement<IButtonController>(new GuiButtonSimple(), new ModeController()),
				"config.property.gui.clock.modeview.tooltip1" + "\n"
						+ "config.property.gui.clock.modeview.tooltip2" + "\n"
						+ "config.property.gui.clock.modeview.tooltip3");
		GuiElement textShadow = tooltipRegistry.wrapElement(
				new GuiElement<IButtonController>(new GuiButtonSimple(), new TextShadowController()),
				"config.property.gui.clock.shadow.tooltip");
		GuiElement transparency = tooltipRegistry.wrapElement(
				new GuiElement<IScrollBarController>(new GuiScrollBar(SCROLL_BTN_SIZE, SCROLL_REGION_HEIGHT, SCROLL_BTN_SIZE), new TransparencyController()),
				"config.property.gui.clock.transparency.tooltip");

		GuiElement hourFormat = tooltipRegistry.wrapElement(
				new GuiElement<IButtonController>(new GuiButtonSimple(), new HourFormatController()),
				"config.property.gui.clock.hourformat.tooltip");
		GuiElement dayToHour = tooltipRegistry.wrapElement(
				new GuiElement<ITextFieldController>(new GuiTextField(), new TextFieldHourLength()),
				new ITooltipElementController() {
					@Override
					public boolean canDisplayTooltip() { return true; }
					@Override
					public int getTooltipDisplayWaitTime() { return 800; }
					@Override
					public StringFormat getTooltipInfo(float ratioX, float ratioY) {
						return new StringFormat(hourFormatAsDay? "config.property.gui.clock.daytohour.tooltip"
								: "config.property.gui.clock.hourlength.tooltip");
					}
				});
		GuiElement wordBrightness = tooltipRegistry.wrapElement(
				new GuiElement<IScrollBarController>(new GuiScrollBar(SCROLL_BTN_SIZE, SCROLL_REGION_HEIGHT, SCROLL_BTN_SIZE), new WordBrightnessController()),
				"config.property.gui.clock.wordbrightness.tooltip");

		GuiElement wordHue = tooltipRegistry.wrapElement(
				new GuiElement<IScrollBarController>(new GuiScrollBar(SCROLL_BTN_SIZE, SCROLL_REGION_HEIGHT, SCROLL_BTN_SIZE), new WordHueController()),
				"config.property.gui.clock.wordhue.tooltip");
		GuiElement wordSaturation = tooltipRegistry.wrapElement(
				new GuiElement<IScrollBarController>(new GuiScrollBar(SCROLL_BTN_SIZE, SCROLL_REGION_HEIGHT, SCROLL_BTN_SIZE), new WordSaturationController()),
				"config.property.gui.clock.wordsaturation.tooltip");

		GuiElement spacedFix = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacing(fix), new ButtonSpacingController());
		GuiElement spacedMode = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacing(mode), new ButtonSpacingController());
		GuiElement spacedShadow = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacing(textShadow), new ButtonSpacingController());

		GuiElement spacedHourFormat = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacing(hourFormat), new ButtonSpacingController());


		GuiElement row1 = new GuiElement<IHasFixedListController>(
				new GuiHasFixedList(simpleSpace,
						Pair.of(spacedFix, BUTTON_WIDTH),
						Pair.of(spacedMode, BUTTON_WIDTH),
						Pair.of(spacedShadow, BUTTON_WIDTH),
						Pair.of(transparency, SCROLL_WIDTH)), new RowController());
		GuiElement spacedRow1 = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacing(row1), new RowSpacingController());

		GuiElement row2 = new GuiElement<IHasFixedListController>(
				new GuiHasFixedList(simpleSpace,
						Pair.of(spacedHourFormat, BUTTON_WIDTH),
						Pair.of(dayToHour, SCROLL_WIDTH - BUTTON_WIDTH),
						Pair.of(wordBrightness, SCROLL_WIDTH)), new RowController());
		GuiElement spacedRow2 = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacing(row2), new RowSpacingController());

		GuiElement row3 = new GuiElement<IHasFixedListController>(
				new GuiHasFixedList(simpleSpace,
						Pair.of(wordHue, SCROLL_WIDTH),
						Pair.of(wordSaturation, SCROLL_WIDTH)), new RowController());
		GuiElement spacedRow3 = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacing(row3), new RowSpacingController());


		GuiElement inner = new GuiElement<IHasFixedListController>(
				new GuiHasFixedList(simpleSpace, 
						Pair.of(spacedRow1, ROW_HEIGHT),
						Pair.of(spacedRow2, ROW_HEIGHT),
						Pair.of(spacedRow3, ROW_HEIGHT)),
				new InnerBackgroundController());

		GuiElement rollButton = new GuiElement<IButtonDraggableController>(new GuiButtonDraggable(), new RollButtonController());

		return new GuiElement<IRollableFluentController>(new GuiRollableFluent(inner, rollButton, ADDBTN_HEIGHT), this.roll = new RollController());
	}
	
	public boolean checkDirty() {
		boolean flag = this.isDirty;
		this.isDirty = false;
		return flag;
	}
	
	public class ButtonSpacingController implements ISimpleSpacingController {
		@Override
		public String setupSpacingRenderer(IRenderer renderer) { return null; }
		@Override
		public float getSpacingX() { return 1.0f; }
		@Override
		public float getSpacingY() { return 1.0f; }
	}
	
	public class RowSpacingController implements ISimpleSpacingController {
		@Override
		public String setupSpacingRenderer(IRenderer renderer) {
			return null;
		}
		@Override
		public float getSpacingX() { return 10.0f; }
		@Override
		public float getSpacingY() { return 0.0f; }
	}
	
	public class RowController implements IHasFixedListController {
		@Override
		public boolean isHorizontal() { return true; }
		@Override
		public String setupRenderer(IRenderer renderer) {
			return null;
		}
		@Override
		public boolean isModifiableFirst() { return false; }
		@Override
		public IGuiPosition wrapFixedPosition(IGuiPosition position, IGuiPosition listPos) { return position; }
		@Override
		public IGuiPosition wrapModifiablePosition(IGuiPosition position, IGuiPosition listPos) { return position; }
	}
	
	public class InnerBackgroundController implements IHasFixedListController {
		@Override
		public boolean isHorizontal() { return false; }
		@Override
		public String setupRenderer(IRenderer renderer) {
			renderer.bindModel(ModelSimpleRect.getInstance());
			renderer.color(0.5f, 0.5f, 0.5f, settings.alpha);
			return "";
		}
		@Override
		public boolean isModifiableFirst() { return true; }
		@Override
		public IGuiPosition wrapFixedPosition(IGuiPosition position, IGuiPosition listPos) { return position; }
		@Override
		public IGuiPosition wrapModifiablePosition(IGuiPosition position, IGuiPosition listPos) { return position; }
	}
	
	public class RollController implements IRollableFluentController {
		@Override
		public boolean isHorizontal() { return false; }

		@Override
		public boolean increaseCoordOnRoll() { return false; }
		
		@Override
		public boolean isRollStateIndependent() { return false; }
		
		@Override
		public boolean disableControlOnAnimating() { return false; }

		@Override
		public boolean forceState() {
			boolean cache = forceRoll;
			forceRoll = false;
			return cache;
		}

		@Override
		public float rollState() {
			return rollState;
		}

		@Override
		public float rollRatePerTick() {
			return 1.0f / ANIMATION_DURATION;
		}

		@Override
		public IGuiPosition wrapExcludedPosition(IGuiPosition position, IGuiPosition rollPos) {
			return new PositionWrapped(position, rollPos, "drag", true);
		}
	}

	public class RollButtonController implements IButtonDraggableController {
		
		private float startRollState;
		
		@Override
		public boolean canClick(int eventButton) {
			return eventButton == 0;
		}
		
		@Override
		public void onDragStart(int eventButton, float dragRatioX, float dragRatioY) {
			this.startRollState = rollState = dragRatioY;
		}

		@Override
		public void onDragging(float dragRatioX, float dragRatioY) {
			rollState = dragRatioY;
		}

		@Override
		public void onDragEnded(int eventButton, float dragRatioX, float dragRatioY) {
			if(Math.abs(this.startRollState - dragRatioY) < 0.01f)
				dragRatioY = 1.0f - dragRatioY;
			rollState = dragRatioY < 0.5f? 0.0f : 1.0f;
		}
		
		@Override
		public void setupButton(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelRollButtonWithoutClick.getInstance());
			renderer.color(1.0f, 1.0f, 1.0f, settings.alpha * 0.7f + 0.3f);
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			return null;
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			return "vertical";
		}
	}
	
	public class FixController implements IButtonController {
		@Override
		public boolean canClick(int eventButton) {
			return eventButton == 0;
		}

		@Override
		public void onClicked(int eventButton) {
			settings.isFixed = !settings.isFixed;
			isDirty = true;
		}

		@Override
		public void onClickEnded(int eventButton) { }

		@Override
		public void setupRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelFixButton.getInstance());
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {			
			if(mouseOver)
				return "select";
			else return null;
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			return settings.isFixed? "fixed" : "unfixed";
		}
	}
	
	public class ModeController implements IButtonController {		
		@Override
		public boolean canClick(int eventButton) {
			return eventButton == 0;
		}

		@Override
		public void onClicked(int eventButton) {
			settings.viewMode = settings.viewMode.nextMode();
			isDirty = true;
		}

		@Override
		public void onClickEnded(int eventButton) { }

		@Override
		public void setupRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelViewMode.getInstance());
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			if(mouseOver)
				return "select";
			return null;
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			return settings.viewMode.getName();
		}
	}
	
	public class TextShadowController implements IButtonController {		
		@Override
		public boolean canClick(int eventButton) {
			return eventButton == 0;
		}

		@Override
		public void onClicked(int eventButton) {
			settings.textShadow = !settings.textShadow;
			isDirty = true;
		}

		@Override
		public void onClickEnded(int eventButton) { }

		@Override
		public void setupRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelTextShadowButton.getInstance());
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			if(mouseOver)
				return "select";
			return null;
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			renderer.pushSettingTillNextRender();
			renderer.color((float)settings.wordBrightness,
					(float)settings.wordBrightness,
					(float)settings.wordBrightness, 1.0f);
			return settings.textShadow? "shadow" : "default";
		}
	}
	
	public class TransparencyController extends ScrollController implements IScrollBarController {

		private ModelGradientScrollRegion model = new ModelGradientScrollRegion()
				.setColorRGBALeft(1.0f, 1.0f, 1.0f, 0.0f)
				.setColorRGBARight(1.0f, 1.0f, 1.0f, 1.0f);
		
		@Override
		public float initialProgress() {
			return settings.alpha;
		}

		@Override
		public void progressUpdating(float currentProgress) {
			settings.alpha = currentProgress;
			isDirty = true;
		}

		@Override
		public void progressUpdated(float currentProgress) {
			settings.alpha = currentProgress;
			isDirty = true;
		}

		@Override
		public float getSpacing() {
			return 2.0f;
		}
		
		@Override
		public void setupRegionRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(this.model);
		}
	}
	
	public class HourFormatController implements IButtonController {		
		@Override
		public boolean canClick(int eventButton) {
			return eventButton == 0;
		}

		@Override
		public void onClicked(int eventButton) {
			hourFormatAsDay = !hourFormatAsDay;
			updateFormat = true;
			isDirty = true;
		}

		@Override
		public void onClickEnded(int eventButton) { }

		@Override
		public void setupRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelHourFormat.getInstance());
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			if(mouseOver)
				return "select";
			else return null;
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			renderer.pushSettingTillNextRender();
			renderer.color((float)settings.wordBrightness,
					(float)settings.wordBrightness,
					(float)settings.wordBrightness, 1.0f);
			return hourFormatAsDay? I18n.format("hud.text.dayequal") : I18n.format("hud.text.hourequal");
		}
	}
	
	public class TextFieldHourLength implements ITextFieldController {
				
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
			return 1.0f;
		}

		@Override
		public float getSpacingY() {
			return 1.0f;
		}
		
		public class Internal implements ITextInternalController {
			@Override
			public int maxStringLength() {
				return 6;
			}

			@Override
			public IFontHelper getFontHelper() {
				return font;
			}

			@Override
			public boolean canModify() {
				return true;
			}

			@Override
			public boolean canLoseFocus() {
				return true;
			}

			@Override
			public void notifySelection(int cursor, int selection) { }

			@Override
			public String updateText(String text) {
				if(!updateFormat) {
					try {
						double value = Double.parseDouble(text.trim());
						if(hourFormatAsDay)
							settings.daylengthToHour = value;
						else {
							double daylength = PeriodHelper.getDayPeriod(Minecraft.getMinecraft().world).getPeriodLength();
							settings.daylengthToHour = daylength / value;
						}

						isDirty = true;
					} catch(NumberFormatException exc) { }
				} else updateFormat = false;
				
				if(hourFormatAsDay) {
					return String.format("%3.2f", settings.daylengthToHour);
				} else {
					CelestialPeriod day = PeriodHelper.getDayPeriod(Minecraft.getMinecraft().world);
					if(day != null) {
						double daylength = day.getPeriodLength();
						return String.format("%5f", daylength / settings.daylengthToHour);
					} else return text;
				}
			}
			
			@Override
			public boolean notifyText(String text, int cursor, int selection, boolean focused) {
				if(!text.matches("[0-9.]*"))
					return true;
				return false;
			}

			@Override
			public float getCursorSpacing() {
				return 0.5f;
			}

			@Override
			public void setupRendererFocused(IRenderer renderer) {
				renderer.bindModel(font);
			}

			@Override
			public void setupText(String text, IRenderer renderer) {
				//renderer.bindModel(font);
				float brightnessNew = (float)settings.wordBrightness;
				renderer.pushSettingTillNextRender();
				renderer.color(brightnessNew,
						brightnessNew,
						brightnessNew, 1.0f);
			}

			@Override
			public void setupHighlightedText(String selection, IRenderer renderer) {
				renderer.pushSettingTillNextRender();
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
				renderer.bindModel(ModelSimpleRect.getInstance());
				renderer.color(1.5f, 1.5f, 1.5f, ((cursorCounter/6)&1) == 0? 1.0f : 0.1f);
				return "";
			}

			@Override
			public String setupRendererUnfocused(String text, IRenderer renderer) {
				renderer.bindModel(font);
				renderer.color((float)settings.wordBrightness,
						(float)settings.wordBrightness,
						(float)settings.wordBrightness, 1.0f);
				return text + (hourFormatAsDay?
						I18n.format("hud.text.unit.hour") : I18n.format("hud.text.unit.tick"));
			}
		}
	}
	
	public abstract class ScrollController implements IScrollBarController {
		@Override
		public boolean isHorizontal() {
			return true;
		}

		@Override
		public boolean canHandle(int eventButton) {
			return eventButton == 0;
		}

		@Override
		public boolean moveCenterOnClick() {
			return true;
		}
		
		@Override
		public boolean isRegionCenterToCenter() {
			return true;
		}

		@Override
		public float getSpacing() {
			return 2.0f;
		}

		@Override
		public String setupBackgroundRenderer(IRenderer renderer) {
			return null;
		}

		
		@Override
		public void setupButtonRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelScrollButton.getInstance());
		}

		@Override
		public String setupButtonOverlay(boolean mouseOver, IRenderer renderer) {
			if(!mouseOver)
				return null;
			return "select";
		}

		@Override
		public String setupButtonMain(boolean mouseOver, IRenderer renderer) {
			return "button";
		}

		@Override
		public String setupRegionOverlay(boolean mouseOver, IRenderer renderer) {
			if(!mouseOver)
				return null;
			return "select";
		}

		@Override
		public String setupRegionMain(boolean mouseOver, IRenderer renderer) {
			return "region";
		}
	}
	
	public class WordBrightnessController extends ScrollController implements IScrollBarController {
		private ModelGradientScrollRegion model = new ModelGradientScrollRegion();
		
		@Override
		public float initialProgress() {
			return (settings.wordBrightness - 0.3f) / 0.7f;
		}
		
		@Override
		public void progressUpdating(float currentProgress) {
			settings.wordBrightness = currentProgress * 0.7f + 0.3f;
			isDirty = true;
		}

		@Override
		public void progressUpdated(float currentProgress) {
			settings.wordBrightness = currentProgress * 0.7f + 0.3f;
			isDirty = true;
		}
		
		@Override
		public void setupRegionRenderer(boolean mouseOver, IRenderer renderer) {
			Color color1 = Color.getHSBColor(settings.wordHue,
					settings.wordSaturation, 0.3f);
			Color color2 = Color.getHSBColor(settings.wordHue,
					settings.wordSaturation, 1.0f);
			model.setColorRGBALeft(color1.getRed()/255.0f,
					color1.getGreen()/255.0f,
					color1.getBlue()/255.0f, 1.0f);
			model.setColorRGBARight(color2.getRed()/255.0f,
					color2.getGreen()/255.0f,
					color2.getBlue()/255.0f, 1.0f);
			
			renderer.bindModel(this.model);
		}
	}
	
	public class WordHueController extends ScrollController implements IScrollBarController {
		private ModelGradientScrollRegion model = new ModelGradientScrollRegion();
		
		@Override
		public float initialProgress() {
			return settings.wordHue;
		}
		
		@Override
		public void progressUpdating(float currentProgress) {
			settings.wordHue = currentProgress;
			isDirty = true;
		}

		@Override
		public void progressUpdated(float currentProgress) {
			settings.wordHue = currentProgress;
			isDirty = true;
		}
		
		@Override
		public void setupRegionRenderer(boolean mouseOver, IRenderer renderer) {
			Color color1 = Color.getHSBColor(0.0f,
					settings.wordSaturation, settings.wordBrightness);
			Color color2 = Color.getHSBColor(0.5f,
					settings.wordSaturation, settings.wordBrightness);
			model.setColorRGBALeft(color1.getRed()/255.0f,
					color1.getGreen()/255.0f,
					color1.getBlue()/255.0f, 1.0f);
			model.setColorRGBARight(color2.getRed()/255.0f,
					color2.getGreen()/255.0f,
					color2.getBlue()/255.0f, 1.0f);
			renderer.bindModel(this.model);
		}
	}
	
	public class WordSaturationController extends ScrollController implements IScrollBarController {
		private ModelGradientScrollRegion model = new ModelGradientScrollRegion();
		
		@Override
		public float initialProgress() {
			return settings.wordSaturation;
		}
		
		@Override
		public void progressUpdating(float currentProgress) {
			settings.wordSaturation = currentProgress;
			isDirty = true;
		}

		@Override
		public void progressUpdated(float currentProgress) {
			settings.wordSaturation = currentProgress;
			isDirty = true;
		}
		
		@Override
		public void setupRegionRenderer(boolean mouseOver, IRenderer renderer) {
			Color color1 = Color.getHSBColor(settings.wordHue,
					0.0f, settings.wordBrightness);
			Color color2 = Color.getHSBColor(settings.wordHue,
					1.0f, settings.wordBrightness);
			model.setColorRGBALeft(color1.getRed()/255.0f,
					color1.getGreen()/255.0f,
					color1.getBlue()/255.0f, 1.0f);
			model.setColorRGBARight(color2.getRed()/255.0f,
					color2.getGreen()/255.0f,
					color2.getBlue()/255.0f, 1.0f);
			renderer.bindModel(this.model);
		}
	}

	public void setRollWithForce(boolean rolling) {
		this.rollState = rolling? 0.0f : 1.0f;
		forceRoll = true;
	}
}
