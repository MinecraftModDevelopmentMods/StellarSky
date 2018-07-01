package stellarium.render.util;

public class VertexReferences {
	private static final BufferBuilderEx BUILDER = new BufferBuilderEx(2097152);
	private static final VertexDirect RENDERER = new VertexDirect();

	public static BufferBuilderEx getBuilder() {
		return BUILDER;
	}

	public static VertexDirect getRenderer() {
		return RENDERER;
	}
}
