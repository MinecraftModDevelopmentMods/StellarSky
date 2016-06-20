package stellarium.stellars.layer.update;

public interface IUpdateTracked<T> {
	public T getMetadata();
	public void tryUpdate();
}
