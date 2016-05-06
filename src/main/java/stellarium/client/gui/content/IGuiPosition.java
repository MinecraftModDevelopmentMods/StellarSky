package stellarium.client.gui.content;

public interface IGuiPosition {
	
	public IRectangleBound getElementBound();
	/**
	 * Must be inside the element bound
	 * */
	public IRectangleBound getClipBound();
	
	public IRectangleBound getAdditionalBound(String boundName);
	
	public void initializeBounds();
	public void updateBounds();
	public void updateAnimation(float partialTicks);
	
}
