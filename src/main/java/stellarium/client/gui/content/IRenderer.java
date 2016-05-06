package stellarium.client.gui.content;

public interface IRenderer {

	/**
	 * Binds render model.
	 * For normal gui rendering case, it will be render model for .
	 * For rendering texts, it will be something like font.
	 * */
	public void bindModel(IRenderModel model);

	public void pushMatrixTillNextRender();
	public void translate(float posX, float posY);

	/**
	 * Rotation around the center.
	 * */
	public void rotate(float angle, float x, float y, float z);
	
	/**
	 * Scale around the center.
	 * */
	public void scale(float scaleX, float scaleY);
	public void color(float red, float green, float blue, float alpha);

	public void render(String info, IRectangleBound totalBound, IRectangleBound clipBound);

	public void startRender();
	public void endRender();

}
