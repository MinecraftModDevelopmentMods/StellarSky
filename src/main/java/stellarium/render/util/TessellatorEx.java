package stellarium.render.util;

import net.minecraft.client.renderer.WorldVertexBufferUploader;

// TODO Mix this with helper methods?
public class TessellatorEx {
	private final BufferBuilderEx buffer;
    private final WorldVertexBufferUploader vboUploader = new WorldVertexBufferUploader();
    /** The static instance of the Tessellator. */
    private static final TessellatorEx INSTANCE = new TessellatorEx(2097152);

    public static TessellatorEx getInstance()
    {
        return INSTANCE;
    }

    public TessellatorEx(int bufferSize)
    {
        this.buffer = new BufferBuilderEx(bufferSize);
    }

    /**
     * Draws the data set up in this tessellator and resets the state to prepare for new drawing.
     */
    public void draw()
    {
        this.buffer.finishDrawing();
        this.vboUploader.draw(this.buffer);
    }

    public BufferBuilderEx getBuffer()
    {
        return this.buffer;
    }
}
