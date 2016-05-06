package stellarium.client.gui.content;

import stellarium.client.EnumKey;

public class GuiElement<C extends IElementController> {
	
	private C controller;
	private IGuiElementType<C> type;
	
	public GuiElement(IGuiElementType<C> type, C controller) {
		this.type = type;
		this.controller = controller;
	}
	
	public C getController() {
		return this.controller;
	}
	
	public IGuiElementType<C> getType() {
		return this.type;
	}

	public void initialize(GuiPositionHierarchy position) {
		type.initialize(position, this.controller);
	}

}
