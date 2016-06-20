package stellarium.stellars.layer.update;

import stellarium.stellars.layer.StellarObject;

public interface IMetadataUpdater<Obj extends StellarObject, T> {

	public long getCurrentTime();
	public void update(Obj object, T metadata);

}
