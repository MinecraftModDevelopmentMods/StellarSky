package stellarium.render.shader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
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
	
	private ContextCapabilities contextcapabilities = GLContext.getCapabilities();
	
	public IShaderObject buildShader(String id, ResourceLocation vertloc, ResourceLocation fragloc) {
		int vertShader = 0, fragShader = 0;
		int programObject;
		
		if(objectMap.containsKey(id)) {
			//Delete object
			OpenGlHelper.func_153187_e(objectMap.get(id).programId);
		}
	 
		try {
			vertShader = createShader(vertloc, ARBVertexShader.GL_VERTEX_SHADER_ARB);
			fragShader = createShader(fragloc, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		} catch(Exception exc) {
			throw new RuntimeException(exc);
		}
		
		if(vertShader == 0 || fragShader == 0)
			return null;
	 
		//Creates program object
		programObject = OpenGlHelper.func_153183_d();
		
		if(programObject == 0)
			return null;
	 
		//Attatches shaders to object
		OpenGlHelper.func_153178_b(programObject, vertShader);
		OpenGlHelper.func_153178_b(programObject, fragShader);
		
		//Links the program
		OpenGlHelper.func_153179_f(programObject);
		
		//Check if link is done correctly
		if (OpenGlHelper.func_153175_a(programObject, OpenGlHelper.field_153207_o) == GL11.GL_FALSE) {
			throw new RuntimeException();
		}
	 
		/*ARBShaderObjects.glValidateProgramARB(programObject);
		if (ARBShaderObjects.glGetObjectParameteriARB(programObject, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
			throw new RuntimeException(getLogInfo(programObject));
		}*/
		
		ShaderObject object = new ShaderObject(programObject);
		objectMap.put(id, object);
		
		return object;
	}
	
	private void bindShader(ShaderObject object){		
		if(this.current != object) {
			this.releaseCurrentShader();
			this.current = object;
			
			//Use program
			OpenGlHelper.func_153161_d(object.programId);
		}
	}
	
	private void releaseShader(ShaderObject object) {
		if(this.current == object)
			this.releaseCurrentShader();
	}
	
	public void releaseCurrentShader(){
		this.current = null;
		
		//Use empty program
		OpenGlHelper.func_153161_d(0);
	}
	
	
	private int createShader(ResourceLocation location, int shaderType) throws Exception{
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
            shader = OpenGlHelper.func_153195_b(shaderType);
			
			//Provide source to the shader
            OpenGlHelper.func_153169_a(shader, bytebuffer);
            
            //Compiles the shader
            OpenGlHelper.func_153170_c(shader);
			
			//Check compile
			if (OpenGlHelper.func_153157_c(shader, OpenGlHelper.field_153208_p) == GL11.GL_FALSE)
				throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
			return shader;
		}
		catch(Exception exc) {
			OpenGlHelper.func_153180_a(shader);
			throw exc;
		}
	}
	
	private static String getLogInfo(int programObject) {
		return OpenGlHelper.func_153166_e(programObject, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB);
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
			int location = OpenGlHelper.func_153194_a(this.programId, fieldName);
			if(location == -1)
				StellarSky.logger.error("Invalid field %s has claimed!", fieldName);
			
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
			OpenGlHelper.func_153163_f(this.location, val);
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
