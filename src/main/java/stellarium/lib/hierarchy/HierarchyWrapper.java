package stellarium.lib.hierarchy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import stellarium.lib.hierarchy.structure.IHierarchyStructure;
import stellarium.lib.hierarchy.structure.SingletonStructure;
import stellarium.render.sky.SkyModel;
import stellarium.render.stellars.StellarModel;

public class HierarchyWrapper {

	private Class<?> hierarchyType;
	private List<FieldElementDescription> descriptions = Lists.newArrayList();
	private Map<Object, FieldElementDescription> descriptionMap = Maps.newHashMap();
	
	private Field wrapperField;
	private Class<?> wrapperClass;
	
	public static final String MAIN_FIELD = "main";
	public static final String HELPER_FIELD = "wraphelper";
	public static final String WRAPPER_REF_FIELD = "wrapper";
	
	public static Type getWrapperType(Class<?> loaded) {
		return getWrapperType(loaded.getName());
	}
	
	public static Type getWrapperType(String classFullName) {
		return Type.getType(String.format("%s%s;",
				Type.getDescriptor(HierarchyWrapper.class).replace("HierarchyWrapper;", "Wrapped_"),
				classFullName.substring(classFullName.lastIndexOf('.')+1)));
	}
	
	/** For internal usage */
	@Deprecated
	public static HierarchyWrapper findHelper(Class<?> hierarchyType) {
		return HierarchyDistributor.INSTANCE.get(hierarchyType);
	}

	public HierarchyWrapper(Class<?> hierarchyType) {
		this.hierarchyType = hierarchyType;

		Hierarchy typeDesc = hierarchyType.getAnnotation(Hierarchy.class);

		//Check is already done here.
		assert(typeDesc != null);

		//Field Check Phase
		for(Field field : hierarchyType.getDeclaredFields()) {
			HierarchyElement elemDesc = field.getAnnotation(HierarchyElement.class);
			if(elemDesc == null)
				continue;
			
			descriptions.add(new FieldElementDescription(field, elemDesc));
		}

		//ID Evaluation Phase
		IIDEvaluator evaluator = HierarchyDistributor.INSTANCE.evaluatorMap.get(typeDesc.idEvaluator());

		int index = 0;
		for(FieldElementDescription description : descriptions) {
			Object id = evaluator != null?
					evaluator.evaluateID(index++, description.defaultId) :
						description.defaultId.isEmpty()? Integer.toHexString(index) : description.defaultId;
			descriptionMap.put(id, description);
		}
	}
	
	public static void workOn(Object wrapped, Object wrapper) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		System.out.println(wrapped);
		System.out.println(wrapper);
		
		Field field = wrapped.getClass().getField(WRAPPER_REF_FIELD);
		field.set(wrapped, wrapper);
		System.out.println(field.get(wrapped));
	}
	
	private static final WrapperClassLoader loader = new WrapperClassLoader();

	private static class WrapperClassLoader extends ClassLoader {
		WrapperClassLoader() {
			super(WrapperClassLoader.class.getClassLoader());
		}
		
		Class<?> loadClass(String name, byte[] byteArray) {
			return super.defineClass(name, byteArray, 0, byteArray.length);
		}
	}
	
	// Generates call with instance from the top of the stack, end with the top entry of the stack removed.
	private void generateCall(GeneratorAdapter adapter, Class<?> invokedType, Method invokedMethod, int[] args) {
		// Loads Arguments
		for(int i = 0; i < invokedMethod.getParameterCount(); i++)
			adapter.loadArg(args != null? args[i] : i);
		
		// Invoke
		if(invokedType.isInterface()) {
			adapter.invokeInterface(Type.getType(invokedType),
					org.objectweb.asm.commons.Method.getMethod(invokedMethod));
		} else {
			adapter.invokeVirtual(Type.getType(invokedType),
					org.objectweb.asm.commons.Method.getMethod(invokedMethod));
		}
	}
	
	// Only for internal use
	@Deprecated
	public Iterator iteFor(Object container, int elementIndex) {
		return descriptions.get(elementIndex).structure.iteratorFor(container);
	}

	
	Map fields() {
		return this.descriptionMap;
	}

	Iterator elementIteOnField(Object instance, Object fieldId) {
		FieldElementDescription description = descriptionMap.get(fieldId);
		return description.structure.iteratorFor(description.getElement(instance));
	}

	private class FieldElementDescription implements IFieldElementDescription {
		final Field field;
		final Class<?> elementType;
		final String defaultId;
		final IHierarchyStructure structure;

		public FieldElementDescription(Field field, HierarchyElement elemDesc) {
			this.field = field;
			this.elementType = elemDesc.type();
			this.defaultId = elemDesc.id();

			IHierarchyStructure customStr = HierarchyDistributor.INSTANCE.structureMap.get(elemDesc.structure());
			IHierarchyStructure defaultStr = HierarchyDistributor.INSTANCE.defaultStrMap.get(field.getType());
			IHierarchyStructure defDefStr = SingletonStructure.INSTANCE;

			this.structure = customStr != null? customStr : (defaultStr != null? defaultStr : defDefStr);

			field.setAccessible(true);
		}

		public Object getElement(Object instance) {
			try {
				return field.get(instance);
			} catch (IllegalAccessException cause) {
				throw new IllegalStateException(
						String.format("Access Denied on field %s, Unexpected", this.field), cause);
			}
		}

		@Override
		public Class<?> getElementType() {
			return this.elementType;
		}

		@Override
		public IHierarchyStructure getStructure() {
			return this.structure;
		}
	}
}