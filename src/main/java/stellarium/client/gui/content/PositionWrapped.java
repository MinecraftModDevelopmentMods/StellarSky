package stellarium.client.gui.content;

public class PositionWrapped implements IGuiPosition {
	
	private IGuiPosition wrapped, reference;
	private String referenceBoundName;
	private boolean isElement;
	
	public PositionWrapped(IGuiPosition wrapped, IGuiPosition reference, String refBoundName, boolean isElement) {
		this.wrapped = wrapped;
		this.reference = reference;
		this.referenceBoundName = refBoundName;
		this.isElement = isElement;
	}

	@Override
	public IRectangleBound getElementBound() {
		return wrapped.getElementBound();
	}

	@Override
	public IRectangleBound getClipBound() {
		return wrapped.getClipBound();
	}

	@Override
	public IRectangleBound getAdditionalBound(String boundName) {
		if(boundName.equals(this.referenceBoundName))
			return this.isElement? reference.getElementBound() : reference.getClipBound();
		else return wrapped.getAdditionalBound(boundName);
	}

	@Override
	public void initializeBounds() {
		wrapped.initializeBounds();
	}

	@Override
	public void updateBounds() {
		wrapped.updateBounds();
	}

	@Override
	public void updateAnimation(float partialTicks) {
		wrapped.updateAnimation(partialTicks);
	}

}
