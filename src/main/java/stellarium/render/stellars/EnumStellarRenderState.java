package stellarium.render.stellars;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Function;
import com.google.common.base.Predicates;

import net.minecraft.client.renderer.OpenGlHelper;
import stellarium.lib.render.RendererRegistry;
import stellarium.lib.render.hierarchy.IDistributionBuilder;
import stellarium.lib.render.hierarchy.IRenderDistribution;
import stellarium.lib.render.hierarchy.IRenderState;
import stellarium.render.sky.SkyRenderInformation;
import stellarium.render.stellars.StellarModel.EnumStellarRenderable;
import stellarium.render.stellars.atmosphere.EnumAtmospherePass;
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
				return UnbindDominateScatter;
			else return PrepareDominateScatter;
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
		resInfo.getActiveShader().releaseShader();
		
	    GL11.glDepthMask(true);
	    GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void constructRender() {
		IRenderDistribution<EnumStellarRenderable> distribution = RendererRegistry.INSTANCE.generateDistribution(StellarModel.class);
		IRenderDistribution atmosphere = distribution.filter(Predicates.equalTo(EnumStellarRenderable.Atmosphere));
		IRenderDistribution sky = distribution.filter(Predicates.equalTo(EnumStellarRenderable.Stellar));

		IDistributionBuilder<Void, SkyRenderInformation> builder = null;
		builder.transitionBuilder(new InfoTransformer(), Initial).appendState(
				PrepareDominateScatter, EnumAtmospherePass.PrepareDominateScatter, atmosphere);
	}
	
	private static class InfoTransformer implements Function<SkyRenderInformation, StellarRenderInformation> {
		@Override
		public StellarRenderInformation apply(SkyRenderInformation input) {
			return new StellarRenderInformation(input);
		}
	}
}
