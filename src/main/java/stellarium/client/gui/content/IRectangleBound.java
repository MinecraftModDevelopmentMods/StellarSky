package stellarium.client.gui.content;

public interface IRectangleBound {
	
	public float getLeftX();
	public float getUpY();
	public float getRightX();
	public float getDownY();
	
	public float getWidth();
	public float getHeight();
	
	public boolean isEmpty();
	
	public boolean isInBound(float x, float y);
	
	/** Ratio on X clipped in [0,1] */
	public float getRatioX(float x);
	
	/** Ratio on Y clipped in [0,1] */
	public float getRatioY(float y);

}
