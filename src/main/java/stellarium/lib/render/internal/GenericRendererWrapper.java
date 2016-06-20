package stellarium.lib.render.internal;

import stellarium.lib.render.IGenericRenderer;

public class GenericRendererWrapper implements IGenericRenderer {
	
	private IGenericRenderer wrapped;

	public GenericRendererWrapper(Class<?> modelClass) {
		this.wrapped = new DefaultRenderer(modelClass);
	}

	@Override
	public void initialize(Object settings) {
		wrapped.initialize(settings);
	}

	@Override
	public void preRender(Object settings, Object info) {
		wrapped.preRender(settings, info);
	}

	@Override
	public void renderPass(Object model, Object pass, Object info) {
		wrapped.renderPass(model, pass, info);
	}

	@Override
	public void postRender(Object settings, Object info) {
		wrapped.postRender(settings, info);
	}


	public void setWrappedRenderer(IGenericRenderer renderer) {
		this.wrapped = renderer;
	}

}
