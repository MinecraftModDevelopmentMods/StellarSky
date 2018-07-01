package stellarium.render.util;

import org.lwjgl.opengl.GL11;

public enum EnumIndexType {
	BYTE(GL11.GL_UNSIGNED_BYTE, 1),
	SHORT(GL11.GL_UNSIGNED_SHORT, 2),
	INT(GL11.GL_UNSIGNED_INT, 4);

	public final int type;
	public final int size;

	EnumIndexType(int type, int size) {
		this.type = type;
		this.size = size;
	}
}
