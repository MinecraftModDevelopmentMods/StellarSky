package stellarium.render;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import stellarapi.api.lib.math.Vector3;

public class ShaderHelper {
	public static ShaderHelper instance = new ShaderHelper();
			
	private int currentShader;
	protected Map<String, Integer> shaderMap = Maps.newHashMap();
	private String beforesh = "";
	
	public void initShader(String shname, String vertloc, String fragloc){
		
		int vertShader = 0, fragShader = 0;
		int programObject;
	 
		try {
			vertShader = createShader(vertloc, ARBVertexShader.GL_VERTEX_SHADER_ARB);
			fragShader = createShader(fragloc, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		} catch(Exception exc) {
			throw new RuntimeException(exc);
		}
		
		if(vertShader == 0 || fragShader == 0)
			return;
	 
		programObject = ARBShaderObjects.glCreateProgramObjectARB();
		
		if(programObject == 0)
			return;
	 
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
		
		shaderMap.put(shname, programObject);
	}
	
	public void useShader(String shname){		
		if(!shname.equals(this.beforesh)) {
			releaseShader();
			
			this.beforesh = shname;
			Integer id = shaderMap.get(shname);
			
			if(id == null)
				throw new IllegalStateException(
						String.format("The shader for the name %s is not initialized.", shname));
			
			this.currentShader = id.intValue();
			ARBShaderObjects.glUseProgramObjectARB(this.currentShader);
		}
	}
	
	public void releaseShader(){
		this.beforesh = "";
		this.currentShader = 0;
		ARBShaderObjects.glUseProgramObjectARB(this.currentShader);
	}
	
	public void setValuei(String value_name, int val){
		int my_value_loc = ARBShaderObjects.glGetUniformLocationARB(this.currentShader, value_name);
		ARBShaderObjects.glUniform1iARB(my_value_loc, val);
	}
	
	public void setValuef(String value_name, double val){
		int my_value_loc = ARBShaderObjects.glGetUniformLocationARB(this.currentShader, value_name);
		ARBShaderObjects.glUniform1fARB(my_value_loc, (float)val);
	}
	
	public void setValueVec(String value_name, Vector3 val){
		int my_value_loc = ARBShaderObjects.glGetUniformLocationARB(this.currentShader, value_name);
		ARBShaderObjects.glUniform3fARB(my_value_loc, (float)val.getX(), (float)val.getY(), (float)val.getZ());
	}
	
	public void setValue4f(String value_name, double red, double green, double blue, double alpha){
		int my_value_loc = ARBShaderObjects.glGetUniformLocationARB(this.currentShader, value_name);
		ARBShaderObjects.glUniform4fARB(my_value_loc, (float)red, (float)green, (float)blue, (float)alpha);
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
			System.out.println("Maybe loading resource.");
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

}
