package org.deuce.reflection;

import org.deuce.objectweb.asm.ClassVisitor;
import org.deuce.objectweb.asm.ClassWriter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;

/**
 * Creates ASM created classes that represents that provides fast fields "reflection" mechanism. 
 */
public class ASMFieldFactory 
{
	private interface MethodBuilder{
		void createGetMethod(ClassWriter cw);
		void createSetMethod(ClassWriter cw);
		String[] getInterfaces();
	}

	public static byte[] getBooleanField(final String declaringClass, final String getAccessor, final String setAccessor){

		final String[] interfaces = new String[]{ Type.getInternalName(BooleanField.class)}; 

		return getField( declaringClass, new MethodBuilder(){

			public void createGetMethod(ClassWriter cw) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)Z", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, getAccessor, "(L" + declaringClass + ";)Z");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;Z)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitVarInsn(Opcodes.ILOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, setAccessor, "(L" + declaringClass + ";Z)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}
	

	public static byte[] getByteField(final String declaringClass, final String getAccessor, final String setAccessor){

		final String[] interfaces = new String[]{ Type.getInternalName(ByteField.class)}; 

		return getField( declaringClass, new MethodBuilder(){

			public void createGetMethod(ClassWriter cw) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)B", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, getAccessor, "(L" + declaringClass + ";)B");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;B)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitVarInsn(Opcodes.ILOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, setAccessor, "(L" + declaringClass + ";B)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}
	
	public static byte[] getCharField(final String declaringClass, final String getAccessor, final String setAccessor){

		final String[] interfaces = new String[]{ Type.getInternalName(CharField.class)}; 

		return getField( declaringClass, new MethodBuilder(){

			public void createGetMethod(ClassWriter cw) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)C", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, getAccessor, "(L" + declaringClass + ";)C");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;C)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitVarInsn(Opcodes.ILOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, setAccessor, "(L" + declaringClass + ";C)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}
	
	public static byte[] getShortField(final String declaringClass, final String getAccessor, final String setAccessor){

		final String[] interfaces = new String[]{ Type.getInternalName(ShortField.class)}; 

		return getField( declaringClass, new MethodBuilder(){

			public void createGetMethod(ClassWriter cw) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)S", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, getAccessor, "(L" + declaringClass + ";)S");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;S)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitVarInsn(Opcodes.ILOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, setAccessor, "(L" + declaringClass + ";S)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}
	
	public static byte[] getIntField(final String declaringClass, final String getAccessor, final String setAccessor){

		final String[] interfaces = new String[]{ Type.getInternalName(IntField.class)}; 

		return getField( declaringClass, new MethodBuilder(){

			public void createGetMethod(ClassWriter cw) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)I", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, getAccessor, "(L" + declaringClass + ";)I");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;I)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitVarInsn(Opcodes.ILOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, setAccessor, "(L" + declaringClass + ";I)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}
	
	public static byte[] getLongField(final String declaringClass, final String getAccessor, final String setAccessor){

		final String[] interfaces = new String[]{ Type.getInternalName(LongField.class)}; 

		return getField( declaringClass, new MethodBuilder(){

			public void createGetMethod(ClassWriter cw) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)J", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, getAccessor, "(L" + declaringClass + ";)J");
				mv.visitInsn(Opcodes.LRETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;J)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitVarInsn(Opcodes.LLOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, setAccessor, "(L" + declaringClass + ";J)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(3, 4);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}
	
	public static byte[] getFloatField(final String declaringClass, final String getAccessor, final String setAccessor){

		final String[] interfaces = new String[]{ Type.getInternalName(FloatField.class)}; 

		return getField( declaringClass, new MethodBuilder(){

			public void createGetMethod(ClassWriter cw) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)F", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, getAccessor, "(L" + declaringClass + ";)F");
				mv.visitInsn(Opcodes.FRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;F)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitVarInsn(Opcodes.FLOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, setAccessor, "(L" + declaringClass + ";F)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}
	
	public static byte[] getDoubleField(final String declaringClass, final String getAccessor, final String setAccessor){

		final String[] interfaces = new String[]{ Type.getInternalName(DoubleField.class)}; 

		return getField( declaringClass, new MethodBuilder(){

			public void createGetMethod(ClassWriter cw) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)D", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, getAccessor, "(L" + declaringClass + ";)D");
				mv.visitInsn(Opcodes.DRETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;D)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitVarInsn(Opcodes.DLOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, setAccessor, "(L" + declaringClass + ";D)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(3, 4);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}
	
	public static byte[] getObjectField(final String declaringClass, final String getAccessor, final String setAccessor){

		final String[] interfaces = new String[]{ Type.getInternalName( ObjectField.class)}; 

		return getField( declaringClass, new MethodBuilder(){

			public void createGetMethod(ClassWriter cw) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, getAccessor, "(L" + declaringClass + ";)Ljava/lang/Object;");
				mv.visitInsn(Opcodes.ARETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, declaringClass);
				mv.visitVarInsn(Opcodes.ALOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, declaringClass, setAccessor, "(L" + declaringClass + ";Ljava/lang/Object;)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(3, 4);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}

	private static int index = 0;
	private static synchronized byte[] getField(String declaringClass, MethodBuilder builder)
	{
		String className = declaringClass + "$DeuceAccess" + (index++);

		String classInternalName = className.replace('.', '/'); // build internal name for ASM

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, classInternalName,
				null, "java/lang/Object", builder.getInterfaces());

		createConstructor(cw);
		builder.createGetMethod(cw);
		builder.createSetMethod(cw);
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void createConstructor(ClassVisitor cw) 
	{
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}


}
