package stellarium.client.gui.content.simple;

import stellarium.client.EnumKey;
import stellarium.client.gui.content.IElementController;
import stellarium.client.gui.content.IGuiBasicElement;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;

public class GuiSimpleRenderElement implements IGuiBasicElement<ISimpleController> {

	private ISimpleController controller;
	
	@Override
	public void initialize(ISimpleController controller) {
		this.controller = controller;
	}

	@Override
	public void updateElement() { }

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) { }

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) { }

	@Override
	public void keyTyped(EnumKey key, char eventChar) { }

	@Override
	public void checkMousePosition(float mouseX, float mouseY) { }
	
	@Override
	public void render(IRenderer renderer) {
		IRectangleBound clipBound = controller.getClipBound();
		if(clipBound.isEmpty())
			return;
		
		String model = controller.setupRenderer(renderer);
		renderer.render(model, controller.getElementBound(), clipBound);
	}

}
