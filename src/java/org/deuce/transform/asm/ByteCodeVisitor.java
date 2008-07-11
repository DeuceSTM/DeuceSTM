package org.deuce.transform.asm;

import org.deuce.objectweb.asm.ClassAdapter;
import org.deuce.objectweb.asm.ClassReader;
import org.deuce.objectweb.asm.ClassWriter;


/**
 * Provides a wrapper over {@link ClassAdapter} and provides a {@link #toByteArray()}
 * @author Guy Korland
 * @since 1.0
 */
abstract public class ByteCodeVisitor extends ClassAdapter{

	protected final String className;

	public ByteCodeVisitor( String className) {

		super(null);

		this.cv = new ClassWriter( ClassWriter.COMPUTE_MAXS);
		this.className = className;
	}

	public byte[] toByteArray() {
		return ((ClassWriter)super.cv).toByteArray();
	}
	
	public byte[] visit( byte[] bytes){
		ClassReader cr = new ClassReader(bytes);
		cr.accept(this, 0);
		return this.toByteArray();
	}
}
