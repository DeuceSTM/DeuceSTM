package org.deuce.reflection;

import java.util.WeakHashMap;

import org.deuce.objectweb.asm.ClassVisitor;
import org.deuce.objectweb.asm.ClassWriter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.transform.asm.Loader;

/**
 * Creates ASM created classes that represents that provides fast fields
 * "reflection" mechanism.
 */
public class ASMFieldFactory {
	
	
	private static long index = 0;
	final private static WeakHashMap<ClassLoader, Loader> loaderMap = new WeakHashMap<ClassLoader, Loader>(); 
	
	
	private interface MethodBuilder {
		void createGetMethod(ClassWriter cw, String owener);

		void createSetMethod(ClassWriter cw, String owener);

		String[] getInterfaces();
	}

	public static BooleanField getBooleanField(final Class declaringClass, final String getAccessor,
			final String setAccessor) {

		final String[] interfaces = new String[] { Type.getInternalName(BooleanField.class) };

		return (BooleanField) createFieldAccessor(declaringClass, new MethodBuilder() {

			public void createGetMethod(ClassWriter cw, String owener) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)Z", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, getAccessor, "(L" + owener + ";)Z");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw, String owener) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;Z)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitVarInsn(Opcodes.ILOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, setAccessor, "(L" + owener + ";Z)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}

	public static ByteField getByteField(final Class declaringClass, final String getAccessor,
			final String setAccessor) {

		final String[] interfaces = new String[] { Type.getInternalName(ByteField.class) };

		return (ByteField) createFieldAccessor(declaringClass, new MethodBuilder() {

			public void createGetMethod(ClassWriter cw, String owener) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)B", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, getAccessor, "(L" + owener + ";)B");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw, String owener) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;B)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitVarInsn(Opcodes.ILOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, setAccessor, "(L" + owener + ";B)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}

	public static CharField getCharField(final Class declaringClass, final String getAccessor, final String setAccessor) {

		final String[] interfaces = new String[] { Type.getInternalName(CharField.class) };

		return (CharField) createFieldAccessor(declaringClass, new MethodBuilder() {

			public void createGetMethod(ClassWriter cw, String owener) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)C", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, getAccessor, "(L" + owener + ";)C");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw, String owener) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;C)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitVarInsn(Opcodes.ILOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, setAccessor, "(L" + owener + ";C)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}

	public static ShortField getShortField(final Class declaringClass, final String getAccessor,
			final String setAccessor) {

		final String[] interfaces = new String[] { Type.getInternalName(ShortField.class) };

		return (ShortField) createFieldAccessor(declaringClass, new MethodBuilder() {

			public void createGetMethod(ClassWriter cw, String owener) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)S", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, getAccessor, "(L" + owener + ";)S");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw, String owener) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;S)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitVarInsn(Opcodes.ILOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, setAccessor, "(L" + owener + ";S)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}

	public static IntField getIntField(final Class declaringClass, final String getAccessor, final String setAccessor) {

		final String[] interfaces = new String[] { Type.getInternalName(IntField.class) };

		return (IntField) createFieldAccessor(declaringClass, new MethodBuilder() {

			public void createGetMethod(ClassWriter cw, String owener) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)I", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, getAccessor, "(L" + owener + ";)I");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw, String owener) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;I)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitVarInsn(Opcodes.ILOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, setAccessor, "(L" + owener + ";I)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}

	public static LongField getLongField(final Class declaringClass, final String getAccessor, final String setAccessor) {

		final String[] interfaces = new String[] { Type.getInternalName(LongField.class) };

		return (LongField) createFieldAccessor(declaringClass, new MethodBuilder() {

			public void createGetMethod(ClassWriter cw, String owener) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)J", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, getAccessor, "(L" + owener + ";)J");
				mv.visitInsn(Opcodes.LRETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw, String owener) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;J)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitVarInsn(Opcodes.LLOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, setAccessor, "(L" + owener + ";J)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(3, 4);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}

	public static FloatField getFloatField(final Class declaringClass, final String getAccessor,
			final String setAccessor) {

		final String[] interfaces = new String[] { Type.getInternalName(FloatField.class) };

		return (FloatField) createFieldAccessor(declaringClass, new MethodBuilder() {

			public void createGetMethod(ClassWriter cw, String owener) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)F", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, getAccessor, "(L" + owener + ";)F");
				mv.visitInsn(Opcodes.FRETURN);
				mv.visitMaxs(1, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw, String owener) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;F)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitVarInsn(Opcodes.FLOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, setAccessor, "(L" + owener + ";F)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(2, 3);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}

	public static DoubleField getDoubleField(final Class declaringClass, final String getAccessor,
			final String setAccessor) {

		final String[] interfaces = new String[] { Type.getInternalName(DoubleField.class) };

		return (DoubleField) createFieldAccessor(declaringClass, new MethodBuilder() {

			public void createGetMethod(ClassWriter cw, String owener) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)D", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, getAccessor, "(L" + owener + ";)D");
				mv.visitInsn(Opcodes.DRETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw, String owener) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;D)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitVarInsn(Opcodes.DLOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, setAccessor, "(L" + owener + ";D)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(3, 4);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}

	public static ObjectField getObjectField(final Class declaringClass, final String getAccessor,
			final String setAccessor) {

		final String[] interfaces = new String[] { Type.getInternalName(ObjectField.class) };

		return (ObjectField) createFieldAccessor(declaringClass, new MethodBuilder() {

			public void createGetMethod(ClassWriter cw, String owener) {

				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;",
						null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, getAccessor, "(L" + owener
						+ ";)Ljava/lang/Object;");
				mv.visitInsn(Opcodes.ARETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();

			}

			public void createSetMethod(ClassWriter cw, String owener) {
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V",
						null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, owener);
				mv.visitVarInsn(Opcodes.ALOAD, 2);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, owener, setAccessor, "(L" + owener
						+ ";Ljava/lang/Object;)V");
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(3, 4);
				mv.visitEnd();

			}

			public String[] getInterfaces() {
				return interfaces;
			}
		});
	}

	

	private static synchronized Object createFieldAccessor(Class declaringClass, MethodBuilder builder) {
		
		String owner = Type.getInternalName(declaringClass);
		
		String className = "org/deuce/DeuceClass" + (index++);

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", builder.getInterfaces());
		
		createConstructor(cw);
		builder.createGetMethod(cw,owner);
		builder.createSetMethod(cw,owner);
		cw.visitEnd();

		ClassLoader classLoader = declaringClass.getClassLoader();
		Loader loader = loaderMap.get(classLoader);
		if( loader == null){
			loader = new Loader(classLoader);
			loaderMap.put(classLoader, loader);
		}
		
		Class defineClass = loader.defineClass(className.replace('/', '.'), cw.toByteArray());
		try {
			return defineClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void createConstructor(ClassVisitor cw) {
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	//	
	// Loader loader = classLoadersHolder.get(classLoader);
	// if( classLoader == null){
	// loader = new Loader(loader);
	// classLoadersHolder.put(class loader);
	// }

}
