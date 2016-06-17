package stellarium.render.sky;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Functions;
import com.google.common.base.Predicates;

import net.minecraft.client.renderer.RenderHelper;
import stellarium.display.DisplayModel;
import stellarium.display.DisplayRenderer;
import stellarium.lib.render.RendererRegistry;
import stellarium.lib.render.hierarchy.IDistributionConfigurable;
import stellarium.lib.render.hierarchy.IRenderState;
import stellarium.lib.render.hierarchy.IRenderTransition;
import stellarium.lib.render.hierarchy.IRenderedCollection;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.sky.SkyModel.EnumSkyRenderable;
import stellarium.render.stellars.EnumStellarRenderState;
import stellarium.world.landscape.LandscapeModel;
import stellarium.world.landscape.LandscapeRenderer;

public enum EnumSkyRenderState implements IRenderState<Void, SkyRenderInformation> {
	Initial() {
		@Override
		public IRenderState<Void, SkyRenderInformation> transitionTo(Void pass, SkyRenderInformation resInfo) {
			RenderHelper.disableStandardItemLighting();
			GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			
			GL11.glPushMatrix();
			GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); // e,n,z

			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			return DisplayRenderBack;
		}
	},
	DisplayRenderBack() {
		@Override
		public IRenderState<Void, SkyRenderInformation> transitionTo(Void pass, SkyRenderInformation resInfo) {
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			return StellarRender;
		}
	},
	StellarRender() {
		@Override
		public IRenderState<Void, SkyRenderInformation> transitionTo(Void pass, SkyRenderInformation resInfo) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			return DisplayRenderFront;
		}
	},
	DisplayRenderFront() {
		@Override
		public IRenderState<Void, SkyRenderInformation> transitionTo(Void pass, SkyRenderInformation resInfo) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_FOG);
			return LandscapeRender;
		}
	},
	LandscapeRender() {
		@Override
		public IRenderState<Void, SkyRenderInformation> transitionTo(Void pass, SkyRenderInformation resInfo) {
			GL11.glDisable(GL11.GL_FOG);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_ALPHA_TEST);

			GL11.glDisable(GL11.GL_BLEND);

			GL11.glDepthMask(true);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glPopMatrix();

			return null;
		}
	};
	
	public static void constructRender() {
		IDistributionConfigurable<Void, SkyRenderInformation> configurable = RendererRegistry.INSTANCE.configureRender(SkyModel.class);
		
		IRenderedCollection<EnumSkyRenderable> collection = configurable.overallCollection();
		IRenderedCollection stellar = collection.getFiltered(Predicates.equalTo(EnumSkyRenderable.Stellar));
		IRenderedCollection display = collection.getFiltered(Predicates.equalTo(EnumSkyRenderable.Display));
		IRenderedCollection landscape = collection.getFiltered(Predicates.equalTo(EnumSkyRenderable.Landscape));

		IRenderTransition transition = configurable.transitionBuilder(Functions.<SkyRenderInformation>identity(), Initial)
				.appendState(DisplayRenderBack, false, display)
				.appendState(StellarRender, null, stellar)
				.appendState(DisplayRenderFront, true, display)
				.appendState(LandscapeRender, null, landscape)
				.build();
		
		configurable.endSetup(transition);
		
		//Sub Render Constructs
		EnumStellarRenderState.constructRender();
		RendererRegistry.INSTANCE.bind(DisplayModel.class, new DisplayRenderer());
		RendererRegistry.INSTANCE.bind(LandscapeModel.class, new LandscapeRenderer());
	}
}