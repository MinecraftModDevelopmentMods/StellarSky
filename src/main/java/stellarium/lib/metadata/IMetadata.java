package stellarium.lib.metadata;

public interface IMetadata<T> {
	public void onMainChanged(IChangeContext<T> changedContext);
}