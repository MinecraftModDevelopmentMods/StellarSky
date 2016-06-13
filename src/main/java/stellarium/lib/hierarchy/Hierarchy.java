package stellarium.lib.hierarchy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for hierarchy acceptor types.
 * Any types without this annotation can't accept hierarchy call.
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hierarchy {
	/**
	 * Gets id evaluator.
	 * By default, id is evaluated from the id specified.
	 * */
	String idEvaluator() default "";
}