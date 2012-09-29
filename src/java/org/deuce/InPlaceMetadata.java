package org.deuce;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the metadata classes for each type in an in-place
 * metadata algorithm.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InPlaceMetadata {
	String fieldObjectClass() default "org.deuce.transform.inplacemetadata.type.TxObjectField";

	String fieldIntClass() default "org.deuce.transform.inplacemetadata.type.TxIntField";

	String fieldShortClass() default "org.deuce.transform.inplacemetadata.type.TxShortField";

	String fieldFloatClass() default "org.deuce.transform.inplacemetadata.type.TxFloatField";

	String fieldCharClass() default "org.deuce.transform.inplacemetadata.type.TxCharField";

	String fieldBooleanClass() default "org.deuce.transform.inplacemetadata.type.TxBooleanField";

	String fieldByteClass() default "org.deuce.transform.type.inplacemetadata.TxByteField";

	String fieldDoubleClass() default "org.deuce.transform.type.inplacemetadata.TxDoubleField";

	String fieldLongClass() default "org.deuce.transform.type.inplacemetadata.TxLongField";

	String arrayIntClass() default "";

	String arrayShortClass() default "";

	String arrayCharClass() default "";

	String arrayBooleanClass() default "";

	String arrayByteClass() default "";

	String arrayFloatClass() default "";

	String arrayLongClass() default "";

	String arrayDoubleClass() default "";

	String arrayObjectClass() default "";
}
