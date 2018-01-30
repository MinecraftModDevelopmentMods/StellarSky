package stellarium.render.shader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.StellarSky;

public class ShaderHelper {
	private static ShaderHelper instance = new ShaderHelper();

	public static ShaderHelper getInstance() {
		return instance;
	}

	private Map<String, ShaderObject> objectMap = Maps.newHashMap();
	private ShaderObject current = null;
	private int prevShader = 0;

	private ContextCapabilities contextcapabilities = GLContext.getCapabilities();

	public IShaderObject buildShader(String id, ResourceLocation vertloc, ResourceLocation fragloc) {
		int vertShader = 0, fragShader = 0;
		int programObject;

		if(objectMap.containsKey(id)) {
			//Delete object
			OpenGlHelper.glDeleteProgram(objectMap.get(id).programId);
		}

		vertShader = createShader(vertloc, OpenGlHelper.GL_VERTEX_SHADER);
		fragShader = createShader(fragloc, OpenGlHelper.GL_FRAGMENT_SHADER);


		if(vertShader == 0 || fragShader == 0)
			return null;

		//Creates program object
		programObject = OpenGlHelper.glCreateProgram();

		if(programObject == 0)
			return null;

		//Attatches shaders to object
		OpenGlHelper.glAttachShader(programObject, vertShader);
		OpenGlHelper.glAttachShader(programObject, fragShader);

		//Links the program
		OpenGlHelper.glLinkProgram(programObject);

		//Check if link is done correctly
		if (OpenGlHelper.glGetProgrami(programObject, OpenGlHelper.GL_LINK_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException(getLogInfo(programObject));
		}

		// Just to use it in other places later.
		/*ARBShaderObjects.glValidateProgramARB(programObject);
		if (ARBShaderObjects.glGetObjectParameteriARB(programObject, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
			throw new RuntimeException(getLogInfo(programObject));
		}*/

		ShaderObject object = new ShaderObject(programObject);
		objectMap.put(id, object);

		return object;
	}

	private void bindShader(ShaderObject object){
		if(this.current == null)
			this.prevShader = GlStateManager.glGetInteger(GL20.GL_CURRENT_PROGRAM);

		if(this.current != object) {
			this.current = object;

			//Use program
			OpenGlHelper.glUseProgram(object.programId);
		}		
	}

	private void releaseShader(ShaderObject object) {
		if(this.current == object)
			this.releaseCurrentShader();
	}

	public void releaseCurrentShader(){
		this.current = null;

		//Use empty program
		OpenGlHelper.glUseProgram(this.prevShader);
	}


	private int createShader(ResourceLocation location, int shaderType) {
		int shader = 0;
		if(location == null)
			return 0;

		try {
			BufferedInputStream bufferedinputstream = new BufferedInputStream(
					Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream());
			byte[] abyte = IOUtils.toByteArray(bufferedinputstream);
			ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length);
			bytebuffer.put(abyte);
			bytebuffer.position(0);

			//Creates the shader object
			shader = OpenGlHelper.glCreateShader(shaderType);

			//Provide source to the shader
			OpenGlHelper.glShaderSource(shader, bytebuffer);

			//Compiles the shader
			OpenGlHelper.glCompileShader(shader);

			//Check compile
			if (OpenGlHelper.glGetShaderi(shader, OpenGlHelper.GL_COMPILE_STATUS) == GL11.GL_FALSE)
				throw new RuntimeException(String.format("Error creating shader %s: %s", location, OpenGlHelper.glGetShaderInfoLog(shader, 32768)));
			return shader;
		}
		catch(IOException exc) {
			OpenGlHelper.glDeleteShader(shader);
			throw Throwables.propagate(exc);
		}
	}

	private static String getLogInfo(int programObject) {
		return OpenGlHelper.glGetProgramInfoLog(programObject, 32768);
	}

	private class ShaderObject implements IShaderObject {
		private int programId;
		private Map<String, ShaderUniform> uniformMap = Maps.newHashMap();

		public ShaderObject(int programId) {
			this.programId = programId;
		}

		@Override
		public void bindShader() {
			ShaderHelper.this.bindShader(this);
		}

		@Override
		public void releaseShader() {
			ShaderHelper.this.releaseShader(this);
		}

		@Override
		public IUniformField getField(String fieldName) {
			if(uniformMap.containsKey(fieldName))
				return uniformMap.get(fieldName);

			//Gets the location
			int location = OpenGlHelper.glGetUniformLocation(this.programId, fieldName);
			if(location == -1)
				StellarSky.INSTANCE.getLogger().error(String.format("Invalid field %s has claimed while loading shaders!", fieldName));

			ShaderUniform uniform = new ShaderUniform(location);
			uniformMap.put(fieldName, uniform);
			return uniform;
		}
	}

	private class ShaderUniform implements IUniformField {
		int location;

		public ShaderUniform(int location) {
			this.location = location;
		}

		@Override
		public void setInteger(int val) {
			OpenGlHelper.glUniform1i(this.location, val);
		}

		@Override
		public void setDouble(double val) {
			//Temporal, since openglhelper does not expose something important (below)
			if (!contextcapabilities.OpenGL21)
				ARBShaderObjects.glUniform1fARB(this.location, (float) val);
			else
				GL20.glUniform1f(this.location, (float) val);
		}

		@Override
		public void setSpCoord(SpCoord val) {
			//Temporal, since openglhelper does not expose something important (below)
			if (!contextcapabilities.OpenGL21)
				ARBShaderObjects.glUniform2fARB(this.location, (float)val.x, (float)val.y);
			else
				GL20.glUniform2f(this.location, (float)val.x, (float)val.y);
		}

		@Override
		public void setVector3(Vector3 val) {
			//Temporal, since openglhelper does not expose something important (below)
			if (!contextcapabilities.OpenGL21)
				ARBShaderObjects.glUniform3fARB(this.location, (float)val.getX(), (float)val.getY(), (float)val.getZ());
			else
				GL20.glUniform3f(this.location, (float)val.getX(), (float)val.getY(), (float)val.getZ());
		}

		@Override
		public void setDouble3(double x, double y, double z) {
			//Temporal, since openglhelper does not expose something important (below)
			if (!contextcapabilities.OpenGL21)
				ARBShaderObjects.glUniform3fARB(this.location, (float)x, (float)y, (float)z);
			else
				GL20.glUniform3f(this.location, (float)x, (float)y, (float)z);
		}

		@Override
		public void setDouble4(double red, double green, double blue, double alpha) {
			//Temporal, since openglhelper does not expose something important (below)
			if (!contextcapabilities.OpenGL21)
				ARBShaderObjects.glUniform4fARB(this.location, (float)red, (float)green, (float)blue, (float)alpha);
			else
				GL20.glUniform4f(this.location, (float)red, (float)green, (float)blue, (float)alpha);
		}
	}

}
