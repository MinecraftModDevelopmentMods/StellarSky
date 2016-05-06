package stellarium.client.gui.content.scroll;

import stellarium.client.PressedKey;
import stellarium.client.gui.content.GuiPositionHierarchy;
import stellarium.client.gui.content.IGuiElementType;
import stellarium.client.gui.content.IGuiPosition;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;
import stellarium.client.gui.content.RectangleBound;
import stellarium.client.gui.content.button.GuiButtonDetectMouse;
import stellarium.client.gui.content.button.GuiButtonDraggable;
import stellarium.client.gui.content.button.IButtonDetectorController;
import stellarium.client.gui.content.button.IButtonDraggableController;

public class GuiScrollBar implements IGuiElementType<IScrollBarController> {

	private IGuiPosition position;
	private IScrollBarController controller;

	private float size, regionSize, btnSize;

	private boolean isHorizontal;
	private float progress;
	private GuiButtonDraggable drag = new GuiButtonDraggable();
	private GuiButtonDetectMouse dragRegion = new GuiButtonDetectMouse();

	public GuiScrollBar(float scrollSize, float regionSize, float btnSize) {
		this.size = scrollSize;
		this.regionSize = regionSize;
		this.btnSize = btnSize;
	}

	@Override
	public void initialize(GuiPositionHierarchy positions, IScrollBarController controller) {
		this.position = positions.getPosition();
		this.controller = controller;
		this.isHorizontal = controller.isHorizontal();
		this.progress = controller.initialProgress();
		
		dragRegion.initialize(positions.addChild(new DragRegionPosition()), new DragRegionController());
		drag.initialize(positions.addChild(new DragBtnPosition()), new DraggableController());
	}

