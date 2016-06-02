package stellarium.lib.render.hierarchy.internal;

import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import stellarium.lib.render.hierarchy.IRenderDistribution;

public class RenderDistribution<SubID> implements IRenderDistribution<SubID> {

	private Class<?> modelClass;
	private Predicate<SubID> filter;

	public RenderDistribution(Class<?> modelClass, Predicate<SubID> filter) {
		this.modelClass = modelClass;
		this.filter = filter;
	}

	@Override
	public IRenderDistribution<SubID> filter(Predicate<SubID> filter) {
		return new RenderDistribution(this.modelClass, Predicates.and(filter, this.filter));
	}

}
