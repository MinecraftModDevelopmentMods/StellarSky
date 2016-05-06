package stellarium.client.gui.content.list;

import java.util.List;

import com.google.common.collect.Lists;

import stellarium.client.PressedKey;
import stellarium.client.gui.content.GuiElement;
import stellarium.client.gui.content.GuiPositionHierarchy;
import stellarium.client.gui.content.IGuiElementType;
import stellarium.client.gui.content.IGuiPosition;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;
import stellarium.client.gui.content.RectangleBound;

public class GuiSimpleDividedList implements IGuiElementType<ISimpleListController> {

	private List<GuiElement> list;
	
	private IGuiPosition position;
	private ISimpleListController controller;
	
	private boolean isHorizontal;
	
	public GuiSimpleDividedList(List<GuiElement> list) {
		this.list = Lists.newArrayList(list);
	}
	
	public GuiSimpleDividedList(GuiElement... elements) {
		this.list = Lists.newArrayList(elements);
	}

	@Override
	public void initialize(GuiPositionHierarchy positions, ISimpleListController controller) {
		this.position = positions.getPosition();
		this.controller = controller;
		this.isHorizontal = controller.isHorizontal();
		
		int size = list.size();
		int index = 0;
		for(GuiElement element : this.list)
			element.initialize(positions.addChild(new DividedPosition(index++, size)));
	}

	@Override
	public void updateElement() {
		for(GuiElement element : this.list)
			element.getType().updateElement();
	}

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		for(GuiElement element : this.list)
			element.getType().mouseClicked(mouseX, mouseY, eventButton);
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		for(GuiElement element : this.list)
			element.getType().mouseMovedOrUp(mouseX, mouseY, eventButton);
	}

	@Override
	public void keyTyped(PressedKey key) {
		for(GuiElement element : this.list)
			element.getType().keyTyped(key);
	}

	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		for(GuiElement element : this.list)
			element.getType().checkMousePosition(mouseX, mouseY);
	}

	@Override
	public void render(IRenderer renderer) {
		if(position.getClipBound().isEmpty())
			return;

		String background = controller.setupRenderer(renderer);
		if(background != null)
			renderer.render(background, position.getElementBound(), position.getClipBound());
		for(GuiElement element : this.list)
			element.getType().render(renderer);
	}

	
	private class DividedPosition implements IGuiPosition {
		
		private RectangleBound element, clip;
		private float relativePos, size;
		
		public DividedPosition(int index, int size) {
			this.relativePos = (float)index / size;
			this.size = size;
		}
		
		public void initializeBounds() {
			this.element = new RectangleBound(position.getElementBound());
			this.clip = new RectangleBound(position.getClipBound());
			if(isHorizontal)
			{
				element.posX = element.getMainX(this.relativePos);
				element.width /= this.size;
			} else {
				element.posY = element.getMainY(this.relativePos);
				element.height /= this.size;
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
			if(isHorizontal)
			{
				element.posX = element.getMainX(this.relativePos);
				element.width /= this.size;
			} else {
				element.posY = element.getMainY(this.relativePos);
				element.height /= this.size;
			}
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateAnimation(float partialTicks) {
			this.updateBounds();
		}

	}
}
