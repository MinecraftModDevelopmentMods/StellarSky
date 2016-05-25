package stellarium.render.sky;

import org.lwjgl.opengl.GL11;

import stellarium.render.base.IGenericRenderer;
import stellarium.render.stellars.StellarRenderer;
import stellarium.world.landscape.LandscapeRenderer;

public class SkyRenderer implements IGenericRenderer<SkyRendererSettings, Void, SkyModel, SkyRenderInformation> {
	
	private IGenericRenderer displayRenderer;
	private StellarRenderer stellarRenderer;
	private LandscapeRenderer landscapeRenderer;
	
	@Override
	public void initialize(SkyRendererSettings settings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preRender(SkyRendererSettings settings, SkyRenderInformation info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderPass(SkyModel model, Void pass, SkyRenderInformation info) {
		GL11.glPushMatrix();
		GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); // e,n,z

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		displayRenderer.renderPass(model.getDisplayModel(), false, info);

		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

		stellarRenderer.renderPass(model.getStellarModel(), null, info);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		displayRenderer.renderPass(model.getDisplayModel(), true, info);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		landscapeRenderer.renderPass(model.getLandscapeModel(), null, info);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		GL11.glDisable(GL11.GL_BLEND);

		GL11.glDepthMask(true);
		GL11.glPopMatrix();
	}

	@Override
	public void postRender(SkyRendererSettings settings, SkyRenderInformation info) {
		// TODO Auto-generated method stub
		
	}
}
