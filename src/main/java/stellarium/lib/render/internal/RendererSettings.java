package stellarium.lib.render.internal;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.hierarchy.IFieldElementDescription;
import stellarium.lib.render.IGenericRenderer;
import stellarium.lib.render.RendererRegistry;
import stellarium.lib.render.hierarchy.IRenderedCollection;

public class RendererSettings {
	Map<Object, IGenericRenderer> subModelRenderers = Maps.newHashMap();
	Function settingsTransformer;
	private int hashCode;
	
	public RendererSettings(IRenderedCollection collection) {
		this.settingsTransformer = collection.getTransformer();
		Map<Object, IFieldElementDescription> fields = HierarchyDistributor.INSTANCE.fields(collection.getModelType());
		this.subModelRenderers = Maps.transformValues(
				Maps.filterKeys(fields, collection.getFilter()),
				new Function<IFieldElementDescription, IGenericRenderer>() {
			@Override
			public IGenericRenderer apply(IFieldElementDescription input) {
				return RendererRegistry.INSTANCE.evaluateRenderer(input.getElementType());
			}
		});
		
		this.hashCode = collection.hashCode();
	}
	
	@Override
	public int hashCode() {
		return this.hashCode;
	}
}