package stellarium.lib.hierarchy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents that this method will accept hierarchical call.
 * The method with this annotation should be public.
 * It is recommended to reduce number of parameters.
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HierarchyCall {
	
	/**
	 * ID for this method as hierarchy call acceptor.
	 * */
	String id();
	
	/**
	 * Defines call order.
	 * When call order is specified as custom, automatic sub-call won't be triggered.
	 * */
	EnumCallOrder callOrder() default EnumCallOrder.ParentFirst;
	
	/**
	 * Accepting parameters from parent calls.
	 * The length of the array should match with the number of parameters of current call.
	 * Otherwise, parent parameters will just propagate like default case.
	 * */
	Accept[] acceptParams() default {};
	
	public @interface Accept {
		/**
		 * The value means the type of the parameter
		 * */
		Class<?> value();
		
		/**
		 * Position of the parameter from parent call, checked when there are duplicates in parameter types.
		 * If there is no appropriate parameter, accepts the nearest parameter with the class.
		 * */
		int position() default -1;
		
		/**
		 * Only checked when there are parameter duplicates and position has not been specified.
		 * Accepts certain parameter with occurred index.
		 * */
		int occurIndex() default -1;
	}
}