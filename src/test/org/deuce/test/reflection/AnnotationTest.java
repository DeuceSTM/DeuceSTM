package org.deuce.test.reflection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.junit.Assert;

public class AnnotationTest extends TestCase {

	public void testAnnotation() throws Exception {
		MyClass c = new MyClass();
		Method declaredMethod = c.getClass().getDeclaredMethod("myMethod");
		Assert.assertNotNull(declaredMethod.getAnnotation(MyAnnotation.class));
	}


	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyAnnotation {
		String value() default "";
	}

	public static class MyClass {
		@MyAnnotation
		public void myMethod() {}
	}



}
