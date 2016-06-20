package stellarium.render.stellars.atmosphere;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.stellars.access.IAtmSphereRenderer;

public class AtmosphereSphereRenderer implements IAtmSphereRenderer {

	private static final int STRIDE_IN_FLOAT = 8;
	
	private int renderCallList = -1;

	private double length;
	private boolean hasNormal, inverted;

	private FloatBuffer verticesPointer;
	private ByteBuffer indicesPointer;
	
	private VertexFormat vertexFormatTextured;
	private VertexFormat vertexFormatDefault;
	
	public AtmosphereSphereRenderer(double length, boolean hasNormal, boolean inverted, FloatBuffer vertexPointer, ByteBuffer indicesPointer) {
		this.length = length;
		this.hasNormal = hasNormal;
		this.inverted = inverted;

		//if(this.hasNormal) {
		//	this.vertexFormatTextured = DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL;
		//	this.vertexFormatDefault = DefaultVertexFormats.
		//}
	}

	public void initialize(Vector3[][] displayvec, int fragLong, int fragLat, boolean hasTexture) {
		if(this.renderCallList >= 0)
			GLAllocation.deleteDisplayLists(this.renderCallList);
		
        this.renderCallList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.renderCallList, GL11.GL_COMPILE);
        
        if(this.inverted)
        	GL11.glFrontFace(GL11.GL_CW);
        
        this.drawDisplay(displayvec, fragLong, fragLat, hasTexture);
        
        if(this.inverted)
        	GL11.glFrontFace(GL11.GL_CCW);
        
        GL11.glEndList();
	}

	private Vector3 temporal = new Vector3();
	
	private void drawDisplay(Vector3[][] displayvec, int fragLong, int fragLat, boolean hasTexture) {
		verticesPointer.position(0);
		GL11.glVertexPointer(3, STRIDE_IN_FLOAT << 2, this.verticesPointer);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

		if(hasTexture) {
			verticesPointer.position(3);
			GL11.glTexCoordPointer(2, STRIDE_IN_FLOAT << 2, this.verticesPointer);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}

		if(hasNormal) {
			verticesPointer.position(5);
			GL11.glNormalPointer(STRIDE_IN_FLOAT << 2, this.verticesPointer);
			GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		}

		indicesPointer.position(0);
		GL11.glDrawElements(GL11.GL_QUADS, fragLong * fragLat * 4, GL11.GL_UNSIGNED_INT, this.indicesPointer);

		if(hasNormal) {
			GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		}

		if(hasTexture) {
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}

	@Override
	public void renderAtmSphere() {
		// TODO Auto-generated method stub

	}

}
