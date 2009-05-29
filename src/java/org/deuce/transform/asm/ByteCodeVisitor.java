package org.deuce.transform.asm;

import java.lang.reflect.Method;

import org.deuce.objectweb.asm.ClassAdapter;
import org.deuce.objectweb.asm.ClassReader;
import org.deuce.objectweb.asm.ClassWriter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.commons.JSRInlinerAdapter;


/**
 * Provides a wrapper over {@link ClassAdapter} and provides a {@link #toByteArray()}
 * @author Guy Korland
 * @since 1.0
 */
public class ByteCodeVisitor extends ClassAdapter{

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		return new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions);
	}

	protected final String className;
	//The maximal bytecode version to tranasform.
	private int maximalversion = Integer.MAX_VALUE;

	public ByteCodeVisitor( String className) {

		super(new CommonClassWriter( ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, className));
		this.className = className;
	}
	
	@Override
	public void visit(final int version, final int access, final String name,
			final String signature, final String superName, final String[] interfaces) {
		if(version > maximalversion) // version higher than allowed 
			throw VersionException.INSTANCE;
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	public byte[] visit( byte[] bytes){
		ClassReader cr = new ClassReader(bytes);
		cr.accept(this, ClassReader.EXPAND_FRAMES);
		return ((ClassWriter)super.cv).toByteArray();
	}
	
	public byte[] visit( byte[] bytes, int maximalversion){
		this.maximalversion = maximalversion;
		try{
			return visit(bytes);
		}
		catch(VersionException ve){
			return bytes;
		}
	}

	
	public String getClassName() {
		return className;
	}
	
	private static class VersionException extends RuntimeException{
		private static final long serialVersionUID = 1L;
		public static VersionException INSTANCE = new VersionException();
	}
	
	/**
	 * FIXME This is a work around and should be fixed better in the future.
	 * By overriding the getCommonSuperClass we avoid loading 
	 * @author Guy Korland
	 *
	 */
	private static class CommonClassWriter extends ClassWriter{
		
		public CommonClassWriter(int flags, String className) {
			super(flags);
		}
		@Override
		protected String getCommonSuperClass(final String type1, final String type2)
	    {
			if( type1.equals(type2))
				return type1;
			
			return  "java/lang/Object";
	    }
	}
}
