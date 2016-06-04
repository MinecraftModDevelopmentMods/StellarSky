package stellarium.render.stellars;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Function;
import com.google.common.base.Predicates;

import stellarium.client.ClientSettings;
import stellarium.lib.render.RendererRegistry;
import stellarium.lib.render.hierarchy.IDistributionConfigurable;
import stellarium.lib.render.hierarchy.IRenderState;
import stellarium.lib.render.hierarchy.IRenderTransition;
import stellarium.lib.render.hierarchy.IRenderedCollection;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.sky.SkyRenderInformation;
import stellarium.render.stellars.StellarModel.EnumStellarRenderable;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.atmosphere.AtmosphereModel;
import stellarium.render.stellars.atmosphere.AtmosphereRenderer;
import stellarium.render.stellars.atmosphere.AtmosphereSettings;
import stellarium.render.stellars.atmosphere.EnumAtmospherePass;
import stellarium.render.stellars.phased.EnumPhasedRenderState;
import stellarium.render.stellars.phased.StellarRenderInformation;

public enum EnumStellarRenderState implements IRenderState<Void, StellarRenderInformation> {
	Initial() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			if(resInfo.isFrameBufferEnabled) {
				return PrepareDominateScatter;
			} else {
				GL11.glShadeModel(GL11.GL_SMOOTH);
				return SetupSurfaceScatter;
			}
		}
	},

	//Dominate Scatter Rendering
	PrepareDominateScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			return RenderDominateScatter;
		}
	},
	RenderDominateScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			return FinalizeDominateScatter;
		}
	},
	FinalizeDominateScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			if(resInfo.isFrameBufferEnabled)
				return BindDominateScatter;
			else {
				endRendering(resInfo);
				return null;
			}
		}
	},

	//Stellar Rendering
	SetupSurfaceScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			return RenderSurfaceScatter;
		}
	}, RenderSurfaceScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			GL11.glShadeModel(GL11.GL_FLAT);
			return SetupPointScatter;
		}
	},
	SetupPointScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			return RenderPointScatter;
		}
	}, RenderPointScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
		    GL11.glDepthMask(true);
		    GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			return SetupOpaque;
		}
	},
	SetupOpaque() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			return RenderOpaque;
		}
	}, RenderOpaque() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
		    GL11.glDepthMask(false);
		    GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			GL11.glShadeModel(GL11.GL_FLAT);
			return SetupOpaqueScatter;
		}
	},
	SetupOpaqueScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			return RenderOpaqueScatter;
		}
	}, RenderOpaqueScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			GL11.glShadeModel(GL11.GL_SMOOTH);
			if(resInfo.isFrameBufferEnabled)
				return RenderCachedAtmosphere;
			else return PrepareDominateScatter;
		}
	},
	
	RenderCachedAtmosphere() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			return UnbindDominateScatter;
		}
	},

	//Dominate Scatter Binding
	BindDominateScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			resInfo.setAtmCallList(-1);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			return SetupSurfaceScatter;
		}
	}, UnbindDominateScatter() {
		@Override
		public IRenderState<Void, StellarRenderInformation> transitionTo(Void pass, StellarRenderInformation resInfo) {
			endRendering(resInfo);
			return null;
		}
	}
	;

	private static void endRendering(StellarRenderInformation resInfo) {
		ShaderHelper.getInstance().releaseCurrentShader();

	    GL11.glDepthMask(true);
	    GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void constructRender() {
		IDistributionConfigurable<Void, SkyRenderInformation> builder = RendererRegistry.INSTANCE.configureRender(StellarModel.class);
		
		IRenderedCollection<EnumStellarRenderable> collection = builder.overallCollection();
		
		IRenderedCollection atmosphere = collection.getFiltered(Predicates.equalTo(EnumStellarRenderable.Atmosphere));
		IRenderedCollection stellar = collection.getFiltered(Predicates.equalTo(EnumStellarRenderable.Stellar));
		
		atmosphere.transformSettings(new Function<ClientSettings, AtmosphereSettings>() {
			@Override
			public AtmosphereSettings apply(ClientSettings input) {
				return (AtmosphereSettings) input.getSubConfig(AtmosphereSettings.KEY);
			}
		});

		stellar.transformSettings(new Function<ClientSettings, Void>() {
			@Override
			public Void apply(ClientSettings input) {
				return null;
			}
		});
		
		IRenderTransition transition = builder.transitionBuilder(new InfoTransformer(), Initial)
				.appendState(PrepareDominateScatter, EnumAtmospherePass.PrepareDominateScatter, atmosphere)
				.appendState(RenderDominateScatter, EnumStellarPass.DominateScatter, stellar)
				.appendState(FinalizeDominateScatter, EnumAtmospherePass.FinalizeDominateScatter, atmosphere)
				
				.appendState(SetupSurfaceScatter, EnumAtmospherePass.SetupSurfaceScatter, atmosphere)
				.appendState(RenderSurfaceScatter, EnumStellarPass.SurfaceScatter, stellar)
				.appendState(SetupPointScatter, EnumAtmospherePass.SetupPointScatter, atmosphere)
				.appendState(RenderPointScatter, EnumStellarPass.PointScatter, stellar)
				.appendState(SetupOpaque, EnumAtmospherePass.SetupOpaque, atmosphere)
				.appendState(RenderOpaque, EnumStellarPass.Opaque, stellar)
				.appendState(SetupOpaqueScatter, EnumAtmospherePass.SetupOpaqueScatter, atmosphere)
				.appendState(RenderOpaqueScatter, EnumStellarPass.OpaqueScatter, stellar)
				
				.appendState(RenderCachedAtmosphere, EnumAtmospherePass.RenderCachedDominate, atmosphere)
				
				.appendState(BindDominateScatter, EnumAtmospherePass.BindDomination, atmosphere)
				.appendState(UnbindDominateScatter, EnumAtmospherePass.UnbindDomination, atmosphere)
				.build();
		
		builder.endSetup(transition);
		
		//Sub Render Constructs
		RendererRegistry.INSTANCE.bind(AtmosphereModel.class, AtmosphereRenderer.INSTANCE);
		EnumPhasedRenderState.constructRender();
	}
	
	private static class InfoTransformer implements Function<SkyRenderInformation, StellarRenderInformation> {
		@Override
		public StellarRenderInformation apply(SkyRenderInformation input) {
			return new StellarRenderInformation(input);
		}
	}
}
