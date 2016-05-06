package stellarium.client.gui.content.text;

import stellarium.client.gui.content.IElementController;
import stellarium.client.gui.content.IFontHelper;
import stellarium.client.gui.content.IRenderer;

public interface ITextInternalController extends IElementController {

	public int maxStringLength();
	public IFontHelper getFontHelper();

	public boolean canModify();
	public boolean canLoseFocus();
	
	public void notifySelection(int cursor, int selection);
	public String updateText(String text);

	public float getCursorSpacing();

	public void setupRendererFocused(IRenderer renderer);	
	public void setupText(String text, IRenderer renderer);	
	public void setupHighlightedText(String selection, IRenderer renderer);
	public String setupHighlightedOverlay(String selection, IRenderer renderer);
	public String setupRendererCursor(int cursorCounter);

	public String setupRendererUnfocused(String text, IRenderer renderer);

}
