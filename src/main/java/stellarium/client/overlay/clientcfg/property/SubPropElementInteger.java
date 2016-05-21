package stellarium.client.overlay.clientcfg.property;

import java.util.List;

import net.minecraftforge.common.config.Property;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.dynamic.tooltip.ITooltipElementController;
import stellarapi.lib.gui.dynamic.tooltip.StringFormat;
import stellarapi.lib.gui.scroll.GuiScrollBar;
import stellarapi.lib.gui.scroll.IScrollBarController;
import stellarium.client.lib.gui.IHierarchyElement;
import stellarium.client.lib.gui.IRollHelper;
import stellarium.client.overlay.clientcfg.CfgConstants;
import stellarium.client.overlay.clientcfg.ICfgHierarchyHandler;
import stellarium.client.overlay.clientcfg.ICfgTooltipHandler;
import stellarium.client.overlay.clientcfg.model.ModelCfgScrollButton;
import stellarium.client.overlay.clientcfg.model.ModelCfgScrollRegion;

public class SubPropElementInteger implements IHierarchyElement {

	private Property property;
	private ICfgTooltipHandler tooltip;
	
	private int min, max;
	private int current;
	private boolean isDirty = false;
	
	public SubPropElementInteger(Property property, ICfgTooltipHandler tooltip) {
		this.property = property;
		this.tooltip = tooltip;

		if(property.getMaxValue() != Integer.toString(Integer.MAX_VALUE))
			this.max = Integer.parseInt(property.getMaxValue());
		else this.max = 10;
		
		if(property.getMinValue() != Integer.toString(Integer.MIN_VALUE))
			this.min = Integer.parseInt(property.getMinValue());
		else this.min = 0;
		
		this.current = property.getInt();
	}

	@Override
	public GuiElement generateGui(boolean isHorizontal, IRollHelper helper) {
		isHorizontal = !isHorizontal;
		
		int pre, post;
		if(helper.isDirectionInverted(isHorizontal)) {
			pre = this.max;
			post = this.min;
		} else {
			pre = this.min;
			post = this.max;
		}
		
		GuiElement scrollBar = new GuiElement<IScrollBarController>(
				new GuiScrollBar(CfgConstants.SCROLL_BTN_PER_SIZE, CfgConstants.SCROLL_REGION_SIZE, CfgConstants.SCROLL_BTN_PROG_SIZE),
				new ScrollController(isHorizontal, pre, post));
		return tooltip.wrapElement(scrollBar, new Tooltip(isHorizontal, pre, post));
	}
	
	@Override
	public boolean hasRoll() {
		return true;
	}
	
	@Override
	public float getSize() {
		return CfgConstants.SCROLL_SIZE;
	}
	
	@Override
	public boolean checkSettingsChanged() {
		if(isDirty) {
			property.set(current);
			isDirty = false;
			return true;
		}
		return false;
	}
	
	private static float getProgress(int pre, int post, int current) {
		return ((float)(current - pre)) / (post - pre);
	}
	
	private static int getValue(int pre, int post, float progress) {
		return (int)Math.round(pre + progress * (post - pre));
	}
	
	public class Tooltip implements ITooltipElementController {
		private int pre, post;
		private boolean isHorizontal;
		
		public Tooltip(boolean isHorizontal, int pre, int post) {
			this.pre = pre;
			this.post = post;
			this.isHorizontal = isHorizontal;
		}
		@Override
		public boolean canDisplayTooltip() {
			return true;
		}
		@Override
		public int getTooltipDisplayWaitTime() {
			return CfgConstants.WAIT_TIME_ANIMATION;
		}
		@Override
		public StringFormat getTooltipInfo(float ratioX, float ratioY) {
			return new StringFormat("hud.text.int", this.isHorizontal?
					getValue(this.pre, this.post, ratioX)
					: getValue(this.pre, this.post, ratioY));
		}
	}
	
	public class ScrollController implements IScrollBarController {
		private int pre, post;
		private boolean isHorizontal;
		
		public ScrollController(boolean isHorizontal, int pre, int post) {
			this.isHorizontal = isHorizontal;
			this.pre = pre;
			this.post = post;
		}

		@Override
		public boolean isHorizontal() {
			return this.isHorizontal;
		}

		@Override
		public float initialProgress() {
			return getProgress(this.pre, this.post, current);
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
			return false;
		}

		@Override
		public void progressUpdating(float progress) { }

		@Override
		public void progressUpdated(float progress) {
			current = getValue(this.pre, this.post, progress);
			isDirty = true;
		}

		@Override
		public float getSpacing() {
			return 0.0f;
		}

		@Override
		public String setupBackgroundRenderer(IRenderer renderer) {
			return null;
		}

		@Override
		public void setupRegionRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelCfgScrollRegion.getInstance());
		}

		@Override
		public String setupRegionOverlay(boolean mouseOver, IRenderer renderer) {
			if(mouseOver)
				return "select";
			return null;
		}

		@Override
		public String setupRegionMain(boolean mouseOver, IRenderer renderer) {
			return this.isHorizontal? "horizontal" : "vertical";
		}

		@Override
		public void setupButtonRenderer(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelCfgScrollButton.getInstance());
		}

		@Override
		public String setupButtonOverlay(boolean mouseOver, IRenderer renderer) {
			if(mouseOver)
				return "select";
			return null;
		}

		@Override
		public String setupButtonMain(boolean mouseOver, IRenderer renderer) {
			return "button";
		}
	}

	@Override
	public List<IHierarchyElement> generateChildElements() {
		return null;
	}

	@Override
	public int rollDuration() {
		return CfgConstants.WAIT_TIME_ANIMATION;
	}

	@Override
	public boolean needUpdate() {
		return false;
	}

	@Override
	public String setupBackground(boolean isHorizontal, IRenderer renderer) {
		return null;
	}

	public static class Type implements IPropertyElementType {
		@Override
		public boolean accept(Property property) {
			return true;
		}

		@Override
		public boolean useRollable() {
			return true;
		}

		@Override
		public IHierarchyElement generate(Property property, ICfgHierarchyHandler handler, ICfgTooltipHandler tooltip) {
			return new SubPropElementInteger(property, tooltip);
		}
	}

	@Override
	public float rollSpacingSize() {
		return 0;
	}

	@Override
	public boolean handleSpacingInElement() {
		return false;
	}

	@Override
	public boolean updateRollOnSpacing(boolean isRolled, boolean clicked, boolean hovering) {
		return isRolled;
	}
}