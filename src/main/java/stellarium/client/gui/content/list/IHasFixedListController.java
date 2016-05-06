package stellarium.client.gui.content.list;

import stellarium.client.gui.content.IGuiPosition;

public interface IHasFixedListController extends ISimpleListController {

	public boolean isModifiableFirst();
	public IGuiPosition wrapFixedPosition(IGuiPosition position, IGuiPosition listPos);
	public IGuiPosition wrapModifiablePosition(IGuiPosition position, IGuiPosition listPos);

}
