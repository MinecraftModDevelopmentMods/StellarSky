package stellarium.render.base;

/**
 * @param Settings the settings for this model
 * @param UpdateInfo the update information for this model
 * */
public interface IRenderModel<Settings, UpdateInfo> {
	/**
	 * Initialize Model
	 * */
	public void initialize(Settings settings);
	
	/**
	 * Updates model
	 * */
	public void update(Settings settings, UpdateInfo update);
}