package stellarium.stellars.layer.query;

import com.google.common.base.Function;

import stellarium.stellars.layer.StellarObject;

public interface IMetadataQueryable<Obj extends StellarObject, T> {
	/**
	 * Gives the iterable for additional metadata loaded for the objects in query,
	 * Or return null if query is invalid, e.g. too big search range.
	 * */
	public Iterable<T> query(QueryStellarObject query);

	/**
	 * Gives the lazy metadata loader when the object is given.
	 * Mostly for internal use, as creating compound loader.
	 * Can give mapping to null if it cannot be loaded.
	 * */
	public Function<Obj, T> lazyLoader();

	/**
	 * Gives the iterable for compound of object and loaded metadata in query,
	 * Or return null if query is invalid, e.g. too big search range.
	 * */
	public <S> Iterable<S> query(QueryStellarObject query, Function<Obj, S> compoundLoader);
}
