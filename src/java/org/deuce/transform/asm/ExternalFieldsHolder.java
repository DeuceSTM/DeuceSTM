package org.deuce.transform.asm;

import org.deuce.objectweb.asm.ClassWriter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;

/**
 * Creates a class to hold the fields address, used by the offline instrumentation. 
 * @author guy
 * @since 1.1
 */
public class ExternalFieldsHolder implements FieldsHolder {

	final static private String FIELDS_HOLDER = "DeuceFieldsHolder";
	
	final private ClassWriter classWriter;
	final private String className;
	final private MethodVisitor staticMethod;
	
	public ExternalFieldsHolder(String className){
		this.className = getFieldsHolderName(className);
		
		classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classWriter.visit(Opcodes.V1_6, Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, 
				this.className, null, "java/lang/Object", null);
		
		staticMethod = classWriter.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
		staticMethod.visitCode();
	}
	
	public ClassByteCode getClassByteCode(){
		return new ClassByteCode(className, classWriter.toByteArray());
	}

	@Override
	public void addField(int fieldAccess, String addressFieldName, String desc,
			Object value) {
		classWriter.visitField(fieldAccess, addressFieldName, desc, null, value);
	}
	
	@Override
	public void close(){
		staticMethod.visitInsn(Opcodes.RETURN);
		staticMethod.visitMaxs(1, 1); // Dummy call 
		staticMethod.visitEnd();
		classWriter.visitEnd();
	}
	
	@Override
	public MethodVisitor getStaticMethodVisitor(){
		return staticMethod;
	}
	
	@Override
	public String getFieldsHolderName(String owner){
		return owner +  FIELDS_HOLDER;
	}
}
