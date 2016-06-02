package stellarium.lib.metadata;

public interface IChangeContext<T> {
	public boolean isChanged(T changeType);
}
