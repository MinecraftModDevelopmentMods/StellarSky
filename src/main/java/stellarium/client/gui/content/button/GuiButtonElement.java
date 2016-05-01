package stellarium.client.gui.content.button;

import net.minecraft.client.Minecraft;
import stellarium.client.EnumKey;
import stellarium.client.gui.content.IGuiBasicElement;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;

public class GuiButtonElement implements IGuiBasicElement<IButtonController> {

	private IButtonController controller;
	private boolean isClicking, mouseOver;
	
	@Override
	public void initialize(IButtonController controller) {
		this.controller = controller;
		this.isClicking = false;
	}

	@Override
	public void updateElement() { }

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		IRectangleBound bound = controller.getClipBound();
        if(controller.canClick(eventButton))
    		if(bound.isInBound(mouseX, mouseY))
    		{
    			this.isClicking = true;
    			controller.onClicked(eventButton);
    		}
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		IRectangleBound bound = controller.getClipBound();
        if(controller.canClick(eventButton) && this.isClicking)
    	{
        	this.isClicking = false;
        	controller.onClickEnded(eventButton);
    	}
	}

	@Override
	public void keyTyped(EnumKey key, char eventChar) { }
	
	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		IRectangleBound clipBound = controller.getClipBound();
		if(clipBound.isEmpty())
			return;
		
		this.mouseOver = clipBound.isInBound(mouseX, mouseY);
	}

	@Override
	public void render(IRenderer renderer) {
		IRectangleBound clipBound = controller.getClipBound();
		if(clipBound.isEmpty())
			return;
		
		IRectangleBound elementBound = controller.getElementBound();
				
		renderer.startRender();
		controller.setupRenderer(this.mouseOver, renderer);
		String overlay = controller.setupOverlay(this.mouseOver, renderer);
		if(overlay != null)
			renderer.render(overlay, elementBound, clipBound);
		
		String main = controller.setupMain(this.mouseOver, renderer);
		renderer.render(main, elementBound, clipBound);
		renderer.endRender();
	}
}