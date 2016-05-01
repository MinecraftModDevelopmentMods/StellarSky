package stellarium.client.gui.content.button;

import net.minecraft.client.Minecraft;
import stellarium.client.EnumKey;
import stellarium.client.gui.content.IGuiBasicElement;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;

public class GuiButtonElement implements IGuiBasicElement<IButtonController> {

	private IButtonController controller;
	
	@Override
	public void initialize(IButtonController controller) {
		this.controller = controller;
	}

	@Override
	public void updateElement() { }

	@Override
	public boolean mouseClicked(float mouseX, float mouseY, int eventButton) {
		IRectangleBound bound = controller.getClipBound();
        if(controller.canClick(eventButton))
    		if(bound.isInBound(mouseX, mouseY))
    			return controller.onClicked(eventButton);
        
        return false;
	}

	@Override
	public boolean mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		IRectangleBound bound = controller.getClipBound();
        if(controller.canClick(eventButton))
    		if(bound.isInBound(mouseX, mouseY))
    			return controller.onClickEnded(eventButton);
        
		return false;
	}

	@Override
	public boolean keyTyped(EnumKey key, char eventChar) {
		return false;
	}

	@Override
	public void render(IRenderer renderer, float mouseX, float mouseY) {
		IRectangleBound clipBound = controller.getClipBound();
		if(clipBound.isEmpty())
			return;
		
		boolean mouseOver = clipBound.isInBound(mouseX, mouseY);
		
		renderer.startRender();
		controller.setupRenderer(mouseOver, renderer);
		String overlay = controller.setupOverlay(mouseOver, renderer);
		if(overlay != null)
			renderer.render(overlay, controller.getElementBound(), clipBound);
		
		String main = controller.setupMain(mouseOver, renderer);
		renderer.render(main, controller.getElementBound(), clipBound);
		renderer.endRender();
	}
}