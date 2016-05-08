package stellarium.client.overlay.clock;

import org.apache.commons.lang3.tuple.Pair;

import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IGuiPosition;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.PositionWrapped;
import stellarapi.lib.gui.animation.GuiRollableFluent;
import stellarapi.lib.gui.animation.IRollableFluentController;
import stellarapi.lib.gui.basicmodel.ModelSimpleRect;
import stellarapi.lib.gui.button.GuiButtonDraggable;
import stellarapi.lib.gui.button.GuiButtonSimple;
import stellarapi.lib.gui.button.IButtonController;
import stellarapi.lib.gui.button.IButtonDraggableController;
import stellarapi.lib.gui.list.GuiHasFixedList;
import stellarapi.lib.gui.list.IHasFixedListController;
import stellarapi.lib.gui.scroll.GuiScrollBar;
import stellarapi.lib.gui.scroll.IScrollBarController;
import stellarapi.lib.gui.simple.GuiEmptyElement;
import stellarapi.lib.gui.simple.GuiSimpleSpacingElement;
import stellarapi.lib.gui.simple.ISimpleController;
import stellarapi.lib.gui.simple.ISimpleSpacingController;

public class OverlayClockControllers {

	private ClockSettings settings;
	private RollController roll;
	private boolean forceRoll;
	private float rollState;
	private boolean isDirty = false;
	
	private static final int ANIMATION_DURATION = 5;
	private static final float ADDBTN_HEIGHT = 8;
	private static final float BUTTON_WIDTH = 20;
	private static final float SCROLL_WIDTH = 60;
	private static final float SCROLL_BTN_SIZE = 10;
	private static final float SCROLL_REGION_HEIGHT = 5;
	
	public OverlayClockControllers(ClockSettings settings) {
		this.settings = settings;
	}

	public GuiElement generateElement(int width, int height) {
		GuiElement simpleSpace = new GuiElement<ISimpleController>(new GuiEmptyElement(), new ISimpleController(){});
		GuiElement fix = new GuiElement<IButtonController>(new GuiButtonSimple(), new FixController());
		GuiElement mode = new GuiElement<IButtonController>(new GuiButtonSimple(), new ModeController());
		GuiElement transparency = new GuiElement<IScrollBarController>(new GuiScrollBar(SCROLL_BTN_SIZE, SCROLL_REGION_HEIGHT, SCROLL_BTN_SIZE), new TransparencyController());
		
		GuiElement spacedFix = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacingElement(fix), new ButtonSpacingController());
		GuiElement spacedMode = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacingElement(mode), new ButtonSpacingController());
		
		GuiElement inner = new GuiElement<IHasFixedListController>(
				new GuiHasFixedList(simpleSpace,
						Pair.of(spacedFix, BUTTON_WIDTH),
						Pair.of(spacedMode, BUTTON_WIDTH),
						Pair.of(transparency, SCROLL_WIDTH)), new InnerController());
		GuiElement spacedInner = new GuiElement<ISimpleSpacingController>(new GuiSimpleSpacingElement(inner), new BackgroundSpacingController());
		
		GuiElement rollButton = new GuiElement<IButtonDraggableController>(new GuiButtonDraggable(), new RollButtonController());
		
		return new GuiElement<IRollableFluentController>(new GuiRollableFluent(spacedInner, rollButton, ADDBTN_HEIGHT), this.roll = new RollController());
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
	
	public class BackgroundSpacingController implements ISimpleSpacingController {
		@Override
		public String setupSpacingRenderer(IRenderer renderer) {
			renderer.bindModel(ModelSimpleRect.getInstance());
			renderer.color(0.5f, 0.5f, 0.5f, settings.alpha);
			return "";
		}
		@Override
		public float getSpacingX() { return 10.0f; }
		@Override
		public float getSpacingY() { return 0.0f; }
	}
	
	public class InnerController implements IHasFixedListController {
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
			renderer.bindModel(ModelScroll.getInstance());
			renderer.color(1.0f, 1.0f, 1.0f, settings.alpha * 1.2f);
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
	
	public class TransparencyController implements IScrollBarController {

		@Override
		public boolean isHorizontal() {
			return true;
		}

		@Override
		public float initialProgress() {
			return (settings.alpha - 0.2f) / 0.6f;
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
		public void progressUpdating(float currentProgress) {
			settings.alpha = 0.2f + currentProgress * 0.6f;
			isDirty = true;
		}

		@Override
		public void progressUpdated(float currentProgress) {
			settings.alpha = 0.2f + currentProgress * 0.6f;
			isDirty = true;
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
		public void setupRegionRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelScrollRegion.getInstance());
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

	public void setRollWithForce(boolean rolling) {
		this.rollState = rolling? 0.0f : 1.0f;
		forceRoll = true;
	}
}
