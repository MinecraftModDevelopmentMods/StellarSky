package stellarium.lib.render.internal;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import stellarium.lib.render.hierarchy.IRenderedCollection;

public class RenderedCollection<SubID> implements IRenderedCollection<SubID> {

	Class<?> modelClass;
	Predicate<SubID> filter;
	Function transformer;

	public RenderedCollection(Class<?> modelClass, Predicate<SubID> filter, Function transformer) {
		this.modelClass = modelClass;
		this.filter = filter;
		this.transformer = transformer;
	}

	@Override
	public void transformSettings(Function setTransformer) {
		this.transformer = Functions.compose(setTransformer, this.transformer);
	}

	@Override
	public IRenderedCollection<SubID> getFiltered(Predicate<SubID> filter) {
		return new RenderedCollection(this.modelClass,
				Predicates.and(filter, this.filter),
				this.transformer);

	}

	@Override
	public Function getTransformer() {
		return this.transformer;
	}

	@Override
	public Predicate<SubID> getFilter() {
		return this.filter;
	}

	@Override
	public Class<?> getModelType() {
		return this.modelClass;
	}

}
