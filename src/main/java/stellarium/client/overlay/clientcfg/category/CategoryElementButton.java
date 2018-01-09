package stellarium.client.overlay.clientcfg.category;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.button.GuiButtonSimple;
import stellarapi.lib.gui.button.IButtonController;
import stellarium.client.lib.gui.IHierarchyElement;
import stellarium.client.lib.gui.IRollHelper;
import stellarium.client.overlay.clientcfg.CfgConstants;
import stellarium.client.overlay.clientcfg.ICfgHierarchyHandler;
import stellarium.client.overlay.clientcfg.ICfgTooltipHandler;
import stellarium.client.overlay.clientcfg.model.ModelClick;
import stellarium.client.overlay.clientcfg.model.ModelMain;

public class CategoryElementButton implements IHierarchyElement {
	private ConfigCategory category;
	private Property btnProp;
	private ICfgHierarchyHandler handler;
	private ICfgTooltipHandler tooltip;
	private boolean current, isDirty = false;
	
	public CategoryElementButton(ConfigCategory category, Property btnProp, ICfgHierarchyHandler handler, ICfgTooltipHandler tooltip) {
		this.category = category;
		this.btnProp = btnProp;
		this.handler = handler;
		this.tooltip = tooltip;
		this.current = btnProp.getBoolean();
	}

	@Override
	public List<IHierarchyElement> generateChildElements() {
		List<IHierarchyElement> childs = Lists.newArrayList();
		
		for(ConfigCategory subCategory : category.getChildren()) {
			if(handler.accept(this.category, subCategory)) {
				childs.add(handler.generate(subCategory, this.tooltip));
			}
		}

		for(Property property : category.getOrderedValues()) {
			if(btnProp.getName().equals(property.getName()))
				continue;
			
			if(handler.accept(property)) {
				childs.add(handler.generate(property, this.tooltip));
			}
		}
		
		return childs;
	}

	@Override
	public GuiElement generateGui(boolean isHorizontal, IRollHelper helper) {
		return tooltip.wrapElement(new GuiElement<IButtonController>(
				new GuiButtonSimple(), new ContentBtnController(isHorizontal, this.handler)),
				tooltip.defaultTooltip(this.category));
	}
	
	@Override
	public boolean checkSettingsChanged() {
		if(isDirty) {
			btnProp.set(current);
			isDirty = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean hasRoll() {
		return true;
	}

	@Override
	public float getSize() {
		return CfgConstants.ELEMENT_SIZE;
	}

	@Override
	public int rollDuration() {
		return CfgConstants.WAIT_TIME_ANIMATION;
	}

	@Override
	public boolean needUpdate() {
		// MAYBE update handling
		return false;
	}

	@Override
	public String setupBackground(boolean isHorizontal, IRenderer renderer) {
		return handler.setupBackground(isHorizontal, renderer);
	}
	
	public class ContentBtnController implements IButtonController {
		private ICfgHierarchyHandler handler;
		private boolean isHorizontal;
		
		public ContentBtnController(boolean isHorizontal, ICfgHierarchyHandler handler) {
			this.handler = handler;
			this.isHorizontal = isHorizontal;
		}

		@Override
		public boolean canClick(int eventButton) {
			return eventButton == 0;
		}

		@Override
		public void onClicked(int eventButton) {
			current = !current;
			isDirty = true;
		}

		@Override
		public void onClickEnded(int eventButton) { }

		@Override
		public void setupRenderer(boolean mouseOver, IRenderer renderer) { }

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			renderer.bindModel(ModelClick.getInstance());
			if(mouseOver)
				return "select";
			return null;
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			handler.setupMainRenderer(this.isHorizontal, renderer);
			return category.getLanguagekey() + ModelMain.separator + current;
		}
	}

	@Override
	public float rollSpacingSize() {
		return CfgConstants.SPACING;
	}

	@Override
	public boolean handleSpacingInElement() {
		return false;
	}

	@Override
	public boolean updateRollOnSpacing(boolean isRolled, boolean clicked, boolean hovering) {
		return !hovering;
	}
}
