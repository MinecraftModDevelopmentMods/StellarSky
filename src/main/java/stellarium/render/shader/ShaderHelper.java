package stellarium.render.shader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;

public class ShaderHelper {
	private static ShaderHelper instance = new ShaderHelper();
	
	public static ShaderHelper getInstance() {
		return instance;
	}
	
	private Map<String, ShaderObject> objectMap = Maps.newHashMap();
	private ShaderObject current = null;
	
	public IShaderObject buildShader(String id, String vertloc, String fragloc) {
		int vertShader = 0, fragShader = 0;
		int programObject;
		
		if(objectMap.containsKey(id))
			ARBShaderObjects.glDeleteObjectARB(objectMap.get(id).programId);
	 
		try {
			vertShader = createShader(vertloc, ARBVertexShader.GL_VERTEX_SHADER_ARB);
			fragShader = createShader(fragloc, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		} catch(Exception exc) {
			throw new RuntimeException(exc);
		}
		
		if(vertShader == 0 || fragShader == 0)
			return null;
	 
		programObject = ARBShaderObjects.glCreateProgramObjectARB();
		
		if(programObject == 0)
			return null;
	 
		ARBShaderObjects.glAttachObjectARB(programObject, vertShader);
		ARBShaderObjects.glAttachObjectARB(programObject, fragShader);
		
		ARBShaderObjects.glLinkProgramARB(programObject);
		if (ARBShaderObjects.glGetObjectParameteriARB(programObject, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
			throw new RuntimeException(getLogInfo(programObject));
		}
	 
		ARBShaderObjects.glValidateProgramARB(programObject);
		if (ARBShaderObjects.glGetObjectParameteriARB(programObject, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
			throw new RuntimeException(getLogInfo(programObject));
		}
		
		return new ShaderObject(programObject);
	}
	
	private void bindShader(ShaderObject object){		
		if(this.current != object) {
			this.releaseCurrentShader();
			this.current = object;
			ARBShaderObjects.glUseProgramObjectARB(object.programId);
		}
	}
	
	private void releaseShader(ShaderObject object) {
		if(this.current == object)
			this.releaseCurrentShader();
	}
	
	private void releaseCurrentShader(){
		this.current = null;
		ARBShaderObjects.glUseProgramObjectARB(0);
	}
	
	
	private int createShader(String resourceName, int shaderType) throws Exception{
		int shader = 0;
		if(resourceName.equals(""))
			return 0;
		
		try {
			shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
		 
			if(shader == 0)
				return 0;
			
			ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(resourceName));
			ARBShaderObjects.glCompileShaderARB(shader);
			
			if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
				throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
			return shader;
		}
		catch(Exception exc) {
			ARBShaderObjects.glDeleteObjectARB(shader);
			throw exc;
		}
	}
		
	private static String getLogInfo(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
			 
	private String readFileAsString(String resourceName) throws Exception {
		StringBuilder source = new StringBuilder();
		
		InputStream in = this.getClass().getResourceAsStream(resourceName);
		
		if(in == null) {
			System.out.println("Maybe loading resources.");
			return "";
		}
		
		Exception exception = null;
		
		BufferedReader reader;
		try{
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			 
			Exception innerExc= null;
			try {
				String line;
				while((line = reader.readLine()) != null)
					source.append(line).append('\n');
			}
			catch(Exception exc) {
				exception = exc;
			}
			finally {
				try {
					reader.close();
				}
				catch(Exception exc) {
					if(innerExc == null)
						innerExc = exc;
					else
						exc.printStackTrace();
				}
			}
			 
			if(innerExc != null)
				throw innerExc;
			}
		catch(Exception exc) {
			exception = exc;
		}
		finally {
			try {
				in.close();
			}
			catch(Exception exc) {
				if(exception == null)
					exception = exc;
				else
					exc.printStackTrace();
			}
			 
			if(exception != null)
				throw exception;
		}
			 
		return source.toString();
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
			
			int location = ARBShaderObjects.glGetUniformLocationARB(this.programId, fieldName);
			if(location == -1)
				return null;
			
			else {
				ShaderUniform uniform = new ShaderUniform(location);
				uniformMap.put(fieldName, uniform);
				return uniform;
			}
		}
	}
	
	private class ShaderUniform implements IUniformField {
		int location;
		
		public ShaderUniform(int location) {
			this.location = location;
		}

		@Override
		public void setInteger(int val) {
			ARBShaderObjects.glUniform1iARB(this.location, val);
		}

		@Override
		public void setDouble(double val) {
			ARBShaderObjects.glUniform1fARB(this.location, (float)val);
		}

		@Override
		public void setSpCoord(SpCoord val) {
			ARBShaderObjects.glUniform2fARB(this.location, (float)val.x, (float)val.y);
		}

		@Override
		public void setVector3(Vector3 val) {
			ARBShaderObjects.glUniform3fARB(this.location, (float)val.getX(), (float)val.getY(), (float)val.getZ());
		}

		@Override
		public void setDouble3(double x, double y, double z) {
			ARBShaderObjects.glUniform3fARB(this.location, (float)x, (float)y, (float)z);
		}

		@Override
		public void setDouble4(double red, double green, double blue, double alpha) {
			ARBShaderObjects.glUniform4fARB(this.location, (float)red, (float)green, (float)blue, (float)alpha);
		}
	}

}
