package org.deuce.transform.asm;

import java.util.LinkedList;

import org.deuce.objectweb.asm.AnnotationVisitor;
import org.deuce.objectweb.asm.FieldVisitor;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.objectweb.asm.commons.Method;
import org.deuce.transaction.Context;
import org.deuce.transform.Exclude;
import org.deuce.transform.util.Util;

@Exclude
public class ClassTransformer extends ByteCodeVisitor{

	private boolean exclude = false;
	private boolean visitclinit = false;
	final private LinkedList<Field> fields = new LinkedList<Field>();
	private Field staticField = null;

	public ClassTransformer( String className){
		super( className);
	}
	
	/**
	 * Checks if the class is marked as {@link Exclude @Exclude}
	 */
	@Override
	public AnnotationVisitor visitAnnotation( String desc, boolean visible) {
		exclude = exclude ? exclude : Type.getDescriptor(Exclude.class).equals(desc);
		return super.visitAnnotation(desc, visible);
	}

	/**
	 * Creates a new static filed for each existing field.
	 * The field will be statically initialized to hold the field address.   
	 */
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature,
			Object value) {

		FieldVisitor fieldVisitor = super.visitField(access, name, desc, signature, value);
		if( exclude)
			return fieldVisitor;

		String addressFieldName = Util.getAddressField( name);
		int fieldAccess = Opcodes.ACC_FINAL | Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;

		Field field = new Field(name, addressFieldName);
		fields.add( field);

		if( staticField == null && (access &  Opcodes.ACC_STATIC) != 0)
			staticField = field;

		super.visitField( fieldAccess, addressFieldName, Type.LONG_TYPE.getDescriptor(), null, null);
		return fieldVisitor;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature,
			String[] exceptions) {

		MethodVisitor originalMethod =  super.visitMethod(access, name, desc, signature, exceptions);
		if( exclude)
			return originalMethod;

		if( name.equals("<clinit>")) {
			visitclinit = true;

			int fieldAccess = Opcodes.ACC_FINAL | Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
			super.visitField( fieldAccess, StaticMethodTransformer.CLASS_BASE,
					Type.getDescriptor(Object.class), null, null);

			return new StaticMethodTransformer( originalMethod, fields, className, staticField);
		}
		else
		{
			Method newMethod = createNewMethod(name, desc);

			MethodVisitor copyMethod =  super.visitMethod(access, name, newMethod.getDescriptor(),
					signature, exceptions);
			
			return new MethodTransformer( originalMethod, copyMethod, className,
					access, name, desc, newMethod);
			
		}
	}

	@Override
	public void visitEnd() {
		if( !exclude & !visitclinit) {
			visitclinit = true;
			MethodVisitor method = visitMethod(8, "<clinit>", "()V", null, null);
			method.visitCode();
			method.visitInsn(Opcodes.RETURN);
			method.visitMaxs(100, 100); // TODO set the right value
			method.visitEnd();
		}
		super.visitEnd();
	}
	
	public static Method createNewMethod(String name, String desc) {
		Method method = new Method( name, desc);
		Type[] arguments = method.getArgumentTypes();

		Type[] newArguments = new Type[ arguments.length + 1];
		System.arraycopy( arguments, 0, newArguments, 0, arguments.length);
		newArguments[newArguments.length - 1] = Context.CONTEXT_TYPE; // add as a constant

		Method newMethod = new Method( name, method.getReturnType(), newArguments);
		return newMethod;
	}
}
