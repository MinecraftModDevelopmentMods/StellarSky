package stellarium.client.gui.content.simple;

import java.util.Map;

import com.google.common.collect.Maps;

import stellarium.client.PressedKey;
import stellarium.client.gui.content.GuiElement;
import stellarium.client.gui.content.GuiPositionHierarchy;
import stellarium.client.gui.content.IGuiElementType;
import stellarium.client.gui.content.IGuiPosition;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;
import stellarium.client.gui.content.RectangleBound;

public class GuiSimpleSpacingElement implements IGuiElementType<ISimpleSpacingController> {

	private IGuiPosition position;
	private ISimpleSpacingController controller;
	private GuiElement subElement;
	
	public GuiSimpleSpacingElement(GuiElement subElement) {
		this.subElement = subElement;
	}
	
	@Override
	public void initialize(GuiPositionHierarchy positions, ISimpleSpacingController controller) {
		this.position = positions.getPosition();
		this.controller = controller;
		subElement.initialize(positions.addChild(new SpacedPosition()));
	}

	@Override
	public void updateElement() {
		subElement.getType().updateElement();
	}

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		subElement.getType().mouseClicked(mouseX, mouseY, eventButton);
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		subElement.getType().mouseMovedOrUp(mouseX, mouseY, eventButton);
	}

	@Override
	public void keyTyped(PressedKey key) {
		subElement.getType().keyTyped(key);
	}

	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		subElement.getType().checkMousePosition(mouseX, mouseY);
	}
	
	@Override
	public void render(IRenderer renderer) {
		IRectangleBound clipBound = position.getClipBound();
		if(clipBound.isEmpty())
			return;
		
		renderer.startRender();
		String model = controller.setupSpacingRenderer(renderer);
		if(model != null)
			renderer.render(model, position.getElementBound(), clipBound);
		renderer.endRender();
		subElement.getType().render(renderer);
	}

	public class SpacedPosition implements IGuiPosition {
		private RectangleBound element, clip;

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
			return position.getAdditionalBound(boundName);
		}

		@Override
		public void initializeBounds() {
			this.element = new RectangleBound(position.getElementBound());
			this.clip = new RectangleBound(position.getClipBound());
			element.extend(-controller.getSpacingX(), -controller.getSpacingY(),
					-controller.getSpacingX(), -controller.getSpacingY());
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateBounds() {
			element.set(position.getElementBound());
			clip.set(position.getClipBound());
			element.extend(-controller.getSpacingX(), -controller.getSpacingY(),
					-controller.getSpacingX(), -controller.getSpacingY());
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateAnimation(float partialTicks) {
			this.updateBounds();
		}

	}
}
