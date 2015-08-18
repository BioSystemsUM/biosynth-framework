package pt.uminho.sysbio.biosynthframework.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(value = {ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RUNTIME)
public @interface EntityMetaProperty {
	String entityType();
}
