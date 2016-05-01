package stellarium.client.gui.content.button;

import stellarium.client.EnumKey;
import stellarium.client.gui.content.IGuiBasicElement;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;

public class GuiButtonDraggable implements IGuiBasicElement<IButtonDraggableController> {
	
	private IButtonDraggableController controller;
	private boolean isClicking;
	private boolean mouseOver;
	private float clickRatioX, clickRatioY;
	private float dragRatioX, dragRatioY;
	
	@Override
	public void initialize(IButtonDraggableController controller) {
		this.controller = controller;
		this.isClicking = false;
	}

	@Override
	public void updateElement() { }

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
        if(controller.canClick(eventButton))
    		if(controller.getClipBound().isInBound(mouseX, mouseY))
    		{
    			this.isClicking = true;
    			IRectangleBound elementBound = controller.getElementBound();
    			IRectangleBound draggableBound = controller.getDraggableBound();
    			this.clickRatioX = elementBound.getRatioX(mouseX);
    			this.clickRatioY = elementBound.getRatioY(mouseY);
    			this.dragRatioX = draggableBound.getRatioX(mouseX - this.clickRatioX * elementBound.getWidth());
    			this.dragRatioY = draggableBound.getRatioY(mouseY - this.clickRatioY * elementBound.getHeight());
    			
    			controller.onDragStart(eventButton);
    		}
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
        if(controller.canClick(eventButton) && this.isClicking)
    	{
        	this.isClicking = false;
			IRectangleBound elementBound = controller.getElementBound();
			IRectangleBound draggableBound = controller.getDraggableBound();
			float currentX = draggableBound.getRatioX(mouseX - this.clickRatioX * elementBound.getWidth());
			float currentY = draggableBound.getRatioY(mouseY - this.clickRatioY * elementBound.getHeight());
        	
        	controller.onDragEnded(eventButton, currentX - this.dragRatioX, currentY - this.dragRatioY);
    	}
	}

	@Override
	public void keyTyped(EnumKey key, char eventChar) { }
	
	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		IRectangleBound elementBound = controller.getElementBound();

		if(this.isClicking) {
			IRectangleBound draggableBound = controller.getDraggableBound();
			float currentX = draggableBound.getRatioX(mouseX - this.clickRatioX * elementBound.getWidth());
			float currentY = draggableBound.getRatioY(mouseY - this.clickRatioY * elementBound.getHeight());

			controller.onDragging(currentX - this.dragRatioX, currentY - this.dragRatioY);
		}
		
		IRectangleBound clipBound = controller.getClipBound();
		if(clipBound.isEmpty())
			return;

		boolean mouseOver = clipBound.isInBound(mouseX, mouseY);
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
