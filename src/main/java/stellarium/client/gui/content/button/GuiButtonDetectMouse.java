package stellarium.client.gui.content.button;

import stellarium.client.PressedKey;
import stellarium.client.gui.content.GuiPositionHierarchy;
import stellarium.client.gui.content.IGuiElementType;
import stellarium.client.gui.content.IGuiPosition;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;

/**
 * Button which detects the relative position of the mouse when clicked.
 * */
public class GuiButtonDetectMouse implements IGuiElementType<IButtonDetectorController> {

	private IGuiPosition position;
	private IButtonDetectorController controller;
	private boolean isClicking, mouseOver;
	
	@Override
	public void initialize(GuiPositionHierarchy positions, IButtonDetectorController controller) {
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
    			
    			IRectangleBound elementBound = position.getElementBound();
    			controller.onClicked(eventButton, elementBound.getRatioX(mouseX), elementBound.getRatioY(mouseY));
    		}
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		IRectangleBound bound = position.getClipBound();
        if(controller.canClick(eventButton) && this.isClicking)
    	{
        	this.isClicking = false;

			IRectangleBound elementBound = position.getElementBound();
        	controller.onClickEnded(eventButton, elementBound.getRatioX(mouseX), elementBound.getRatioY(mouseY));
    	}
	}

	@Override
	public void keyTyped(PressedKey key) { }
	
	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		IRectangleBound clipBound = position.getClipBound();		
		this.mouseOver = clipBound.isInBound(mouseX, mouseY);
		
		if(this.isClicking) {
			IRectangleBound elementBound = position.getElementBound();
			controller.onClicking(elementBound.getRatioX(mouseX), elementBound.getRatioY(mouseY));
		}
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