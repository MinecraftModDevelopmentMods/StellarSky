package stellarium.lib.render.hierarchy;

import com.google.common.base.Predicate;

import stellarium.lib.render.IGenericRenderer;

public interface IRenderDistribution<SubID> {
	
	public IRenderDistribution<SubID> filter(Predicate<SubID> filter);

}