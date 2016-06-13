package stellarium.render.shader;

import java.util.Map;

import com.google.common.collect.Maps;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;

public class SwitchableShaders implements IShaderObject {

	private IShaderObject[] similars;
	private int index;
	private Map<String, IUniformField> commonFields = Maps.newHashMap();
	
	public SwitchableShaders(IShaderObject... similars) {
		this.similars = similars;
	}
	
	public void switchShader(int i) {
		this.index = i;
	}
	
	public IShaderObject getCurrent() {
		return similars[this.index];
	}
	
	@Override
	public void bindShader() {
		similars[this.index].bindShader();
	}

	@Override
	public void releaseShader() {
		similars[this.index].releaseShader();
	}

	@Override
	public IUniformField getField(String fieldName) {
		IUniformField[] fields = new IUniformField[similars.length];
		for(int i = 0; i < similars.length; i++)
			fields[i] = similars[i].getField(fieldName);

		IUniformField uniform = new SwitchableUniformField(fields);
		commonFields.put(fieldName, uniform);
		return uniform;
	}

	private class SwitchableUniformField implements IUniformField {
		
		private IUniformField[] fields;
		
		public SwitchableUniformField(IUniformField[] fields) {
			this.fields = fields;
		}

		@Override
		public void setInteger(int val) {
			fields[index].setInteger(val);
		}

		@Override
		public void setDouble(double val) {
			fields[index].setDouble(val);
		}

		@Override
		public void setSpCoord(SpCoord val) {
			fields[index].setSpCoord(val);
		}

		@Override
		public void setVector3(Vector3 val) {
			fields[index].setVector3(val);
		}

		@Override
		public void setDouble3(double x, double y, double z) {
			fields[index].setDouble3(x, y, z);
		}

		@Override
		public void setDouble4(double red, double green, double blue, double alpha) {
			fields[index].setDouble4(red, green, blue, alpha);
		}
	}
}
