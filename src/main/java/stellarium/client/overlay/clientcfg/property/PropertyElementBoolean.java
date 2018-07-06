package stellarium.client.overlay.clientcfg.property;

import java.util.List;

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

public class PropertyElementBoolean implements IHierarchyElement {

	private Property property;
	private boolean current, isDirty = false;
	private ICfgHierarchyHandler handler;
	private ICfgTooltipHandler tooltip;
	
	public PropertyElementBoolean(Property property, ICfgHierarchyHandler handler, ICfgTooltipHandler tooltip) {
		this.property = property;
		this.handler = handler;
		this.tooltip = tooltip;
		
		this.current = property.getBoolean();
	}
	
	@Override
	public GuiElement generateGui(boolean isHorizontal, IRollHelper helper) {
		GuiElement button = tooltip.wrapElement(new GuiElement<IButtonController>(new GuiButtonSimple(),
				new BooleanButtonController(isHorizontal)),
				tooltip.defaultTooltip(property));
		return button;
	}

	@Override
	public boolean hasRoll() {
		return false;
	}
	
	@Override
	public float getSize() {
		return CfgConstants.ELEMENT_SIZE;
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
	
	public class BooleanButtonController implements IButtonController {
		private boolean isHorizontal;
		
		public BooleanButtonController(boolean isHorizontal) {
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
			return property.getLanguageKey() + ModelMain.SEPARATOR + current;
		}
	}

	
	@Override
	public List<IHierarchyElement> generateChildElements() {
		return null;
	}

	@Override
	public int rollDuration() {
		return 0;
	}

	@Override
	public boolean needUpdate() {
		return false;
	}

	@Override
	public String setupBackground(boolean isHorizontal, IRenderer renderer) {
		return handler.setupBackground(isHorizontal, renderer);
	}
	
	public static class Type implements IPropertyElementType {
		@Override
		public boolean accept(Property property) {
			return true;
		}

		@Override
		public boolean useRollable() {
			return false;
		}

		@Override
		public IHierarchyElement generate(Property property, ICfgHierarchyHandler handler, ICfgTooltipHandler tooltip) {
			return new PropertyElementBoolean(property, handler, tooltip);
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
