package stellarium.stellars.layer.update;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import stellarium.stellars.layer.IPerWorldImage;
import stellarium.stellars.layer.StellarObject;

public class MetadataUpdateTracker<Obj extends StellarObject, T> {

	private IMetadataUpdater<Obj, T> updater;
	private UpdateWrapper updateWrapper = new UpdateWrapper();

	public MetadataUpdateTracker(IMetadataUpdater<Obj, T> updater) {
		this.updater = updater;
	}

	public IUpdateTracked<T> createTracker(Obj object, T metadata) {
		return new UpdateTracked(object, metadata);
	}

	/**
	 * Add update while iterating on the tracker.
	 * */
	public Iterable<T> addUpdateOnIteration(Iterable<IUpdateTracked<T>> wrapped) {
		return Iterables.transform(wrapped, this.updateWrapper);
	}
	
	/**
	 * Updates map with certain updater. (Without checking the time)
	 * */
	public void updateMap(Map<Obj, T> map) {
		for(Map.Entry<Obj, T> entry : map.entrySet())
			updater.update(entry.getKey(), entry.getValue());
	}

	private class UpdateWrapper implements Function<IUpdateTracked<T>, T> {
		@Override
		public T apply(IUpdateTracked<T> input) {
			input.tryUpdate();
			return input.getMetadata();
		}
	}

	public class UpdateTracked implements IUpdateTracked<T> {
		private Obj object;
		private T metadata;
		private long lastUpdateTime;

		public UpdateTracked(Obj object, T metadata) {
			this.object = object;
			this.metadata = metadata;
			this.lastUpdateTime = Long.MAX_VALUE;
		}

		@Override
		public T getMetadata() {
			return this.metadata;
		}

		@Override
		public void tryUpdate() {
			long current = updater.getCurrentTime();
			if(this.lastUpdateTime == current)
				return;
			updater.update(this.object, this.metadata);
			this.lastUpdateTime = current;
		}
	}

}
