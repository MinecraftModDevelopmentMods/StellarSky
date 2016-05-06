package stellarium.client.gui.content.button;

import stellarium.client.PressedKey;
import stellarium.client.gui.content.GuiPositionHierarchy;
import stellarium.client.gui.content.IGuiElementType;
import stellarium.client.gui.content.IGuiPosition;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;

public class GuiButtonSimple implements IGuiElementType<IButtonController> {

	private IGuiPosition position;
	private IButtonController controller;
	private boolean isClicking, mouseOver;
	
	@Override
	public void initialize(GuiPositionHierarchy positions, IButtonController controller) {
		this.position = positions.getPosition();
		this.controller = controller;
		this.isClicking = false;
	}

	@Override
	public void updateElement() { }

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		IRectangleBound bound = position.getClipBound();
        if(controller.canClick(eventButton))
    		if(bound.isInBound(mouseX, mouseY))
    		{
    			this.isClicking = true;
    			controller.onClicked(eventButton);
    		}
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		IRectangleBound bound = position.getClipBound();
        if(controller.canClick(eventButton) && this.isClicking)
    	{
        	this.isClicking = false;
        	controller.onClickEnded(eventButton);
    	}
	}

	@Override
	public void keyTyped(PressedKey key) { }
	
	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		IRectangleBound clipBound = position.getClipBound();		
		this.mouseOver = clipBound.isInBound(mouseX, mouseY);
	}

	@Override
	public void render(IRenderer renderer) {
		IRectangleBound clipBound = position.getClipBound();
		if(clipBound.isEmpty())
			return;
		
		IRectangleBound elementBound = position.getElementBound();
		
		renderer.startRender();
		controller.setupRenderer(this.mouseOver, renderer);		
		String main = controller.setupMain(this.mouseOver, renderer);
		renderer.render(main, elementBound, clipBound);

		String overlay = controller.setupOverlay(this.mouseOver, renderer);
		if(overlay != null)
			renderer.render(overlay, elementBound, clipBound);
		
		renderer.endRender();
	}
}