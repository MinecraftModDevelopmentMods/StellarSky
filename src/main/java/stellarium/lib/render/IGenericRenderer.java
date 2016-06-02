package stellarium.lib.render;

/**
 * @param Settings the renderer-specific settings
 * @param Pass the immutable object to represent render pass
 * @param Model the model to render, need not inherit IRenderModel while it is recommended
 * @param RCI mutable render context information
 * */
public interface IGenericRenderer<Settings, Pass, Model, RCI> {
	/**
	 * Initialize with renderer-specific settings.
	 * */
	public void initialize(Settings settings);

	/**
	 * Initialize Renderer before any pass
	 * */
	public void preRender(Settings settings, RCI info);

	/**
	 * Render for pass
	 * */
	public void renderPass(Model model, Pass pass, RCI info);

	/**
	 * Finalize(Reduce) Renderer after any pass
	 * */
	public void postRender(Settings settings, RCI info);
}