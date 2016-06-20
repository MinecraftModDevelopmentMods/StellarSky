package stellarium.lib.hierarchy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents elements which will accept hierarchy call.
 * It can be single element, or collection of elements.
 * */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HierarchyElement {

	/**
	 * Basic hierarchy type for this element.
	 * The class should have @Hierarchy declared.
	 * */
	Class<?> type();

	/**
	 * ID for the hierarchy element if there are multiple hierarchy elements.
	 * If empty(Default case), it will be replaced by the field number in HEX format.
	 * */
	String id() default "";

	/**
	 * Structure for the hierarchy element.
	 * Default structure for the type is accepted by default.
	 * */
	String structure() default "";
}
