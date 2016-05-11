package stellarium.client.overlay.clientcfg.property;

import java.util.List;

import com.google.common.collect.ImmutableList;

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

public class PropertyElementRollable implements IHierarchyElement {

	private Property property;
	private ICfgHierarchyHandler handler;
	private IPropertyElementType type;
	private ICfgTooltipHandler tooltip;
	
	public PropertyElementRollable(Property property, ICfgHierarchyHandler handler,
			ICfgTooltipHandler tooltip, IPropertyElementType type) {
		this.property = property;
		this.handler = handler;
		this.tooltip = tooltip;
		this.type = type;
	}
	
	@Override
	public List<IHierarchyElement> generateChildElements() {
		IHierarchyElement subElement = type.generate(this.property, this.handler, this.tooltip);
		return ImmutableList.of(subElement);
	}
	
	@Override
	public GuiElement generateGui(boolean isHorizontal, IRollHelper helper) {
		GuiElement content = tooltip.wrapElement(new GuiElement<ISimpleRenderController>(new GuiSimpleRenderElement(),
				new ContentController(isHorizontal)),
				tooltip.defaultTooltip(this.property));
		return content;
	}
	
	public class ContentController implements ISimpleRenderController {
		private boolean isHorizontal;
		
		public ContentController(boolean isHorizontal) {
			this.isHorizontal = isHorizontal;
		}
		
		@Override
		public String setupRenderer(IRenderer renderer) {
			handler.setupMainRenderer(this.isHorizontal, renderer);
			return property.getLanguageKey();
		}
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
	public boolean checkSettingsChanged() {
		return false;
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

	@Override
	public float rollSpacingSize() {
		//No spacing for lists of sub-elements
		return 0.0f;
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
