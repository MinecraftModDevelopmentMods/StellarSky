package stellarium.render.util;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

/**
 * Vertex non-buffer which directly draws buffer data
 * */
public class VertexDirect implements IVertexBuffer {
	private VertexFormat vertexFormat;
	private ByteBuffer data;
	private int mode, count;

	/** Shorthand method for uploading and drawing arrays */
	public void draw(BufferBuilder builder) {
		this.upload(builder);
		this.drawArrays();
	}

	@Override
	public void upload(BufferBuilder builder) {
		this.vertexFormat = builder.getVertexFormat();
		this.data = builder.getByteBuffer();
		this.mode = builder.getDrawMode();
		this.count = builder.getVertexCount();

		builder.reset();
	}

	@Override
	public void drawArrays() {
		if(this.count > 0) {
			this.preDraw();
			GlStateManager.glDrawArrays(this.mode, 0, this.count);
			this.postDraw();
		}
	}

	@Override
	public void drawElements(EnumIndexType type, ByteBuffer indices) {
		if(this.count > 0) {
			this.preDraw();
			indices.position(0);
			GL11.glDrawElements(this.mode, indices.limit() / type.size, type.type, indices);
			this.postDraw();
		}
	}

	private void preDraw() {
		int stride = vertexFormat.getSize();
		List<VertexFormatElement> list = vertexFormat.getElements();

		for(int element = 0; element < list.size(); ++element)
		{
			VertexFormatElement vfElement = list.get(element);
			data.position(vertexFormat.getOffset(element));

			vfElement.getUsage().preDraw(this.vertexFormat, element, stride, this.data);
		}
	}

	private void postDraw() {
		int stride = vertexFormat.getSize();
		List<VertexFormatElement> list = vertexFormat.getElements();

		for(int element = 0; element < list.size(); ++element)
		{
			VertexFormatElement vfElement = list.get(element);
			vfElement.getUsage().postDraw(this.vertexFormat, element, stride, this.data);
		}
	}
}
