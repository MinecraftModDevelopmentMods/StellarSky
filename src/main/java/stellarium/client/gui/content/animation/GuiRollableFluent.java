package stellarium.client.gui.content.animation;

import stellarium.client.PressedKey;
import stellarium.client.gui.content.GuiElement;
import stellarium.client.gui.content.GuiPositionHierarchy;
import stellarium.client.gui.content.IGuiElementType;
import stellarium.client.gui.content.IGuiPosition;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;
import stellarium.client.gui.content.RectangleBound;
import stellarium.client.gui.content.list.GuiHasFixedList;
import stellarium.client.gui.content.list.IHasFixedListController;

/**
 * Rollable GUI with support for intermediate positions, for cases i.e. dragging.
 * Note that the position only include the element to roll.
 * */
public class GuiRollableFluent implements IGuiElementType<IRollableFluentController> {
	private IGuiPosition position;
	private IRollableFluentController controller;
	
	private GuiLinearMoving mover;
	private GuiElement elementToRoll, elementToExclude;
	private float excludedSize;
	
	private boolean isHorizontal, increaseCoordOnRoll, invertRollState;

	private float rollState;
	
	public GuiRollableFluent(GuiElement elementToRoll, GuiElement elementToExclude, float excludedSize) {
		this.elementToRoll = elementToRoll;
		this.elementToExclude = elementToExclude;
		this.excludedSize = excludedSize;
	}
	
	@Override
	public void initialize(GuiPositionHierarchy positions, IRollableFluentController parController) {
		this.position = positions.getPosition();
		this.controller = parController;
		
		this.isHorizontal = parController.isHorizontal();
		this.increaseCoordOnRoll = parController.increaseCoordOnRoll();
		this.invertRollState = parController.isRollStateIndependent() && this.increaseCoordOnRoll;
		
		this.rollState = parController.rollState();
		final RollPosition rollPosition = new RollPosition();
		
		GuiHasFixedList guiFixedList = new GuiHasFixedList(this.elementToRoll, this.elementToExclude, this.excludedSize);
		GuiElement fixedList = new GuiElement(guiFixedList, new IHasFixedListController() {
			@Override
			public boolean isHorizontal() { return isHorizontal; }
			@Override
			public String setupRenderer(IRenderer renderer) { return null; }
			@Override
			public boolean isModifiableFirst() { return !increaseCoordOnRoll; }
			@Override
			public IGuiPosition wrapFixedPosition(IGuiPosition wrapped, IGuiPosition listPos) {
				return controller.wrapExcludedPosition(wrapped, rollPosition);
			}
			@Override
			public IGuiPosition wrapModifiablePosition(IGuiPosition position, IGuiPosition listPos) { return position; }
		});
		
		this.mover = new GuiLinearMoving(fixedList);
		mover.initialize(positions.addChild(rollPosition), new AnimationController());
	}
	
	@Override
	public void updateElement() {
		mover.updateElement();
	}

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		mover.mouseClicked(mouseX, mouseY, eventButton);
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		mover.mouseMovedOrUp(mouseX, mouseY, eventButton);
	}

	@Override
	public void keyTyped(PressedKey key) {
		mover.keyTyped(key);
	}

	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		mover.checkMousePosition(mouseX, mouseY);
	}

	@Override
	public void render(IRenderer renderer) {
		mover.render(renderer);
	}
	
	private class RollPosition implements IGuiPosition {
		
		private RectangleBound element, clip, animation;

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
			if(boundName.equals("animation"))
				return this.animation;
			else return null;
		}

		@Override
		public void initializeBounds() {
			this.element = new RectangleBound(position.getElementBound());
			this.clip = new RectangleBound(position.getClipBound());
			this.animation = new RectangleBound(this.element);
			
			this.extendBounds();
			
			clip.setAsIntersection(this.element);
		}
		
		private void extendBounds() {
			if(isHorizontal) {
				if(increaseCoordOnRoll) {
					element.extend(excludedSize, 0.0f, 0.0f, 0.0f);
					clip.extend(excludedSize, 0.0f, 0.0f, 0.0f);
					animation.extend(excludedSize, 0.0f, animation.width, 0.0f);
				} else {
					element.extend(0.0f, 0.0f, excludedSize, 0.0f);
					clip.extend(0.0f, 0.0f, excludedSize, 0.0f);
					animation.extend(animation.width, 0.0f, excludedSize, 0.0f);
				}
			} else {
				if(increaseCoordOnRoll) {
					element.extend(0.0f, excludedSize, 0.0f, 0.0f);
					clip.extend(0.0f, excludedSize, 0.0f, 0.0f);
					animation.extend(0.0f, excludedSize, 0.0f, animation.height);
				} else {
					element.extend(0.0f, 0.0f, 0.0f, excludedSize);
					clip.extend(0.0f, 0.0f, 0.0f, excludedSize);
					animation.extend(0.0f, animation.height, 0.0f, excludedSize);
				}
			}
		}

		@Override
		public void updateBounds() {
			element.set(position.getElementBound());
			clip.set(position.getClipBound());
			animation.set(this.element);
			
			this.extendBounds();
			
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateAnimation(float partialTicks) {
			this.updateBounds();
		}
		
	}
	
	private class AnimationController implements ILinearMoveController {
		
		private int duration;

		@Override
		public float initialRatioX() {
			return this.nextRatioX();
		}

		@Override
		public float initialRatioY() {
			return this.nextRatioY();
		}

		@Override
		public boolean disableControlOnAnimating() {
			return controller.disableControlOnAnimating();
		}
		
		@Override
		public boolean forceState() {
			if(controller.forceState()) {
				rollState = controller.rollState();
				return true;
			} else return false;
		}

		@Override
		public boolean doesStartAnimation() {
			if(rollState != controller.rollState()) {
				this.duration = (int)Math.ceil(Math.abs(controller.rollState() - rollState) / controller.rollRatePerTick());
				rollState = controller.rollState();
				return true;
			} else return false;
		}

		@Override
		public boolean needHaltAnimation() {
			return rollState != controller.rollState();
		}

		@Override
		public void onAnimationEnded() { }

		@Override
		public int getAnimationDuration() {
			return this.duration;
		}

		@Override
		public float nextRatioX() {
			if(isHorizontal) {
				if(!invertRollState)
					return rollState;
				else return 1.0f - rollState;
			} else return 0.0f;
		}

		@Override
		public float nextRatioY() {
			if(!isHorizontal) {
				if(!invertRollState)
					return rollState;
				else return 1.0f - rollState;
			} else return 0.0f;
		}
	}

}
