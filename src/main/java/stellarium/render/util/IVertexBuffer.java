package stellarium.render.util;

import java.nio.ByteBuffer;

import net.minecraft.client.renderer.BufferBuilder;

public interface IVertexBuffer {
	public void upload(BufferBuilder builder);

	public void drawArrays();
	public void drawElements(EnumIndexType type, ByteBuffer indices);
}
