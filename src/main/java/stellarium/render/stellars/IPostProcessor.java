package stellarium.render.stellars;

public interface IPostProcessor {
	public void initialize();
	public void onResize(int width, int height);

	public void preProcess();
	public void postProcess(StellarRI info);
}
