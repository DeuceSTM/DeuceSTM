package org.deuce.transform.asm;

import java.lang.annotation.Annotation;
import java.util.LinkedList;

import org.deuce.objectweb.asm.AnnotationVisitor;
import org.deuce.objectweb.asm.FieldVisitor;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.objectweb.asm.commons.JSRInlinerAdapter;
import org.deuce.objectweb.asm.commons.Method;
import org.deuce.transaction.Context;
import org.deuce.transform.Exclude;
import org.deuce.transform.asm.method.MethodTransformer;
import org.deuce.transform.asm.method.StaticMethodTransformer;
import org.deuce.transform.util.Util;

@Exclude
public class ClassTransformer extends ByteCodeVisitor implements FieldsHolder{

	private boolean exclude = false;
	private boolean visitclinit = false;
	final private LinkedList<Field> fields = new LinkedList<Field>();

	final static private String EXCLUDE_DESC = Type.getDescriptor(Exclude.class);
	final static private String ANNOTATION_NAME = Type.getInternalName(Annotation.class);
	private boolean isInterface;
	private MethodVisitor staticMethod;
	
	private final FieldsHolder fieldsHolder;

	public ClassTransformer( String className, FieldsHolder fieldsHolder){
		super( className);
		this.fieldsHolder = fieldsHolder == null ? this : fieldsHolder;
	}

	@Override
	public void visit(final int version, final int access, final String name,
			final String signature, final String superName, final String[] interfaces) {
		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
		for(String inter : interfaces){
			if( inter.equals(ANNOTATION_NAME)){
				exclude = true;
				break;
			}

		}
		super.visit(version, access, name, signature, superName, interfaces);
	}

	/**
	 * Checks if the class is marked as {@link Exclude @Exclude}
	 */
	@Override
	public AnnotationVisitor visitAnnotation( String desc, boolean visible) {
		exclude = exclude ? exclude : EXCLUDE_DESC.equals(desc);
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
		
		// Define as constant
		int fieldAccess = Opcodes.ACC_FINAL | Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC;
		String addressFieldName = Util.getAddressField( name);
		
		final boolean include = (access & Opcodes.ACC_FINAL) == 0;
		if( include){ // include field if not final 
			Field field = new Field(name, addressFieldName);
			fields.add( field);

			fieldsHolder.addField( fieldAccess, addressFieldName, Type.LONG_TYPE.getDescriptor(), null);
		}else{
			// If this field is final mark with a negative address.
			fieldsHolder.addField( fieldAccess, addressFieldName, Type.LONG_TYPE.getDescriptor(), -1L);
		}
		
		return fieldVisitor;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature,
			String[] exceptions) {

		MethodVisitor originalMethod =  super.visitMethod(access, name, desc, signature, exceptions);
		if( exclude)
			return originalMethod;

		if( name.equals("<clinit>")) {
			staticMethod = originalMethod;
			visitclinit = true;

			if( isInterface){
				return originalMethod;
			}

			int fieldAccess = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC;
			fieldsHolder.addField( fieldAccess, StaticMethodTransformer.CLASS_BASE,
					Type.getDescriptor(Object.class), null);
			
			MethodVisitor staticMethodVisitor = fieldsHolder.getStaticMethodVisitor();
			StaticMethodTransformer staticTransformer =  createStaticMethodTransformer( originalMethod, staticMethodVisitor);
			return new JSRInlinerAdapter(staticTransformer, access, name, desc, signature, exceptions);
		}
		Method newMethod = createNewMethod(name, desc);

		MethodVisitor copyMethod =  super.visitMethod(access | Opcodes.ACC_SYNTHETIC, name, newMethod.getDescriptor(),
				signature, exceptions);

		return new MethodTransformer( originalMethod, copyMethod, className,
				access, name, desc, newMethod, fieldsHolder);
	}

	@Override
	public void visitEnd() {
		//Didn't see any static method till now, so creates one.
		if(!exclude){
			super.visitAnnotation(EXCLUDE_DESC, false);
			if( !visitclinit && fields.size() > 0) {

				//TODO avoid creating new static method in case of external fields holder
				visitclinit = true;
				MethodVisitor method = visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
				method.visitCode();
				method.visitInsn(Opcodes.RETURN);
				method.visitMaxs(100, 100); // TODO set the right value
				method.visitEnd();

			}
		}
		super.visitEnd();
		fieldsHolder.close();
	}

	private StaticMethodTransformer createStaticMethodTransformer(MethodVisitor originalMethod, MethodVisitor staticMethod){
		return new StaticMethodTransformer( originalMethod, staticMethod, fields, 
				className, fieldsHolder.getFieldsHolderName(className));
	}
	
	public static Method createNewMethod(String name, String desc) {
		Method method = new Method( name, desc);
		Type[] arguments = method.getArgumentTypes();

		Type[] newArguments = new Type[ arguments.length + 1];
		System.arraycopy( arguments, 0, newArguments, 0, arguments.length);
		newArguments[newArguments.length - 1] = Context.CONTEXT_TYPE; // add as a constant

		return new Method( name, method.getReturnType(), newArguments);
	}
	
	@Override
	public void addField(int fieldAccess, String addressFieldName, String desc, Object value){
		super.visitField( fieldAccess, addressFieldName, desc, null, value);
	}
	
	@Override
	public void close(){
	}
	
	@Override
	public MethodVisitor getStaticMethodVisitor(){
		return staticMethod;
	}
	
	@Override
	public String getFieldsHolderName(String owner){
		return owner;
	}
}
