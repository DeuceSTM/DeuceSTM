package org.deuce.transform.inplacemetadata;

import java.util.List;

import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.objectweb.asm.commons.AnalyzerAdapter;
import org.deuce.objectweb.asm.commons.Method;

/**
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
/*
 * TODO Class fields should not be instrumented inside the constructor We need
 *      to implement this optimization!
 *      I believe that implementing this optimization will fix the following 
 *      issue.
 *      
 *      Consider the following class, in bytecode:
 *        public class asmconstructortest.Test2 extends java.lang.Object {
 *        
 *        public int property;
 *        
 *        public asmconstructortest.Test2();
 *          Code:
 *          0:   aload_0
 *          1:   iconst_1
 *          2:   putfield        #10; //Field property:I
 *          5:   aload_0
 *          6:   invokespecial   #12; //Method java/lang/Object."<init>":()V
 *          9:   return
 *        }
 *      While the putfield instruction before the call to the super's <init>
 *      is impossible to produce programming in Java, it is possible in 
 *      bytecode. And it executes.
 *      
 *      But it wouldn't run if instead of a putfield it was a getfield.
 *      The following error occurs (line 5 of Main2.java is "new Test2()"):
 *        
 *        Exception in thread "main" java.lang.VerifyError: 
 *          (class: asmconstructortest/Test2, method: <init> signature: ()V) 
 *          Expecting to find object/array on stack at 
 *          asmconstructortest.Main2.main(Main2.java:5)
 *          
 *      This is probably because self is not initialized yet.
 *      
 *      The transaction-aware duplication of Test2(), Test2(IContext), will 
 *      inject before the aforementioned putfield a getfield to the 
 *      corresponding metadata. That will cause a similar exception to the 
 *      above to be thrown.
 */
public class ConstructorMethodTransformer extends AnalyzerAdapter {

	final static public String CLASS_BASE = "__CLASS_BASE__";

	private final List<Field> fields;
	private final String fieldsHolderName;
	private boolean callsOtherCtor;
	private final String className;

	public ConstructorMethodTransformer(MethodVisitor mv, List<Field> fields, String className, int access,
			String name, String desc, String fieldsHolderName) {
		super(className, access, name, desc, mv);
		this.fields = fields;
		this.fieldsHolderName = fieldsHolderName;
		this.callsOtherCtor = false;
		this.className = className;
	}

	@Override
	public void visitCode() {
		// ((MethodTransformer)mv).disableDuplicateInstrumentation(true);
		super.visitCode();
	}

	private void addField(Field field, MethodVisitor method) {
		/*
		 * Example: class cA { int mA; }
		 * 
		 * (...)
		 * 
		 * cA inst = new cA(); <- during init
		 * 
		 * Concept code: this.mA__ADDRESS__ = new TxField(inst,
		 * cAFieldHolder.__STATIC__mA__ADDRESS__);
		 */
		// stack: ... =>
		method.visitVarInsn(Opcodes.ALOAD, 0);
		// stack: ..., Object (this) =>
		method.visitTypeInsn(Opcodes.NEW, field.getType().getInternalName());
		// stack: ..., Object (this), TxField =>
		method.visitInsn(Opcodes.DUP);
		// stack: ..., Object (this), TxField, TxField =>
		method.visitVarInsn(Opcodes.ALOAD, 0);
		// stack: ..., Object (this), TxField, TxField, Object (this) =>
		method.visitFieldInsn(Opcodes.GETSTATIC, fieldsHolderName, "__STATIC__" + field.getFieldNameAddress(),
				Type.LONG_TYPE.getDescriptor());
		// stack: ..., Object (this), TxField, TxField, Object (this), =>
		// => long (__STATIC__*__ADDRESS__) =>
		method.visitMethodInsn(Opcodes.INVOKESPECIAL, field.getType().getInternalName(), "<init>",
				"(Ljava/lang/Object;J)V");
		// stack: ..., Object (this), TxField =>
		method.visitFieldInsn(Opcodes.PUTFIELD, fieldsHolderName, field.getFieldNameAddress(), field.getType()
				.getDescriptor());
		// stack: ... =>
	}

	@Override
	public void visitInsn(int opcode) {
		if (opcode == Opcodes.RETURN && !callsOtherCtor) {
			((MethodTransformer) mv).disableDuplicateInstrumentation(true);
			((MethodTransformer) mv).disableMethodInstrumentation(true);
			if (fields.size() > 0) {
				for (Field field : fields) {
					if ((field.getAccess() & Opcodes.ACC_STATIC) == 0) {
						addField(field, this.mv);
					}
				}
			}
			((MethodTransformer) mv).disableMethodInstrumentation(false);
			((MethodTransformer) mv).disableDuplicateInstrumentation(false);
		}

		super.visitInsn(opcode);
	}

	@Override
	public void visitEnd() {
		// ((MethodTransformer)mv).disableDuplicateInstrumentation(false);
		super.visitEnd();
	}

	@Override
	public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
		if (opcode == Opcodes.INVOKESPECIAL && owner.equals(className) && name.equals("<init>")) {
			// The method is created merely to know the #params
			int nParams = new Method(name, desc).getArgumentTypes().length;

			// With that number, we go down the stack #params and check what's
			// there
			Object stackObj = stack.get(stack.size() - nParams - 1);

			// Is it <this>, uninitialized?
			if (stackObj instanceof Integer && ((Integer) stackObj) == Opcodes.UNINITIALIZED_THIS) {
				callsOtherCtor = true;
			}
		}
		super.visitMethodInsn(opcode, owner, name, desc);
	}

}
