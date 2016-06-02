package stellarium.lib.hierarchy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class HierarchyWrapper {

	private Class<?> hierarchyType;
	private List<FieldElementDescription> descriptions = Lists.newArrayList();
	private Map<Object, FieldElementDescription> descriptionMap = Maps.newHashMap();
	private Map<String, CallDescription> calls = Maps.newHashMap();
	
	private Field wrapperField;
	private Class<?> wrapperClass;
	
	public static final String MAIN_FIELD = "main";
	public static final String HELPER_FIELD = "wraphelper";
	public static final String WRAPPER_REF_FIELD = "wrapper";
	
	public static Type getWrapperType(Class<?> loaded) {
		return Type.getType(String.format("%s%s;",
				Type.getDescriptor(HierarchyWrapper.class).replace("HierarchyWrapper;", ""),
				loaded.getSimpleName().replace('.', '/')));
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
					evaluator.evaluateID(index++, description.defaultId) : description.defaultId;
			descriptionMap.put(id, description);
		}

		//Method Check Phase
		for(Method method : hierarchyType.getDeclaredMethods()) {
			HierarchyCall callDesc = method.getAnnotation(HierarchyCall.class);
			if(callDesc == null)
				continue;

			calls.put(callDesc.id(), new CallDescription(method, callDesc));
		}
	}
	
	public void initialize() throws NoSuchMethodException, NoSuchFieldException {
		ClassWriter cw = new ClassWriter(0);
		Type wrapperType = getWrapperType(this.hierarchyType);
		Type originType = Type.getType(this.hierarchyType);

		//cw.visit(Opcodes.V1_6, access, name, signature, superName, interfaces);
		
		cw.visitSource(".dynamic", null);
		{
			cw.visitField(Opcodes.ACC_PRIVATE, MAIN_FIELD,
					originType.getDescriptor(), null, null).visitEnd();
			cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, HELPER_FIELD,
					Type.getDescriptor(HierarchyWrapper.class), null, null).visitEnd();
		}
		
		//Class Initialization
		GeneratorAdapter adapter = new GeneratorAdapter(
				cw.visitMethod(Opcodes.ACC_PUBLIC, "<clinit>",
						Type.getMethodDescriptor(Type.VOID_TYPE), null, null),
				Opcodes.ACC_PUBLIC, "<clinit>", null);
		
		adapter.visitCode();
		{
			adapter.visitLdcInsn(originType);
			adapter.invokeStatic(Type.getType(HierarchyWrapper.class),
					org.objectweb.asm.commons.Method.getMethod(
							HierarchyWrapper.class.getMethod("findHelper", Class.class)));
			adapter.putStatic(wrapperType, HELPER_FIELD, Type.getType(HierarchyWrapper.class));
		}
		adapter.visitEnd();


		//Object Initialization
		adapter = new GeneratorAdapter(
				cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>",
						Type.getMethodDescriptor(Type.VOID_TYPE, originType), null, null),
				Opcodes.ACC_PUBLIC, "<init>", null);

		adapter.visitCode();
		{
			adapter.loadThis();
			adapter.invokeConstructor(Type.getType(Object.class),
					org.objectweb.asm.commons.Method.getMethod(Object.class.getConstructor()));
			adapter.loadArg(0);
			adapter.putField(wrapperType, MAIN_FIELD, originType);
			adapter.swap();
			adapter.putField(originType, WRAPPER_REF_FIELD, wrapperType);
			adapter.returnValue();
			adapter.visitMaxs(2, 2);
		}
		adapter.visitEnd();


		//Copy method for each Call
		for(Map.Entry<String, CallDescription> entry : calls.entrySet()) {
			String callId = entry.getKey();
			CallDescription parentDescription = entry.getValue();
			
			adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC,
					org.objectweb.asm.commons.Method.getMethod(parentDescription.method),
					null, null, cw);
			
			adapter.visitCode();
			{
				if(parentDescription.callOrder.isParentFirst) {
					adapter.loadThis();
					adapter.getField(wrapperType, MAIN_FIELD, Type.getType(this.hierarchyType));
					this.generateCall(adapter, this.hierarchyType, parentDescription.method, null);
				}

				if(parentDescription.callOrder.subCall) {
					int fieldCount = 0;
					for(FieldElementDescription element : this.descriptions) {
						HierarchyWrapper elementWrapper = HierarchyDistributor.INSTANCE.getSafely(element.elementType);

						if(!elementWrapper.calls.containsKey(callId))
							throw new IllegalStateException(
									String.format("Impossible Hierarchy Call for id %s,"
											+ "No such method in element class %s",
											callId, element.elementType));

						CallDescription childDescription = elementWrapper.calls.get(entry.getKey());
						Class<?>[] parameterTypes = parentDescription.method.getParameterTypes();
						Class<?>[] childParamTypes = childDescription.method.getParameterTypes();

						int[] args = this.getArgumentPos(childDescription, parameterTypes, childParamTypes, parentDescription.method);

						// Iteration
						// Gets Iterator Provider and Helper
						adapter.getStatic(wrapperType, HELPER_FIELD, Type.getType(HierarchyWrapper.class));
						adapter.loadThis();
						adapter.getField(wrapperType, MAIN_FIELD, originType);
						adapter.swap();
						// HELPER_FIELD / MAIN_FIELD / THIS
						adapter.pop();
						// HELPER_FIELD / MAIN_FIELD
						adapter.getField(originType, element.field.getName(), Type.getType(element.field.getType()));
						adapter.swap();
						adapter.pop();
						//  HELPER_FIELD / SUB_ELEMENT_CONTAINER
						adapter.visitLdcInsn(fieldCount);
						// HELPER_FIELD / SUB_ELEMENT_CONTAINER / CONTAINER_INDEX

						adapter.invokeVirtual(Type.getType(HierarchyWrapper.class),
								org.objectweb.asm.commons.Method.getMethod(
										HierarchyWrapper.class.getMethod("iteFor", Object.class, Integer.class)));

						// HELPER_FIELD / SUB_ELEMENT_CONTAINER / CONTAINER_INDEX / Iterator<Sub>
						adapter.swap();
						adapter.pop();
						adapter.swap();
						adapter.pop();
						adapter.swap();
						adapter.pop();
						// Iterator<Sub>

						Label iteration = adapter.newLabel();
						Label iterationEnd = adapter.newLabel();

						adapter.mark(iteration);
						{
							//HasNext check
							adapter.invokeInterface(Type.getType(Iterator.class),
									org.objectweb.asm.commons.Method.getMethod(Iterator.class.getMethod("hasNext")));
							adapter.ifZCmp(Opcodes.IFEQ, iterationEnd);
							adapter.pop();

							//Gets next
							adapter.invokeInterface(Type.getType(Iterator.class),
									org.objectweb.asm.commons.Method.getMethod(Iterator.class.getMethod("next")));
							// Iterator<Sub> / Sub
							adapter.getField(Type.getType(element.elementType), WRAPPER_REF_FIELD, getWrapperType(element.elementType));
							adapter.swap();
							adapter.pop();
							// Iterator<Sub> / SUB_WRAPPER
							
							this.generateCall(adapter, element.elementType, childDescription.method, args);

							// Pops next
							adapter.pop();
							adapter.goTo(iteration);
						}
						adapter.mark(iterationEnd);

						//Pops Iterator
						adapter.pop();
						
						fieldCount++;
					}
				}

				if(!parentDescription.callOrder.isParentFirst) {
					adapter.loadThis();
					adapter.getField(wrapperType, "main", Type.getType(this.hierarchyType));
					this.generateCall(adapter, this.hierarchyType, parentDescription.method, null);
				}

				adapter.returnValue();

				int maxStack = 1;
				int maxLocals = 1;

				for(Class<?> paramType : parentDescription.method.getParameterTypes()) {
					maxStack += Type.getType(paramType).getSize();
					maxLocals++;
				}

				if(parentDescription.callOrder.subCall) {
					maxStack = Math.max(4, maxStack + 2);
					maxLocals += 6;
				}

				adapter.visitMaxs(maxStack, maxLocals);

			}
			adapter.visitEnd();
		}
		
		//Class<?> wrapperClass;
		
		this.wrapperField = hierarchyType.getClass().getDeclaredField(WRAPPER_REF_FIELD);
		
		for(CallDescription callDesc : calls.values()) {
			Method rawCall = callDesc.method;
			callDesc.wrappedMethod = wrapperClass.getMethod(rawCall.getName(), rawCall.getParameterTypes());
		}
	}
	
	// Generates call with instance from the top of the stack
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
		
		if(args != null) {
			// Pops Arguments
			for(int i = 0; i < invokedMethod.getParameterCount(); i++) {
				int size = Type.getType(invokedMethod.getParameterTypes()[i]).getSize();
				if(size == 1)
					adapter.pop();
				else adapter.pop2();
			}
		}
	}
	
	private int[] getArgumentPos(CallDescription childDescription, Class<?>[] parameterTypes, Class<?>[] childParamTypes, Method parent) {
		int[] list = new int[childParamTypes.length];
		BitSet set = new BitSet(parameterTypes.length);

		for(int i = 0; i < childParamTypes.length; i++) {
			HierarchyCall.Accept accept = childDescription.accepts[i];
			
			set.clear();
			for(int j = 0; j < parameterTypes.length; j++)
				if(parameterTypes[j].equals(accept.value()))
					set.set(j);
			
			if(set.isEmpty())
				throw new IllegalStateException(
						String.format("Can't find parameter for type %s for method %s!",
								accept.value(), parent));

			if(accept.position() >= 0) {
				int next = set.nextSetBit(accept.position());
				int previous = set.previousSetBit(accept.position());
				
				if(next < 0 || accept.position() - previous < next - accept.position() && previous >= 0)
					list[i] = previous;
				else list[i] = next;
			} else if(accept.occurIndex() >= 0) {
				int occurIndex = Math.min(accept.occurIndex(), set.size()-1);
				int current = set.nextSetBit(0);
				for(int j = 0; j < occurIndex; j++)
					current = set.nextSetBit(current);
				list[i] = current;
			} else {
				list[i] = set.nextSetBit(0);
			}
			
			//Previously checked
			assert(list[i] > 0);
		}
		
		return list;
	}
	
	// Only for internal use
	@Deprecated
	public Iterator iteFor(Object container, int elementIndex) {
		return descriptions.get(elementIndex).structure.iteratorFor(container);
	}
	
	/**
	 * Triggers Hierarchical Call.
	 * @param instance the hierarchy instance
	 * @param callId specifies the call
	 * @param parameters the parameters for this call
	 * */
	public void call(Object instance, String callId, Object[] parameters) {
		if(this.wrapperField == null) {
			try {
				this.initialize();
			} catch (ReflectiveOperationException cause) {
				throw new IllegalStateException(
						String.format("Can't define wrapper for %s, Unexpected", this.hierarchyType), cause);
			}
		}

		try {
			Object wrapper = wrapperField.get(instance);
			if(wrapper == null)
				wrapperField.set(
						instance, wrapperClass.getConstructor(this.hierarchyType).newInstance(instance));
		} catch (IllegalAccessException cause) {
			throw new IllegalStateException(
					String.format("Access Denied on field %s, Unexpected", this.wrapperField), cause);
		} catch (ReflectiveOperationException cause) {
			throw new IllegalStateException(
					String.format("Wrong wrapper definition for %s, Unexpected", this.hierarchyType), cause);
		}

		CallDescription callDesc = calls.get(callId);
		try {
			callDesc.wrappedMethod.invoke(instance, parameters);
		} catch (IllegalAccessException cause) {
			throw new IllegalStateException(
					String.format("Access Denied on method %s, Unexpected", callDesc.wrappedMethod), cause);
		} catch (InvocationTargetException cause) {
			Throwables.propagate(cause);
		}
	}
	
	/**
	 * Triggers Call for elements on certain field.
	 * @param instance the parent hierarchy instance
	 * @param fieldId specifies the field
	 * @param callId specifies the call
	 * @param subParams the parameters for sub-elements on the field
	 * */
	public void callFor(Object instance, Object fieldId, String callId, Object... subParams) {
		FieldElementDescription description = descriptionMap.get(fieldId);
		HierarchyWrapper subWrapper = HierarchyDistributor.INSTANCE.get(description.elementType);
		Iterator ite = description.structure.iteratorFor(instance);
		
		while(ite.hasNext())
			subWrapper.call(ite.next(), callId, subParams);
	}
	
	Set elementIds() {
		return descriptionMap.keySet();
	}

	Iterator elementIteOnField(Object instance, Object fieldId) {
		return descriptionMap.get(fieldId).structure.iteratorFor(instance);
	}

	private class FieldElementDescription {
		final Field field;
		final Class<?> elementType;
		final String defaultId;
		final IHierarchyStructure structure;

		public FieldElementDescription(Field field, HierarchyElement elemDesc) {
			this.field = field;
			this.elementType = elemDesc.type();
			this.defaultId = elemDesc.id();

			IHierarchyStructure customStr = HierarchyDistributor.INSTANCE.structureMap.get(elemDesc.structure());
			IHierarchyStructure defaultStr = HierarchyDistributor.INSTANCE.defaultStrMap.get(field.getDeclaringClass());
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
	}
	
	private class CallDescription {
		final Method method;
		final EnumCallOrder callOrder;
		final HierarchyCall.Accept[] accepts;
		Method wrappedMethod;

		public CallDescription(Method method, HierarchyCall callDesc) {
			this.method = method;
			this.callOrder = callDesc.callOrder();
			this.accepts = callDesc.acceptParams();
		}
	}
}