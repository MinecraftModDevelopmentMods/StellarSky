package stellarium.render.stellars.phased;

import com.google.common.base.Function;
import com.google.common.base.Functions;

import stellarium.lib.render.RendererRegistry;
import stellarium.lib.render.hierarchy.IDistributionConfigurable;
import stellarium.lib.render.hierarchy.IRenderState;
import stellarium.lib.render.hierarchy.IRenderTransition;
import stellarium.lib.render.hierarchy.IRenderedCollection;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.render.stellars.layer.StellarLayerModel;
import stellarium.render.stellars.layer.StellarLayerRenderer;

public enum EnumPhasedRenderState implements IRenderState<EnumStellarPass, LayerRenderInformation> {
	Initial() {

		@Override
		public IRenderState<EnumStellarPass, LayerRenderInformation> transitionTo(EnumStellarPass pass,
				LayerRenderInformation resInfo) {
			resInfo.initialize(pass);
			return Instance;
		}
		
	},
	Instance() {
		@Override
		public IRenderState<EnumStellarPass, LayerRenderInformation> transitionTo(EnumStellarPass pass,
				LayerRenderInformation resInfo) {
			return null;
		}
	};
	
	private static final StellarTessellator tessellator = new StellarTessellator();

	public static void constructRender() {
		IDistributionConfigurable<EnumStellarPass, StellarRenderInformation> configurable =
				RendererRegistry.INSTANCE.configureRender(StellarRenderModel.class);
		
		IRenderedCollection def = configurable.overallCollection();
		
		IRenderTransition transition = configurable.transitionBuilder(new LayerTransformer(), Initial)
				.appendStateWithPassFn(Instance, Functions.<EnumStellarPass>identity(), def)
				.build();
		
		configurable.endSetup(transition);
		
		//Sub Render Constructs
		RendererRegistry.INSTANCE.bind(StellarLayerModel.class, StellarLayerRenderer.INSTANCE);
	}
	
	private static class LayerTransformer implements Function<StellarRenderInformation, LayerRenderInformation> {
		@Override
		public LayerRenderInformation apply(StellarRenderInformation input) {
			return new LayerRenderInformation(input, tessellator);
		}
	}
}
