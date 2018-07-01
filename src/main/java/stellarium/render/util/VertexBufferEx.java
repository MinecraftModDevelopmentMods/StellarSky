package stellarium.render.util;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.fml.common.FMLLog;

// TODO AA Test this on Atmosphere renderer
/**
 * Vertex buffer
 * */
public class VertexBufferEx implements IVertexBuffer {
	private int glBufferId;
	private VertexFormat vertexFormat;
	private int mode, count;

	public VertexBufferEx() {
		this.glBufferId = OpenGlHelper.glGenBuffers();
	}

	public void deleteGlBuffers()
	{
		if (this.glBufferId >= 0)
		{
			OpenGlHelper.glDeleteBuffers(this.glBufferId);
			this.glBufferId = -1;
		}
	}

	@Override
	public void upload(BufferBuilder builder) {
		ByteBuffer data = builder.getByteBuffer();
		OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
		OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, data, OpenGlHelper.GL_STATIC_DRAW);
		OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);

		this.vertexFormat = builder.getVertexFormat();
		this.mode = builder.getDrawMode();
		this.count = builder.getVertexCount();
		//this.count = data.limit() / this.vertexFormat.getSize();

		builder.reset();
	}

	@Override
	public void drawArrays() {
		this.preDraw();
		GlStateManager.glDrawArrays(this.mode, 0, this.count);
		this.postDraw();
	}

	@Override
	public void drawElements(EnumIndexType type, ByteBuffer indices) {
		this.preDraw();
		indices.position(0);
		GL11.glDrawElements(this.mode, indices.limit() / type.size, type.type, indices);
		this.postDraw();
	}

	private void preDraw() {
		OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
		for(int i = 0; i < vertexFormat.getElements().size(); ++i)
			this.preDraw(this.vertexFormat, i);
	}

	private void postDraw() {
		for(int i = 0; i < vertexFormat.getElements().size(); ++i)
			this.postDraw(this.vertexFormat, i);
		OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
	}


	private void preDraw(VertexFormat format, int element) {
		VertexFormatElement attr = format.getElement(element);
		VertexFormatElement.EnumUsage attrType = attr.getUsage();
		int count = attr.getElementCount();
		int constant = attr.getType().getGlConstant();
		int stride = vertexFormat.getSize();
		int offset = format.getOffset(element);

		switch(attrType)
		{
		case POSITION:
			GL11.glVertexPointer(count, constant, stride, offset);
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			break;
		case NORMAL:
			if(count != 3)
			{
				throw new IllegalArgumentException("Normal attribute should have the size 3: " + attr);
			}
			GL11.glNormalPointer(constant, stride, offset);
			GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
			break;
		case COLOR:
			GL11.glColorPointer(count, constant, stride, offset);
			GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
			break;
		case UV:
			OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex());
			GL11.glTexCoordPointer(count, constant, stride, offset);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
			break;
		case PADDING:
			break;
		case GENERIC:
			GL20.glEnableVertexAttribArray(attr.getIndex());
			GL20.glVertexAttribPointer(attr.getIndex(), count, constant, false, stride, offset);
		default:
			FMLLog.log.fatal("Unimplemented vanilla attribute upload: {}", attrType.getDisplayName());
		}
	}

	private void postDraw(VertexFormat format, int element) {
		VertexFormatElement attr = format.getElement(element);
		VertexFormatElement.EnumUsage attrType = attr.getUsage();

		switch(attrType)
		{
		case POSITION:
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			break;
		case NORMAL:
			GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
			break;
		case COLOR:
			GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
			// is this really needed? I just copied this one.
			GlStateManager.resetColor();
			break;
		case UV:
			OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex());
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
			break;
		case PADDING:
			break;
		case GENERIC:
			GL20.glDisableVertexAttribArray(attr.getIndex());
		default:
			FMLLog.log.fatal("Unimplemented vanilla attribute upload: {}", attrType.getDisplayName());
		}
	}
}
