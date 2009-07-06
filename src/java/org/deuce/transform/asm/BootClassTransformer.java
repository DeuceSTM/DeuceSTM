package org.deuce.transform.asm;

import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.transform.asm.method.StaticMethodTransformer;

public class BootClassTransformer extends ClassTransformer {

	public BootClassTransformer(String className) {
		super(className);
	}
	
	@Override
	protected StaticMethodTransformer createStaticMethodTransformer(MethodVisitor originalMethod){
		return null;
	}

}
