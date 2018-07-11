package stellarium.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class MCUtil {
	private static final Method GET_FOV_MODIFIER = ReflectionHelper.findMethod(EntityRenderer.class,
			"getFOVModifier", "func_78481_a", float.class, boolean.class);
	private static final MethodHandle GFM_HANDLE;

	static {
		MethodHandle handle = null;
		try {
			handle = MethodHandles.lookup().unreflect(GET_FOV_MODIFIER);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		GFM_HANDLE = handle;
	}

	public static float getFOVModifier(EntityRenderer renderer, float partialTicks, boolean useFOVSetting) {
		try {
			return (float) GFM_HANDLE.invokeExact(renderer, partialTicks, useFOVSetting);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
