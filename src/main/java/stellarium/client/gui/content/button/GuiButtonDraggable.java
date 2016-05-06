package stellarium.client.gui.content.button;

import stellarium.client.PressedKey;
import stellarium.client.gui.content.GuiPositionHierarchy;
import stellarium.client.gui.content.IGuiElementType;
import stellarium.client.gui.content.IGuiPosition;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;
import stellarium.client.gui.content.RectangleBound;

/**
 * Draggable button which moves through the mouse on dragging.
 *  (Although moving draggable button is not implemented internally)
 * Usually sub-element for other element which supports drag.
 * */
public class GuiButtonDraggable implements IGuiElementType<IButtonDraggableController> {
	
	private IButtonDraggableController controller;
	private IGuiPosition position;
	private boolean isClicking;
	private boolean mouseOver, mouseOverDrag;
	private float clickRatioX, clickRatioY;
	private float dragRatioX, dragRatioY;

	@Override
	public void initialize(GuiPositionHierarchy positions, IButtonDraggableController controller) {
		this.position = new DraggablePosition(positions.getPosition());
		this.controller = controller;
		this.isClicking = false;
	}

	@Override
	public void updateElement() { }

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
        if(controller.canClick(eventButton))
        {
			IRectangleBound elementBound = position.getElementBound();
			IRectangleBound draggableBound = position.getAdditionalBound("dragOffset");
			
    		if(position.getClipBound().isInBound(mouseX, mouseY))
    		{
    			this.isClicking = true;

    			this.clickRatioX = elementBound.getRatioX(mouseX);
    			this.clickRatioY = elementBound.getRatioY(mouseY);
    			this.dragRatioX = draggableBound.getRatioX(mouseX - this.clickRatioX * elementBound.getWidth());
    			this.dragRatioY = draggableBound.getRatioY(mouseY - this.clickRatioY * elementBound.getHeight());
    			
    			controller.onDragStart(eventButton, this.dragRatioX, this.dragRatioY);
    		}
        }
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
        if(controller.canClick(eventButton) && this.isClicking)
    	{
        	this.isClicking = false;
			IRectangleBound elementBound = position.getElementBound();
			IRectangleBound draggableBound = position.getAdditionalBound("dragOffset");
			float currentX = draggableBound.getRatioX(mouseX - this.clickRatioX * elementBound.getWidth());
			float currentY = draggableBound.getRatioY(mouseY - this.clickRatioY * elementBound.getHeight());
			
        	controller.onDragEnded(eventButton, currentX, currentY);
    	}
	}

	@Override
	public void keyTyped(PressedKey key) { }
	
	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		IRectangleBound elementBound = position.getElementBound();

		if(this.isClicking) {
			IRectangleBound draggableBound = position.getAdditionalBound("dragOffset");
			float currentX = draggableBound.getRatioX(mouseX - this.clickRatioX * elementBound.getWidth());
			float currentY = draggableBound.getRatioY(mouseY - this.clickRatioY * elementBound.getHeight());

			controller.onDragging(currentX, currentY);
		}
		
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
		
		controller.setupButton(this.mouseOver, renderer);

		String main = controller.setupMain(this.mouseOver, renderer);
		renderer.render(main, elementBound, clipBound);
		
		String overlay = controller.setupOverlay(this.mouseOver, renderer);
		if(overlay != null)
			renderer.render(overlay, elementBound, clipBound);
		renderer.endRender();
	}
	
	private class DraggablePosition implements IGuiPosition {
		
		private IGuiPosition wrapped;
		private RectangleBound dragBound = new RectangleBound(0,0,0,0);
		
		public DraggablePosition(IGuiPosition position) {
			this.wrapped = position;
		}

		@Override
		public IRectangleBound getElementBound() {
			return wrapped.getElementBound();
		}

		@Override
		public IRectangleBound getClipBound() {
			return wrapped.getClipBound();
		}

		@Override
		public IRectangleBound getAdditionalBound(String boundName) {
			if(boundName.equals("drag"))
				return wrapped.getAdditionalBound("drag");
			dragBound.set(wrapped.getAdditionalBound("drag"));
			IRectangleBound elementBound = wrapped.getElementBound();
			dragBound.width -= elementBound.getWidth();
			dragBound.height -= elementBound.getHeight();
			
			//Negative dragging bound width/height means that the direction is not important.
			//Just to avoid errors from Infinity or NaN.
			if(dragBound.width <= 0.0f)
				dragBound.width = 0.01f;
			if(dragBound.height <= 0.0f)
				dragBound.height = 0.01f;
			
			return this.dragBound;
		}

		@Override
		public void initializeBounds() { }

		@Override
		public void updateBounds() { }

		@Override
		public void updateAnimation(float partialTicks) { }
	}
}
