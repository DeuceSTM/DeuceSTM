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
	protected boolean isInterface;

	public ByteCodeVisitor( String className) {

		super(new CommonClassWriter( ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, className));

		this.className = className;
	}
	
	@Override
	public void visit(final int version, final int access, final String name,
			final String signature, final String superName,
			final String[] interfaces) {
		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	/**
	 * Creates a new method without and additions.
	 */
	public MethodVisitor createMethod( int access, String name, String desc,
			String signature, String[] exceptions) {
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	public byte[] toByteArray() {
		return ((ClassWriter)super.cv).toByteArray();
	}

	public byte[] visit( byte[] bytes){
		ClassReader cr = new ClassReader(bytes);
		cr.accept(this, ClassReader.EXPAND_FRAMES);
		return this.toByteArray();
	}
	
	public String getClassName() {
		return className;
	}
	
	/**
	 * FIXME This is a work around and should be fixed better in the future.
	 * By overriding the getCommonSuperClass we avoid loading 
	 * @author Guy Korland
	 *
	 */
	private static class CommonClassWriter extends ClassWriter{
		
		final static private Method getCallerClassLoaderMethod;
		final static private Method findLoadedClassMethod;
		static{
			Method callerMethod;
			Method findMethod;
			try {
				callerMethod = ClassLoader.class.getMethod("getCallerClassLoader");
				callerMethod.setAccessible(true);
				findMethod = ClassLoader.class.getMethod("findLoadedClass", String.class);
				findMethod.setAccessible(true);
			} catch (Exception e) {
				callerMethod = findMethod = null;
			}
			getCallerClassLoaderMethod = callerMethod;
			findLoadedClassMethod = findMethod;
		}
		
		public CommonClassWriter(int flags, String className) {
			super(flags);
		}
		@Override
		protected String getCommonSuperClass(final String type1, final String type2)
	    {
			if( type1.equals(type2))
				return type1;
			
			if( getCallerClassLoaderMethod != null){
				try{
					ClassLoader classLoader = (ClassLoader) getCallerClassLoaderMethod.invoke(null);
					if( findLoadedClassMethod.invoke(classLoader, type1) == null)
						return  "java/lang/Object";

					if( findLoadedClassMethod.invoke(classLoader, type2) == null)
						return  "java/lang/Object";
				}
				catch(Exception e){
					return  "java/lang/Object";
				}
			}
			
			return super.getCommonSuperClass(type1, type2);
	    }
	}
}
