package stellarium.display;

public interface IDisplayRenderer<Cache extends IDisplayCache> {

	public void render(DisplayRenderInfo info, Cache cache);

}
