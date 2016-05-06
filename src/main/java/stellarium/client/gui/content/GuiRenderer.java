package stellarium.client.gui.content;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;

public class GuiRenderer implements IRenderer {
	
	private IRenderModel currentModel = null;
	private Tessellator tessellator;
	private TextureManager textureManager;
	private boolean matrixPushedTillNextRender;
	private List<IMatrixTransformation> transformation = Lists.newArrayList();
	private List<IMatrixTransformation> innerTrans = Lists.newArrayList();
	private RectangleBound temp = new RectangleBound(0,0,0,0), tempClip = new RectangleBound(0,0,0,0);
		
	public GuiRenderer(Minecraft minecraft) {
		this.tessellator = Tessellator.instance;
		this.textureManager = minecraft.getTextureManager();
	}
	
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
		if(this.matrixPushedTillNextRender)
			innerTrans.add(new Translation(posX, posY));
		else transformation.add(new Translation(posX, posY));
	}

	@Override
	public void rotate(float angle, float x, float y, float z) {
		if(this.matrixPushedTillNextRender)
			innerTrans.add(new Rotation(angle, x, y, z));
		else transformation.add(new Rotation(angle, x, y, z));
	}

	@Override
	public void scale(float scaleX, float scaleY) {
		if(this.matrixPushedTillNextRender)
			innerTrans.add(new Scale(scaleX, scaleY));
		else transformation.add(new Scale(scaleX, scaleY));
	}

	@Override
	public void color(float red, float green, float blue, float alpha) {
		GL11.glColor4f(red, green, blue, alpha);
	}
	
	@Override
	public void pushMatrixTillNextRender() {
		if(this.matrixPushedTillNextRender)
			return;
		
		this.matrixPushedTillNextRender = true;
	}

	@Override
	public void render(String info, IRectangleBound totalBound, IRectangleBound clipBound) {
		if(currentModel == null)
			throw new IllegalStateException("The model haven't got binded!");

		float centerX = totalBound.getMainX(0.5f);
		float centerY = totalBound.getMainY(0.5f);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(centerX, centerY, 0.0f);
		
		for(IMatrixTransformation trans : this.transformation)
			trans.doTransform();
		for(IMatrixTransformation trans : this.innerTrans)
			trans.doTransform();
		
		temp.set(totalBound);
		temp.posX -= centerX;
		temp.posY -= centerY;
		tempClip.set(clipBound);
		tempClip.posX -= centerX;
		tempClip.posY -= centerY;
		
		currentModel.renderModel(info, this.temp, this.tempClip, this.tessellator, this.textureManager);
		GL11.glPopMatrix();
		
		if(this.matrixPushedTillNextRender)
		{
			innerTrans.clear();
			this.matrixPushedTillNextRender = false;
		}
	}

	@Override
	public void startRender() { }

	@Override
	public void endRender() {
		transformation.clear();
		innerTrans.clear();
		this.currentModel = null;
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	
	private interface IMatrixTransformation {
		public void doTransform();
	}
	
	private class Translation implements IMatrixTransformation {
		
		private float x, y;
		
		public Translation(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public void doTransform() {
			GL11.glTranslatef(x, y, 0.0f);
		}
	}
	
	private class Scale implements IMatrixTransformation {
		
		private float x, y;
		
		public Scale(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public void doTransform() {
			GL11.glScalef(x, y, 1.0f);
		}
	}

	private class Rotation implements IMatrixTransformation {
		
		private float x, y, z, angle;
		
		public Rotation(float angle, float x, float y, float z) {
			this.angle = angle;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public void doTransform() {
			GL11.glRotatef(angle, x, y, z);
		}
	}
}
