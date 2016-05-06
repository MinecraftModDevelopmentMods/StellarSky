package stellarium.client.gui.content.animation;

import stellarium.client.PressedKey;
import stellarium.client.gui.content.GuiElement;
import stellarium.client.gui.content.GuiPositionHierarchy;
import stellarium.client.gui.content.IGuiElementType;
import stellarium.client.gui.content.IGuiPosition;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;
import stellarium.client.gui.content.RectangleBound;

/**
 * Linear moving motion.
 * Usually sub-element for something which controls animation.
 * */
public class GuiLinearMoving implements IGuiElementType<ILinearMoveController> {

	private GuiElement subElement;
	
	private IGuiPosition position;
	private ILinearMoveController controller;
	
	
	private int duration, current;
	private boolean isAnimating = false;
	
	private float previousPosX, nextPosX;
	private float previousPosY, nextPosY;
	
	public GuiLinearMoving(GuiElement subElement) {
		this.subElement = subElement;
	}

	@Override
	public void initialize(GuiPositionHierarchy positions, ILinearMoveController controller) {
		this.position = positions.getPosition();
		this.controller = controller;
		
		this.previousPosX = this.nextPosX = controller.initialRatioX();
		this.previousPosY = this.nextPosY = controller.initialRatioY();
		subElement.initialize(positions.addChild(new AnimatedPosition()));
	}

	@Override
	public void updateElement() {
		if(controller.forceState()) {
			this.previousPosX = this.nextPosX = controller.nextRatioX();
			this.previousPosY = this.nextPosY = controller.nextRatioY();
			this.current = this.duration;
		}
		
		if(this.isAnimating)
			if(controller.needHaltAnimation()) {
				this.current = this.duration;
			} else this.current++;
		
		if(this.isAnimating && this.duration == this.current)
		{
			controller.onAnimationEnded();
			this.isAnimating = false;
			this.previousPosX = this.nextPosX;
			this.previousPosY = this.nextPosY;
		}
		
		subElement.getType().updateElement();
		
		if(!this.isAnimating && controller.doesStartAnimation())
		{
			this.current = 0;
			this.duration = controller.getAnimationDuration();
			this.nextPosX = controller.nextRatioX();
			this.nextPosY = controller.nextRatioY();
			this.isAnimating = true;
		}
	}

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		if(this.isAnimating && controller.disableControlOnAnimating())
			return;

		subElement.getType().mouseClicked(mouseX, mouseY, eventButton);
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		if(this.isAnimating && controller.disableControlOnAnimating())
			return;

		subElement.getType().mouseMovedOrUp(mouseX, mouseY, eventButton);
	}

	@Override
	public void keyTyped(PressedKey key) {
		if(this.isAnimating && controller.disableControlOnAnimating())
			return;

		subElement.getType().keyTyped(key);
	}

	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		subElement.getType().checkMousePosition(mouseX, mouseY);
	}

	@Override
	public void render(IRenderer renderer) {
		subElement.getType().render(renderer);
	}
	
	private class AnimatedPosition implements IGuiPosition {
		
		private RectangleBound element, clip, animation;
		
		public void initializeBounds() {
			this.element = new RectangleBound(position.getElementBound());
			this.clip = new RectangleBound(position.getClipBound());
			
			this.animation = new RectangleBound(position.getAdditionalBound("animation"));

			this.setupAnimationBound();

			element.posX = animation.getMainX(nextPosX);
			element.posY = animation.getMainY(nextPosY);
			
			clip.setAsIntersection(this.element);
		}
		
		private void setupAnimationBound() {
			animation.width -= element.width;
			animation.height -= element.height;
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
			animation.set(position.getAdditionalBound("animation"));

			this.setupAnimationBound();

			if(isAnimating) {
				element.posX = animation.getMainX(((duration - current) * previousPosX + current * nextPosX) / duration);
				element.posY = animation.getMainY(((duration - current) * previousPosY + current * nextPosY) / duration);
			} else {
				element.posX = animation.getMainX(nextPosX);
				element.posY = animation.getMainY(nextPosY);
			}
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateAnimation(float partialTicks) {
			if(!isAnimating)
			{
				this.updateBounds();
				return;
			}
			
			float currentPr = current + partialTicks;
			element.set(position.getElementBound());
			clip.set(position.getClipBound());
			animation.set(position.getAdditionalBound("animation"));

			this.setupAnimationBound();

			element.posX = animation.getMainX(((duration - currentPr) * previousPosX + currentPr * nextPosX) / duration);
			element.posY = animation.getMainY(((duration - currentPr) * previousPosY + currentPr * nextPosY) / duration);
			clip.setAsIntersection(this.element);
		}
	}

}
