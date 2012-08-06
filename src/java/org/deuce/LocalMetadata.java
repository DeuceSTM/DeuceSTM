package org.deuce;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalMetadata {
	String fieldObjectClass() default "org.deuce.transform.localmetadata.type.TxObjectField";
	String fieldIntClass() default "org.deuce.transform.localmetadata.type.TxIntField";
	String fieldShortClass() default "org.deuce.transform.localmetadata.type.TxShortField";
	String fieldFloatClass() default "org.deuce.transform.localmetadata.type.TxFloatField";
	String fieldCharClass() default "org.deuce.transform.localmetadata.type.TxCharField";
	String fieldBooleanClass() default "org.deuce.transform.localmetadata.type.TxBooleanField";
	String fieldByteClass() default "org.deuce.transform.type.localmetadata.TxByteField";
	String fieldDoubleClass() default "org.deuce.transform.type.localmetadata.TxDoubleField";
	String fieldLongClass() default "org.deuce.transform.type.localmetadata.TxLongField";
	
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
