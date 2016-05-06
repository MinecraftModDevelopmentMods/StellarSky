package stellarium.client.gui.content.text;

import net.minecraft.util.MathHelper;
import stellarium.client.PressedKey;
import stellarium.client.gui.content.GuiPositionHierarchy;
import stellarium.client.gui.content.IFontHelper;
import stellarium.client.gui.content.IGuiElementType;
import stellarium.client.gui.content.IGuiPosition;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderer;
import stellarium.client.gui.content.RectangleBound;
import stellarium.client.gui.content.simple.GuiSimpleRenderElement;

public class GuiTextField implements IGuiElementType<ITextFieldController> {

	private IGuiPosition position;
	private ITextFieldController controller;
	
	private GuiTextInternal internal = new GuiTextInternal();
	private GuiSimpleRenderElement background = new GuiSimpleRenderElement();
	private float xOffset = 0.0f;
	
	@Override
	public void initialize(GuiPositionHierarchy positions, ITextFieldController controller) {
		this.position = positions.getPosition();
		this.controller = controller;
		internal.initialize(positions.addChild(new TextPosition()), new WrappedController(controller.getTextController()));
		background.initialize(positions, controller.getBackground());
	}

	@Override
	public void updateElement() {
		internal.updateElement();
	}

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		internal.mouseClicked(mouseX, mouseY, eventButton);
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		internal.mouseMovedOrUp(mouseX, mouseY, eventButton);
	}

	@Override
	public void keyTyped(PressedKey key) {
		internal.keyTyped(key);
	}

	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		internal.checkMousePosition(mouseX, mouseY);
	}

	@Override
	public void render(IRenderer renderer) {
		background.render(renderer);
		internal.render(renderer);
	}

	private class TextPosition implements IGuiPosition {
		
		private RectangleBound element, clip;
		
		public void initializeBounds() {
			this.element = new RectangleBound(position.getElementBound());
			this.clip = new RectangleBound(position.getClipBound());
			element.extend(-controller.getSpacingX(), -controller.getSpacingY(),
					-controller.getSpacingX(), -controller.getSpacingY());
			element.posX += xOffset;
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
			element.extend(-controller.getSpacingX(), -controller.getSpacingY(),
					-controller.getSpacingX(), -controller.getSpacingY());
			element.posX += xOffset;
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateAnimation(float partialTicks) {
			this.updateBounds();
		}
		
	}
	
	private class WrappedController implements ITextInternalController {

		private ITextInternalController wrapped;
		private String current = "";
		private int scrollOffset;
		
		public WrappedController(ITextInternalController wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public int maxStringLength() {
			return wrapped.maxStringLength();
		}

		@Override
		public IFontHelper getFontHelper() {
			return wrapped.getFontHelper();
		}

		@Override
		public boolean canModify() {
			return wrapped.canModify();
		}

		@Override
		public boolean canLoseFocus() {
			return wrapped.canLoseFocus();
		}

		@Override
		public String updateText(String text) {
			return this.current = wrapped.updateText(text);
		}

		@Override
		public float getCursorSpacing() {
			return wrapped.getCursorSpacing();
		}

		@Override
		public void setupRendererFocused(IRenderer renderer) {
			wrapped.setupRendererFocused(renderer);
		}

		@Override
		public void setupText(String text, IRenderer renderer) {
			wrapped.setupText(text, renderer);
		}

		@Override
		public void setupHighlightedText(String selection, IRenderer renderer) {
			wrapped.setupHighlightedText(selection, renderer);
		}

		@Override
		public String setupHighlightedOverlay(String selection, IRenderer renderer) {
			return wrapped.setupHighlightedOverlay(selection, renderer);
		}

		@Override
		public String setupRendererCursor(int cursorCounter) {
			return wrapped.setupRendererCursor(cursorCounter);
		}

		@Override
		public String setupRendererUnfocused(String text, IRenderer renderer) {
			return wrapped.setupRendererUnfocused(text, renderer);
		}

		@Override
		public void notifySelection(int cursor, int selection) {
			wrapped.notifySelection(cursor, selection);
			int totalLength = current.length();

			IFontHelper font = wrapped.getFontHelper();

			if (this.scrollOffset > totalLength)
				this.scrollOffset = totalLength;

			float fieldWidth = position.getElementBound().getWidth() - 2*controller.getSpacingX();
			String currentVisible = font.trimStringToWidth(current.substring(this.scrollOffset), fieldWidth);
			int visibleEnd = this.scrollOffset + currentVisible.length();

			if (selection == this.scrollOffset)
				this.scrollOffset -= font.trimStringToWidth(current.substring(0, this.scrollOffset), fieldWidth, true).length();

			if (selection > visibleEnd)
				this.scrollOffset += selection - visibleEnd;
			else if (selection <= this.scrollOffset)
				this.scrollOffset = selection;

			this.scrollOffset = MathHelper.clamp_int(this.scrollOffset, 0, totalLength);

			xOffset = wrapped.getFontHelper().getStringWidth(current.substring(0, this.scrollOffset));
		}
	}
}
