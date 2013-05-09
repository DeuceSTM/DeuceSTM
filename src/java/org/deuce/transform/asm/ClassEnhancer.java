package org.deuce.transform.asm;

import java.util.List;

import org.deuce.transform.Exclude;

/**
 * This interface specifies an additional transformation to a transactional class.
 * Any STM that requires specific enhancements beyond the standard Deuce instrumentation,
 * then it can provide additional enhancements in subclasses of ClassEnhancer. 
 * The main difference to the visit method of the class ClassTransformer is in the 
 * returned type. In this case, the ClassEnhancer may produce several classes from 
 * the input class. 
 * 
 * @author Fernando Miguel Carvalho
 *
 */
@Exclude
public interface ClassEnhancer{    

	public List<ClassByteCode> visit(boolean offline, String className, byte[] classfileBuffer);

}