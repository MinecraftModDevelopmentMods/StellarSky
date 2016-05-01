package stellarium.client.gui.content;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;

public class GuiRenderer implements IRenderer {
	
	private IRenderModel currentModel = null;
	private Tessellator tessellator;
	private TextureManager textureManager;
	
	public GuiRenderer(Tessellator tessellator, TextureManager textureManager) {
		this.tessellator = tessellator;
		this.textureManager = textureManager;
	}

	@Override
	public void bindModel(IRenderModel model) {
		this.currentModel = model;
	}

	@Override
	public void translate(float posX, float posY) {
		GL11.glTranslatef(posX, posY, 0.0f);
	}

	@Override
	public void rotate(float angle, float x, float y, float z) {
		GL11.glRotatef(angle, x, y, z);
	}

	@Override
	public void scale(float scaleX, float scaleY) {
		GL11.glScalef(scaleX, scaleY, 1.0f);
	}

	@Override
	public void color(float red, float green, float blue, float alpha) {
		GL11.glColor4f(red, green, blue, alpha);
	}

	@Override
	public void render(String info, IRectangleBound totalBound, IRectangleBound clipBound) {
		currentModel.renderModel(info, totalBound, clipBound, this.tessellator, this.textureManager);
	}

	@Override
	public void startRender() {
		GL11.glPushMatrix();
	}

	@Override
	public void endRender() {
		GL11.glPopMatrix();
		this.currentModel = null;
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

}
