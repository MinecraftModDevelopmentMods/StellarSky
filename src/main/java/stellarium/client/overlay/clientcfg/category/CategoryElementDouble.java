package stellarium.client.overlay.clientcfg.category;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.simple.GuiSimpleRenderElement;
import stellarapi.lib.gui.simple.ISimpleRenderController;
import stellarium.client.lib.gui.IHierarchyElement;
import stellarium.client.lib.gui.IRollHelper;
import stellarium.client.overlay.clientcfg.CfgConstants;
import stellarium.client.overlay.clientcfg.ICfgHierarchyHandler;
import stellarium.client.overlay.clientcfg.ICfgTooltipHandler;

/**
 * Category element which contains 2 steps of category,
 * i.e. a category and its subcategories.
 * */
public class CategoryElementDouble implements IHierarchyElement {
	private ConfigCategory category;
	private ICfgHierarchyHandler handler;
	private ICfgTooltipHandler tooltip;
	private boolean isRoot;
	
	public CategoryElementDouble(ConfigCategory category, ICfgHierarchyHandler handler, ICfgTooltipHandler tooltip) {
		this.category = category;
		this.handler = handler;
		this.tooltip = tooltip;
		this.isRoot = false;
	}
	
	public CategoryElementDouble(ConfigCategory category, ICfgHierarchyHandler handler, ICfgTooltipHandler tooltip, boolean isRoot) {
		this.category = category;
		this.handler = handler;
		this.tooltip = tooltip;
		this.isRoot = isRoot;
	}

	@Override
	public List<IHierarchyElement> generateChildElements() {
		List<IHierarchyElement> childs = Lists.newArrayList();
		
		for(ConfigCategory subCategory : category.getChildren()) {
			for(ConfigCategory subSubCategory : subCategory.getChildren()) {
				if(handler.accept(subCategory, subSubCategory)) {
					childs.add(handler.generate(subSubCategory, this.tooltip));
				}
			}
			
			for(Property property : subCategory.getOrderedValues()) {
				if(handler.accept(property)) {
					childs.add(handler.generate(property, this.tooltip));
				}
			}
			childs.add(handler.spacing(CfgConstants.SPACING));
		}

		for(Property property : category.getOrderedValues()) {
			if(handler.accept(property)) {
				childs.add(handler.generate(property, this.tooltip));
			}
		}
		
		return childs;
	}

	@Override
	public GuiElement generateGui(boolean isHorizontal, IRollHelper helper) {
		return tooltip.wrapElement(new GuiElement<ISimpleRenderController>(
				new GuiSimpleRenderElement(), new ContentController(isHorizontal, this.handler)),
				tooltip.defaultTooltip(this.category));
	}
	
	@Override
	public boolean checkSettingsChanged() {
		return false;
	}

	@Override
	public boolean hasRoll() {
		return !this.isRoot;
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
		// TODO update handling
		return false;
	}

	@Override
	public String setupBackground(boolean isHorizontal, IRenderer renderer) {
		return handler.setupBackground(isHorizontal, renderer);
	}
	
	public class ContentController implements ISimpleRenderController {
		private ICfgHierarchyHandler handler;
		private boolean isHorizontal;
		
		public ContentController(boolean isHorizontal, ICfgHierarchyHandler handler) {
			this.handler = handler;
			this.isHorizontal = isHorizontal;
		}
		
		@Override
		public String setupRenderer(IRenderer renderer) {
			handler.setupMainRenderer(this.isHorizontal, renderer);
			return category.getLanguagekey();
		}
	}

	@Override
	public float rollSpacingSize() {
		return CfgConstants.SPACING;
	}

	@Override
	public boolean handleSpacingInElement() {
		return true;
	}

	@Override
	public boolean updateRollOnSpacing(boolean isRolled, boolean clicked, boolean hovering) {
		return !hovering;
	}
}
