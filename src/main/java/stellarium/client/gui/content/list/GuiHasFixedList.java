package stellarium.client.gui.content.list;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import stellarium.client.PressedKey;
import stellarium.client.gui.content.GuiElement;
import stellarium.client.gui.content.GuiPositionHierarchy;
import stellarium.client.gui.content.IGuiElementType;
import stellarium.client.gui.content.IGuiPosition;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;
import stellarium.client.gui.content.RectangleBound;

public class GuiHasFixedList implements IGuiElementType<IHasFixedListController> {

	private GuiElement modifiable;
	private List<GuiElement> list;
	private List<Float> sizelist;
	private float totalSize;

	private IGuiPosition position;
	private ISimpleListController controller;

	private boolean isHorizontal, isModifiableFirst;

	public GuiHasFixedList(GuiElement modifiable, GuiElement fixed, float fixedSize) {
		this(modifiable, Lists.newArrayList(Pair.of(fixed, fixedSize)));
	}
	
	public GuiHasFixedList(GuiElement modifiable, GuiElement fixed1, float fixedSize1, GuiElement fixed2, float fixedSize2) {
		this(modifiable, Lists.newArrayList(Pair.of(fixed1, fixedSize1), Pair.of(fixed2, fixedSize2)));
	}
	
	public GuiHasFixedList(GuiElement modifiable, Pair<GuiElement, Float>... list) {
		this(modifiable, Lists.newArrayList(list));
	}

	public GuiHasFixedList(GuiElement modifiable, List<Pair<GuiElement, Float>> listWithSize) {
		this.modifiable = modifiable;
		this.list = Lists.transform(listWithSize,
				new Function<Pair<GuiElement, Float>, GuiElement>() {
			@Override
			public GuiElement apply(Pair<GuiElement, Float> input) {
				return input.getLeft();
			}
		});
		this.sizelist = Lists.transform(listWithSize,
				new Function<Pair<GuiElement, Float>, Float>() {
			
			@Override
			public Float apply(Pair<GuiElement, Float> input) {
				return input.getRight();
			}
		});
		
		this.totalSize = sizelist.get(sizelist.size()-1);
	}

	@Override
	public void initialize(GuiPositionHierarchy positions, IHasFixedListController controller) {
		this.position = positions.getPosition();
		this.controller = controller;
		
		this.isHorizontal = controller.isHorizontal();
		this.isModifiableFirst = controller.isModifiableFirst();
		
		int size = list.size();
		int index = 0;
		float currentPos = 0.0f;
		
		for(GuiElement element : this.list)
		{
			element.initialize(positions.addChild(
					controller.wrapFixedPosition(new FixedPosition(currentPos, sizelist.get(index)), this.position)));
			currentPos += sizelist.get(index++);
		}
		this.totalSize = currentPos;
		
		modifiable.initialize(positions.addChild(
				controller.wrapModifiablePosition(new ModifiablePosition(), this.position)));
	}

	@Override
	public void updateElement() {
		modifiable.getType().updateElement();
		for(GuiElement element : this.list)
			element.getType().updateElement();
	}

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		modifiable.getType().mouseClicked(mouseX, mouseY, eventButton);
		for(GuiElement element : this.list)
			element.getType().mouseClicked(mouseX, mouseY, eventButton);
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		modifiable.getType().mouseMovedOrUp(mouseX, mouseY, eventButton);
		for(GuiElement element : this.list)
			element.getType().mouseMovedOrUp(mouseX, mouseY, eventButton);
	}

	@Override
	public void keyTyped(PressedKey key) {
		modifiable.getType().keyTyped(key);
		for(GuiElement element : this.list)
			element.getType().keyTyped(key);
	}

	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		modifiable.getType().checkMousePosition(mouseX, mouseY);
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
		modifiable.getType().render(renderer);
		for(GuiElement element : this.list)
			element.getType().render(renderer);
	}

	
	private class FixedPosition implements IGuiPosition {
		
		private RectangleBound element, clip;
		private float pos, size;
		
		public FixedPosition(float pos, float size) {
			this.pos = pos;
			this.size = size;
		}
		
		public void initializeBounds() {
			this.element = new RectangleBound(position.getElementBound());
			this.clip = new RectangleBound(position.getClipBound());
			if(isHorizontal)
			{
				element.posX += isModifiableFirst? element.width-this.pos-this.size : this.pos;
				element.width = this.size;
			} else {
				element.posY = isModifiableFirst? element.height-this.pos-this.size : this.pos;
				element.height = this.size;
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
				element.posX += isModifiableFirst? element.width-totalSize+this.pos : this.pos;
				element.width = this.size;
			} else {
				element.posY += isModifiableFirst? element.height-totalSize+this.pos : this.pos;
				element.height = this.size;
			}
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateAnimation(float partialTicks) {
			this.updateBounds();
		}

	}


	private class ModifiablePosition implements IGuiPosition {
		
		private RectangleBound element, clip;
				
		public void initializeBounds() {
			this.element = new RectangleBound(position.getElementBound());
			this.clip = new RectangleBound(position.getClipBound());
			if(isHorizontal)
			{
				element.posX += isModifiableFirst? 0.0f : totalSize;
				element.width -= totalSize;
			} else {
				element.posY += isModifiableFirst? 0.0f : totalSize;
				element.height -= totalSize;
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
				element.posX += isModifiableFirst? 0.0f : totalSize;
				element.width -= totalSize;
			} else {
				element.posY += isModifiableFirst? 0.0f : totalSize;
				element.height -= totalSize;
			}
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateAnimation(float partialTicks) {
			this.updateBounds();
		}

	}
}
