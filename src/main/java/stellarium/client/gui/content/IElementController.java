package stellarium.client.gui.content;

import stellarium.client.EnumKey;

public interface IElementController {

	public void update(float partialTicks);
	
	public IRectangleBound getElementBound();
	public IRectangleBound getClipBound();

}
