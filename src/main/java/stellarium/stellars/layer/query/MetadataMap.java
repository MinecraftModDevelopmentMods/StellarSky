package stellarium.stellars.layer.query;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import stellarium.stellars.layer.StellarObject;

@Deprecated
public class MetadataMap<Obj extends StellarObject, T> implements IMetadataQueryable<Obj, T> {

	private Map<Obj, T> wrappedMap;
	private Function<QueryStellarObject, Predicate<T>> inRange;

	/**
	 * Query-able wrap for metadata map.
	 * @param wrappedMap the wrapped map
	 * @param inRange gets range predicate for query, provide null predicate to accept all 
	 * */
	public MetadataMap(Map<Obj, T> wrappedMap, Function<QueryStellarObject, Predicate<T>> inRange) {
		this.wrappedMap = wrappedMap;
		this.inRange = inRange;
	}

	@Override
	public Iterable<T> query(QueryStellarObject query) {
		return Iterables.filter(wrappedMap.values(),
				ObjectUtils.firstNonNull(inRange.apply(query), Predicates.<T>alwaysTrue()));
	}
	
	@Override
	public Function<Obj, T> lazyLoader() {
		return Functions.forMap(this.wrappedMap, null);
	}

	@Override
	public <S> Iterable<S> query(QueryStellarObject query, Function<Obj, S> lazyLoader) {
		Predicate<T> predicate = inRange.apply(query);
		return Iterables.transform(
				predicate == null? wrappedMap.keySet() :
					Iterables.filter(wrappedMap.keySet(), Predicates.compose(predicate, Functions.forMap(this.wrappedMap))),
				lazyLoader);
	}
}
