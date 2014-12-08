package stellarium.viewrender.render;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.Color;
import stellarium.util.math.*;

public class CRenderEngine {
	public static CRenderEngine instance=new CRenderEngine();
	
	protected TextureManager reng;
	
	private static final int shadernum = 12;
	
	private int cnt = 0;
	private int nowshader;
	 
	protected Map<String, Integer> ShaderList = new HashMap<String, Integer>();

	private int program[] = new int[shadernum];

	private boolean hasatm = false;
	
	public RAtmHost host;
	
	public double res;
	
	private String beforesh = "";
	private String beforetex = "";

	public double con = 1.0;
	
	public CRenderEngine(){
		InitShader("pointy", "point", "point");
		InitShader("pointyatm", "pointatm", "pointatm");
		InitShader("fuzzy","point","fuzzy");
		InitShader("fuzzyatm","pointatm","fuzzyatm");
		InitShader("dsobj", "dsobj", "dsobj");
		InitShader("dsobj", "dsobjatm", "dsobjatm");
		InitShader("img", "img", "img");
		InitShader("imgatm", "imgatm", "imgatm");
	}
	
	
	public void InitShader(String shname, String verts, String frags){
		verts = "shaders/" + verts + ".vsh";
		frags = "shaders/" + frags + ".fsh";
		
		int vertShader = 0, fragShader = 0;
	 
		try {
			vertShader = createShader(verts, ARBVertexShader.GL_VERTEX_SHADER_ARB);
			fragShader = createShader(frags, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return;
		}
	
		finally {
			if(vertShader == 0 || fragShader == 0)
				return;
		}
	 
		program[cnt] = ARBShaderObjects.glCreateProgramObjectARB();
		
		if(program[cnt] == 0)
			return;
	 
		ARBShaderObjects.glAttachObjectARB(program[cnt], vertShader);
		ARBShaderObjects.glAttachObjectARB(program[cnt], fragShader);
		
		ARBShaderObjects.glLinkProgramARB(program[cnt]);
		if (ARBShaderObjects.glGetObjectParameteriARB(program[cnt], ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
			System.err.println(getLogInfo(program[cnt]));
			return;
		}
	 
		ARBShaderObjects.glValidateProgramARB(program[cnt]);
		if (ARBShaderObjects.glGetObjectParameteriARB(program[cnt], ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
			System.err.println(getLogInfo(program[cnt]));
			return;
		}
		
		ShaderList.put(shname, cnt);
		
		cnt++;
	}
	
	public void useShader(String shname){
		if(hasatm) shname += "atm";
		
		if( !shname.equals(beforesh) ){
			releaseShader();
			
			beforesh = shname;
			
			nowshader = ShaderList.get(shname);
			ARBShaderObjects.glUseProgramObjectARB(program[ nowshader ]);
		}
		
		if(hasatm){
			setValue("zen", host.Zen);
			setValue("dtor", host.DtoR);
			setValue("seeing", host.Seeing);
			setValue("particle_size", host.particle_size);
		}
		
		setValue("res", res);
	}
	
	protected void releaseShader(){
		ARBShaderObjects.glUseProgramObjectARB(0);
	}
	
	public void setValue(String Value_name, double val){
		int my_value_loc = ARBShaderObjects.glGetUniformLocationARB(nowshader, Value_name);
		ARBShaderObjects.glUniform1fARB(my_value_loc, (float)val);
	}
	
	public void setValue(String Value_name, EVector val){
		int my_value_loc = ARBShaderObjects.glGetUniformLocationARB(nowshader, Value_name);
		ARBShaderObjects.glUniform3fARB(my_value_loc, val.getCoord(0).asFloat(), val.getCoord(1).asFloat(), val.getCoord(2).asFloat());
	}
	
	public void setValue(String Value_name, Color val){
		int my_value_loc = ARBShaderObjects.glGetUniformLocationARB(nowshader, Value_name);
		ARBShaderObjects.glUniform3fARB(my_value_loc, (float)val.r/255.0f, (float)val.g/255.0f, (float)val.b/255.0f);
	}
	
	
	public void bindTexture(String name){
		if(!name.equals(beforetex))
			reng.bindTexture(new ResourceLocation("textures/"+name));
	}
	
	
	public void SetAtm(RAtmHost h){
		hasatm = true;
		host = h;
	}
	
	public void UnsetAtm(){
		hasatm = false;
	}
	
	public void SetRes(double Res){
		res = Res;
	}
	
	
	public int createShader(String filename, int shaderType) throws Exception{
		int shader = 0;
		try {
			shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
		 
			if(shader == 0)
				return 0;
			
			ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
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
			 
	private String readFileAsString(String filename) throws Exception {
		StringBuilder source = new StringBuilder();
			 
		FileInputStream in = new FileInputStream(filename);
			 
		Exception exception = null;
			 
		BufferedReader reader;
		try{
			reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			 
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
