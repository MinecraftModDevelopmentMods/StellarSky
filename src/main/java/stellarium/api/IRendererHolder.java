package stellarium.api;

public interface IRendererHolder {
	public void setRenderer(IAdaptiveRenderer renderer);
	public IAdaptiveRenderer getRenderer();
}
