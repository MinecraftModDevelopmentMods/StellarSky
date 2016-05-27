package stellarium.stellars.layer.query;

import stellarium.stellars.layer.StellarObject;

public interface IMetadataManager<Obj extends StellarObject, T> {
	/**
	 * Loads metadata for the object.
	 * */
	public T loadMetadata(Obj object);
	
	/**
	 * Updates metadata for use.
	 * */
	public void updateMetadata(Obj object, T metadata);
}