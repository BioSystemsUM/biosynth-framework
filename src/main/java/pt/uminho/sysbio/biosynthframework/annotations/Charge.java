package pt.uminho.sysbio.biosynthframework.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
@MetaProperty
@EntityMetaProperty(entityType="")
public @interface Charge {

}
