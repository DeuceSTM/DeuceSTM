package org.deuce.transform.asm;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.deuce.objectweb.asm.ClassAdapter;
import org.deuce.objectweb.asm.ClassWriter;
import org.deuce.transform.Exclude;
import org.deuce.transform.util.IgnoreTree;


/**
 * This class implements a chain of transformations. This solution was adapted from the  
 * the Decorator design pattern. We cannot make the own implementations of the ClassEnhancer
 * as decorators, because we need to instantiate a new ClassEnhancer for each instrumentation. 
 * 
 * @author Fernando Miguel Carvalho
 *
 */
@Exclude
public class ClassEnhancerChain implements ClassEnhancer{

	private final ClassEnhancerChain wrapped;
	private final Class<ClassEnhancer> transformation; 

	public ClassEnhancerChain(Class<ClassEnhancer> transformation, ClassEnhancerChain wrapped) {
		this.transformation = transformation;
		this.wrapped = wrapped;
	}

	private ClassEnhancer transformationNewInstance(){
		try {
			return transformation.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/*===========================================================================*
	 *~~~~~~~~~~~~~~~~~~~~~~~    ClassEnhancer Methods      ~~~~~~~~~~~~~~~~~~~~~*
	 *===========================================================================*/

	public List<ClassByteCode> visit(boolean offline, String className, byte[] classfileBuffer) {
		List<ClassByteCode> res;
		if(wrapped == null){
			res = Arrays.asList(new ClassByteCode(className, classfileBuffer));
		}else{
			res = wrapped.visit(offline, className, classfileBuffer);
		}
		List<ClassByteCode> secondEnhancement = new LinkedList<ClassByteCode>();
		for (ClassByteCode cb : res) {
			secondEnhancement.addAll(transformationNewInstance().visit(offline, cb.getClassName(), cb.getBytecode()));
		}
		return secondEnhancement;
	}

}
