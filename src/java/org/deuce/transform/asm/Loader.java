package org.deuce.transform.asm;

/**
 * ClassLoader to load field accessors.
 * @author Guy Korland
 */
public class Loader extends ClassLoader{
	
	public Loader(ClassLoader parent){
		super(parent);
	}
	
	public Class defineClass(String name, byte[] b) {
		return defineClass(name, b, 0, b.length);
	}
}
