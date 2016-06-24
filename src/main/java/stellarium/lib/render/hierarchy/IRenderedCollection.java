package stellarium.lib.render.hierarchy;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public interface IRenderedCollection<SubID> {
	
	/**
	* Sets settings transformer, by composing the previous transformer with given transformer.
	* This is for hierarchy of types of the settings.
	* */
	public void transformSettings(Function setTransformer);
	
	/**
	 * Gives new collection for filtered ids.
	 * */
	public IRenderedCollection<SubID> getFiltered(Predicate<SubID> filter);

	
	/**
	 * The settings transformer currently set.
	 * */
	public Function getTransformer();

	/**
	 * Current filter.
	 * */
	public Predicate<SubID> getFilter();

	/**
	 * Current Model Type.
	 * */
	public Class<?> getModelType();

}