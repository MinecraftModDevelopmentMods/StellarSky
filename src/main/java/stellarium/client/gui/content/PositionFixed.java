package stellarium.client.gui.content;

public class PositionFixed implements IGuiPosition {
	
	private IRectangleBound bound;
	
	public PositionFixed(IRectangleBound bound) {
		this.bound = bound;
	}

	@Override
	public IRectangleBound getElementBound() {
		return this.bound;
	}

	@Override
	public IRectangleBound getClipBound() {
		return this.bound;
	}

	@Override
	public IRectangleBound getAdditionalBound(String boundName) {
		return null;
	}

	@Override
	public void initializeBounds() { }

	@Override
	public void updateBounds() { }

	@Override
	public void updateAnimation(float partialTicks) { }

}
