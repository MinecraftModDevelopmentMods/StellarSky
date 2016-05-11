package stellarium.client.overlay.clientcfg.property;

import java.util.List;

import net.minecraftforge.common.config.Property;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.dynamic.tooltip.ITooltipElementController;
import stellarapi.lib.gui.dynamic.tooltip.StringFormat;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.scroll.GuiScrollBar;
import stellarapi.lib.gui.scroll.IScrollBarController;
import stellarium.client.lib.gui.IHierarchyElement;
import stellarium.client.lib.gui.IRollHelper;
import stellarium.client.overlay.clientcfg.CfgConstants;
import stellarium.client.overlay.clientcfg.ICfgHierarchyHandler;
import stellarium.client.overlay.clientcfg.ICfgTooltipHandler;
import stellarium.client.overlay.clientcfg.model.ModelCfgScrollButton;
import stellarium.client.overlay.clientcfg.model.ModelCfgScrollRegion;

public class SubPropElementDouble implements IHierarchyElement {

	private Property property;
	private ICfgTooltipHandler tooltip;

	private float min, max;
	private float current;
	private boolean isDirty = false;
	
	public SubPropElementDouble(Property property, ICfgHierarchyHandler handler, ICfgTooltipHandler tooltip) {
		this.property = property;

		if(property.getMaxValue() != Double.toString(Double.MAX_VALUE))
			this.max = (float)Double.parseDouble(property.getMaxValue());
		else this.max = 1.0f;
		
		if(property.getMinValue() != Double.toString(Double.MIN_VALUE))
			this.min = (float)Double.parseDouble(property.getMinValue());
		else this.min = 0.0f;
		
		this.tooltip = tooltip;
		this.current = (float) property.getDouble();
	}

	@Override
	public GuiElement generateGui(boolean isHorizontal, IRollHelper helper) {
		isHorizontal = !isHorizontal;
		
		float pre, post;
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
	
	public class Tooltip implements ITooltipElementController {
		private float pre, post;
		private boolean isHorizontal;
		
		public Tooltip(boolean isHorizontal, float pre, float post) {
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
			return CfgConstants.WAIT_TIME_FAST_TOOLTIP;
		}
		@Override
		public StringFormat getTooltipInfo(float ratioX, float ratioY) {
			return new StringFormat("hud.text.double", String.format("%.2f", this.isHorizontal?
					this.pre + ratioX * (this.post - this.pre)
					: this.pre + ratioY * (this.post - this.pre)));
		}
	}
	
	public class ScrollController implements IScrollBarController {
		private float pre, post;
		private boolean isHorizontal;
		
		public ScrollController(boolean isHorizontal, float pre, float post) {
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
			return (current - pre) / (post - pre);
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
			current = this.pre + progress * (this.post - this.pre);
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
			return new SubPropElementDouble(property, handler, tooltip);
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
