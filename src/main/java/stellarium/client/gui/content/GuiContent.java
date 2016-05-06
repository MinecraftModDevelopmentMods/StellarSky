package stellarium.client.gui.content;

import stellarium.client.PressedKey;

public class GuiContent {
	private IRenderer renderer;
	private GuiElement element;
	private GuiPositionHierarchy positions;
	
	public GuiContent(IRenderer renderer, GuiElement element, IGuiPosition position) {
		this.renderer = renderer;
		this.element = element;
		this.positions = new GuiPositionHierarchy(position);
		
		element.initialize(this.positions);
		positions.initializeBounds();
	}
	
	public void updateTick() {
		positions.updateBounds();
		element.getType().updateElement();
	}
	
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		element.getType().mouseClicked(mouseX, mouseY, eventButton);
	}

	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		element.getType().mouseMovedOrUp(mouseX, mouseY, eventButton);
	}

	public void keyTyped(PressedKey key) {
		element.getType().keyTyped(key);
	}

	public void render(float mouseX, float mouseY, float partialTicks) {
		element.getType().checkMousePosition(mouseX, mouseY);
		positions.updateAnimation(partialTicks);
		element.getType().render(this.renderer);
	}

}