	@Override
	public void updateElement() { }

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		dragRegion.mouseClicked(mouseX, mouseY, eventButton);
		drag.mouseClicked(mouseX, mouseY, eventButton);
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		dragRegion.mouseMovedOrUp(mouseX, mouseY, eventButton);
		drag.mouseMovedOrUp(mouseX, mouseY, eventButton);
	}

	@Override
	public void keyTyped(PressedKey key) { }

	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		dragRegion.checkMousePosition(mouseX, mouseY);
		drag.checkMousePosition(mouseX, mouseY);
	}

	@Override
	public void render(IRenderer renderer) {
		IRectangleBound clipBound = position.getClipBound();
		if(clipBound.isEmpty())
			return;
		
		renderer.startRender();
		String background = controller.setupBackgroundRenderer(renderer);
		if(background != null)
			renderer.render(background, position.getElementBound(), clipBound);
		renderer.endRender();
		
		dragRegion.render(renderer);
		drag.render(renderer);
	}
	
	
	public class DraggableController implements IButtonDraggableController {

		@Override
		public boolean canClick(int eventButton) {
			return controller.canHandle(eventButton);
		}

		@Override
		public void onDragStart(int eventButton, float dragRatioX, float dragRatioY) {
			progress = (isHorizontal? dragRatioX : dragRatioY);
		}

		@Override
		public void onDragging(float dragRatioX, float dragRatioY) {
			progress = (isHorizontal? dragRatioX : dragRatioY);
			controller.progressUpdating(progress);
		}

		@Override
		public void onDragEnded(int eventButton, float dragRatioX, float dragRatioY) {
			progress = (isHorizontal? dragRatioX : dragRatioY);
			controller.progressUpdated(progress);
		}

		@Override
		public void setupButton(boolean mouseOver, IRenderer renderer) {
			controller.setupButtonRenderer(mouseOver, renderer);
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			return controller.setupButtonOverlay(mouseOver, renderer);
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			return controller.setupButtonMain(mouseOver, renderer);
		}
		
	}
	
	private class DragBtnPosition implements IGuiPosition {
		
		private RectangleBound element, clip, drag;
		
		public void initializeBounds() {
			this.drag = new RectangleBound(position.getElementBound());
			this.clip = new RectangleBound(position.getClipBound());

			if(isHorizontal) {
				drag.posY += (drag.height-size) / 2;
				drag.height = size;
				drag.extend(-controller.getSpacing(), 0.0f, -controller.getSpacing(), 0.0f);
			} else {
				drag.posX += (drag.width-size) / 2;
				drag.width = size;
				drag.extend(0.0f, -controller.getSpacing(), 0.0f, -controller.getSpacing());
			}

			this.element = new RectangleBound(this.drag);

			if(isHorizontal)
			{
				element.width -= btnSize;
				element.posX = element.getMainX(progress);
				element.width = btnSize;
			} else {
				element.height -= btnSize;
				element.posY = element.getMainY(progress);
				element.height = btnSize;
			}
			clip.setAsIntersection(this.element);
		}

		@Override
		public IRectangleBound getElementBound() {
			return this.element;
		}

		@Override
		public IRectangleBound getClipBound() {
			return this.clip;
		}

		@Override
		public IRectangleBound getAdditionalBound(String boundName) {
			if("drag".equals(boundName))
				return this.drag;
			else return null;
		}

		@Override
		public void updateBounds() {
			drag.set(position.getElementBound());
			clip.set(position.getClipBound());
			
			if(isHorizontal) {
				drag.posY += (drag.height-size) / 2;
				drag.height = size;
				drag.extend(-controller.getSpacing(), 0.0f, -controller.getSpacing(), 0.0f);
			} else {
				drag.posX += (drag.width-size) / 2;
				drag.width = size;
				drag.extend(0.0f, -controller.getSpacing(), 0.0f, -controller.getSpacing());
			}
			
			element.set(this.drag);
			
			if(isHorizontal)
			{
				element.width -= btnSize;
				element.posX = element.getMainX(progress);
				element.width = btnSize;
			} else {
				element.height -= btnSize;
				element.posY = element.getMainY(progress);
				element.height = btnSize;
			}
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateAnimation(float partialTicks) {
			this.updateBounds();
		}
		
	}


	private class DragRegionController implements IButtonDetectorController {
		@Override
		public boolean canClick(int eventButton) {
			return controller.canHandle(eventButton);
		}

		@Override
		public void onClicked(int eventButton, float ratioX, float ratioY) {
			if(controller.moveCenterOnClick())
			{
				progress = isHorizontal? ratioX : ratioY;
				controller.progressUpdated(progress);
			}
		}

		@Override
		public void onClicking(float ratioX, float ratioY) { }

		@Override
		public void onClickEnded(int eventButton, float ratioX, float ratioY) { }

		@Override
		public void setupRenderer(boolean mouseOver, IRenderer renderer) {
			controller.setupRegionRenderer(mouseOver, renderer);
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			return controller.setupRegionOverlay(mouseOver, renderer);
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			return controller.setupRegionMain(mouseOver, renderer);
		}
	}

	private class DragRegionPosition implements IGuiPosition {

		private RectangleBound element, clip;

		public void initializeBounds() {
			this.element = new RectangleBound(position.getElementBound());
			this.clip = new RectangleBound(position.getClipBound());

			if(isHorizontal) {
				element.posY += (element.height-regionSize) / 2;
				element.height = regionSize;
				if(controller.isRegionCenterToCenter())
					element.extend(-btnSize/2, 0.0f, -btnSize/2, 0.0f);
				element.extend(-controller.getSpacing(), 0.0f, -controller.getSpacing(), 0.0f);
			} else {
				element.posX += (element.width-regionSize) / 2;
				element.width = regionSize;
				if(controller.isRegionCenterToCenter())
					element.extend(0.0f, -btnSize/2, 0.0f, -btnSize/2);
				element.extend(0.0f, -controller.getSpacing(), 0.0f, -controller.getSpacing());
			}

			clip.setAsIntersection(this.element);
		}

		@Override
		public IRectangleBound getElementBound() {
			return this.element;
		}

		@Override
		public IRectangleBound getClipBound() {
			return this.clip;
		}

		@Override
		public IRectangleBound getAdditionalBound(String boundName) {
			return null;
		}

		@Override
		public void updateBounds() {
			element.set(position.getElementBound());
			clip.set(position.getClipBound());
			
			if(isHorizontal) {
				element.posY += (element.height-regionSize) / 2;
				element.height = regionSize;
				if(controller.isRegionCenterToCenter())
					element.extend(-btnSize/2, 0.0f, -btnSize/2, 0.0f);
				element.extend(-controller.getSpacing(), 0.0f, -controller.getSpacing(), 0.0f);
			} else {
				element.posX += (element.width-regionSize) / 2;
				element.width = regionSize;
				if(controller.isRegionCenterToCenter())
					element.extend(0.0f, -btnSize/2, 0.0f, -btnSize/2);
				element.extend(0.0f, -controller.getSpacing(), 0.0f, -controller.getSpacing());
			}

			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateAnimation(float partialTicks) {
			this.updateBounds();
		}
		
	}
}
