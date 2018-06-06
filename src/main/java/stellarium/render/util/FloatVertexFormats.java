package stellarium.render.util;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class FloatVertexFormats {
	public static final VertexFormat POSITION_COLOR_F = new VertexFormat();
	public static final VertexFormat POSITION_TEX_COLOR_F = new VertexFormat();
	public static final VertexFormat POSITION_TEX_COLOR_F_NORMAL = new VertexFormat();

	public static final VertexFormatElement COLOR_4F = new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.COLOR, 4);

	static {
		POSITION_COLOR_F.addElement(DefaultVertexFormats.POSITION_3F);
		POSITION_COLOR_F.addElement(COLOR_4F);
		POSITION_TEX_COLOR_F.addElement(DefaultVertexFormats.POSITION_3F);
		POSITION_TEX_COLOR_F.addElement(DefaultVertexFormats.TEX_2F);
		POSITION_TEX_COLOR_F.addElement(COLOR_4F);
		POSITION_TEX_COLOR_F_NORMAL.addElement(DefaultVertexFormats.POSITION_3F);
		POSITION_TEX_COLOR_F_NORMAL.addElement(DefaultVertexFormats.TEX_2F);
		POSITION_TEX_COLOR_F_NORMAL.addElement(COLOR_4F);
		POSITION_TEX_COLOR_F_NORMAL.addElement(DefaultVertexFormats.NORMAL_3B);
	}
}
