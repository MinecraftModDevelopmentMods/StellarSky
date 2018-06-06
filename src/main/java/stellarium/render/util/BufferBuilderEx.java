package stellarium.render.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import stellarapi.api.lib.math.Vector3;

/**
 * Extended buffer builder.
 * To specify color, use only {@link #color(float, float, float, float)} and {@link #color(int, int, int, int)}.
 * */
public class BufferBuilderEx extends BufferBuilder {
	private ByteBuffer byteBuffer;
	private MethodHandle vFormatIndex, nextVFI;

	public BufferBuilderEx(int bufferSizeIn) {
		super(bufferSizeIn);
		this.byteBuffer = ReflectionHelper.getPrivateValue(BufferBuilder.class,
				this, "byteBuffer", "field_179001_a");
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			this.vFormatIndex = lookup.unreflectGetter(
					ReflectionHelper.findField(BufferBuilder.class,
							"vertexFormatIndex", "field_181678_g")).bindTo(this);
			this.nextVFI = lookup.unreflect(
					ReflectionHelper.findMethod(BufferBuilder.class,
							"nextVertexFormatIndex", "func_181667_k")).bindTo(this);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
	}

	public BufferBuilderEx pos(Vector3 pos) {
		super.pos(pos.getX(), pos.getY(), pos.getZ());
		return this;
	}

	public BufferBuilderEx pos(Vector3 pos, double scale) {
		super.pos(pos.getX() * scale, pos.getY() * scale, pos.getZ() * scale);
		return this;
	}

	public BufferBuilderEx normal(Vector3 normal) {
		super.normal((float)normal.getX(), (float)normal.getY(), (float)normal.getZ());
		return this;
	}

	public BufferBuilder color(float red, float green, float blue, float alpha) {
		if (this.isColorDisabled())
		{
			return this;
		}
		else
		{
			try {
				int vertexFormatIndex = (int) vFormatIndex.invokeExact();
				int i = this.getVertexCount() * this.getVertexFormat().getSize() + this.getVertexFormat().getOffset(vertexFormatIndex);
				VertexFormatElement vertexFormatElement = this.getVertexFormat().getElement(vertexFormatIndex);

				if(vertexFormatElement.getType() == VertexFormatElement.EnumType.FLOAT) {
					this.byteBuffer.putFloat(i, red);
					this.byteBuffer.putFloat(i + 4, green);
					this.byteBuffer.putFloat(i + 8, blue);
					this.byteBuffer.putFloat(i + 12, alpha);
					nextVFI.invokeExact();
					return this;
				} else {
					return super.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
				}
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}
}
