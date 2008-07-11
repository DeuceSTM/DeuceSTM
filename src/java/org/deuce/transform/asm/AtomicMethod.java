package org.deuce.transform.asm;

import org.deuce.objectweb.asm.AnnotationVisitor;
import org.deuce.objectweb.asm.Attribute;
import org.deuce.objectweb.asm.Label;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.transaction.AbstractContext;
import org.deuce.transform.asm.type.TypeCodeResolver;
import org.deuce.transform.asm.type.TypeCodeResolverFactory;

public class AtomicMethod extends MethodAdapter implements Opcodes{

	private int retries = 7; // TODO set default
	private final String className;
	private final String methodName;

	final private TypeCodeResolver returnReolver;
	final private TypeCodeResolver[] argumentReolvers;
	private final boolean isStatic;
	final private int variablesSize;
	private final String newDescriptor; 

	public AtomicMethod(MethodVisitor mv, String className, String methodName,
			String descriptor, String newDescriptor, boolean isStatic) {
		super(mv);
		this.className = className;
		this.methodName = methodName;
		this.newDescriptor = newDescriptor;
		this.isStatic = isStatic;

		Type returnType = Type.getReturnType(descriptor);
		Type[] argumentTypes = Type.getArgumentTypes(descriptor);

		returnReolver = TypeCodeResolverFactory.getReolver(returnType);
		argumentReolvers = new TypeCodeResolver[ argumentTypes.length];
		for( int i=0; i< argumentTypes.length ; ++i) {
			argumentReolvers[ i] = TypeCodeResolverFactory.getReolver( argumentTypes[ i]);
		}
		variablesSize = variablesSize( argumentReolvers, isStatic);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return super.visitAnnotation(desc, visible);
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return super.visitAnnotationDefault();
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		return super.visitParameterAnnotation(parameter, desc, visible);
	}

	@Override
	public void visitAttribute(Attribute attr) {
		super.visitAttribute(attr);
	}

	@Override
	public void visitCode() {

		final int indexIndex = variablesSize; // i
		final int contextIndex = indexIndex + 1; // context
		final int resultIndex = returnReolver == null ? contextIndex : contextIndex + 1;
		final int throwableIndex = resultIndex + (returnReolver == null ? 0 : returnReolver.extendLocals()) + 1;

		Label l0 = new Label();
		Label l1 = new Label();
		mv.visitTryCatchBlock(l0, l1, l1, null); // try{

		mv.visitIntInsn(SIPUSH, retries); // for( int i =retries; ; ...) 
		mv.visitVarInsn(ISTORE, indexIndex);
		Label l2 = new Label();
		mv.visitJumpInsn(GOTO, l2);

		Label l3 = getContext(contextIndex); // AbstractContext context = AbstractContext.getInstance();

		if( returnReolver != null) { // result = null;
			mv.visitInsn(returnReolver.nullValueCode());
			mv.visitVarInsn(returnReolver.storeCode(), resultIndex);
		}

		// -------------- result = foo( context, ...)  --------------- 

		mv.visitLabel(l0);
		if( !isStatic) // load this id not static
			mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, contextIndex); // load the context

		// load the rest of the arguments
		int local = isStatic ? 0 : 1;
		for( int i=0 ; i < argumentReolvers.length ; ++i) { 
			mv.visitVarInsn(argumentReolvers[i].loadCode(), local);
			local += argumentReolvers[i].extendLocals();
		}
		if( isStatic)
			mv.visitMethodInsn(INVOKESTATIC, className, methodName, newDescriptor); // ... = foo( ...
		else
			mv.visitMethodInsn(INVOKEVIRTUAL, className, methodName, newDescriptor); // ... = foo( ...

		if( returnReolver != null) {
			mv.visitVarInsn(returnReolver.storeCode(), resultIndex); // result = ...
		}

		Label l4 = new Label();
		mv.visitJumpInsn(GOTO, l4);
		mv.visitLabel(l1);
		mv.visitVarInsn(ASTORE, throwableIndex); // store the throwable
		mv.visitVarInsn(ALOAD, contextIndex); // context.commit()
		mv.visitMethodInsn(INVOKEVIRTUAL, "org/deuce/transaction/AbstractContext", "commit", "()Z");
		Label l5 = new Label();
		mv.visitJumpInsn(IFNE, l5); //if( !context.commit())
		Label l6 = new Label();
		mv.visitJumpInsn(GOTO, l6); // continue;
		mv.visitLabel(l5);
		mv.visitVarInsn(ALOAD, throwableIndex); // load the throwable for re-throw
		mv.visitInsn(ATHROW);

		mv.visitLabel(l4);
		mv.visitVarInsn(ALOAD, contextIndex); // context.commit()
		mv.visitMethodInsn(INVOKEVIRTUAL, "org/deuce/transaction/AbstractContext", "commit", "()Z");

		Label l7 = new Label();
		mv.visitJumpInsn(IFNE, l7); //if( !context.commit())
		mv.visitJumpInsn(GOTO, l6);  // continue;

		mv.visitLabel(l7);
		if( returnReolver == null) {
			mv.visitInsn( RETURN); // return;
		}
		else {
			mv.visitVarInsn(returnReolver.loadCode(), resultIndex); // return result;
			mv.visitInsn(returnReolver.returnCode());
		}

		mv.visitLabel(l6);
		mv.visitIincInsn(indexIndex, -1); // for( ... ; ... ; --i)

		// for( ... ; i>0 ...
		mv.visitLabel(l2);
		mv.visitVarInsn(ILOAD, indexIndex);
		mv.visitJumpInsn(IFGT, l3);

		// throw new TransactionException("Failed to commit ...");
		throwTransactionException();

		mv.visitMaxs(5 + variablesSize, throwableIndex + 1);
		mv.visitEnd();
	}

	private Label getContext(final int contextIndex) {
		Label l3 = new Label();
		mv.visitLabel(l3); // AbstractContext context = AbstractContext.getInstance();
		mv.visitMethodInsn(INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME, "getInstance", "()Lorg/deuce/transaction/AbstractContext;");
		mv.visitVarInsn(ASTORE, contextIndex);
		return l3;
	}

	private void throwTransactionException() {
		mv.visitTypeInsn(NEW, "org/deuce/transaction/TransactionException");
		mv.visitInsn(DUP);
		mv.visitLdcInsn("Failed to commit the transaction in the defined retries.");
		mv.visitMethodInsn(INVOKESPECIAL, "org/deuce/transaction/TransactionException", "<init>", "(Ljava/lang/String;)V");
		mv.visitInsn(ATHROW);
	}

	@Override
	public void visitFrame(int type, int local, Object[] local2, int stack, Object[] stack2) {
	}

	@Override
	public void visitIincInsn(int var, int increment) {
	}

	@Override
	public void visitInsn(int opcode) {
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
	}

	@Override
	public void visitLabel(Label label) {
	}

	@Override
	public void visitEnd() {
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
	}


	@Override
	public void visitLdcInsn(Object cst) {
	}

	@Override
	public void visitLineNumber(int line, Label start) {
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start,
			Label end, int index) {
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	private int variablesSize( TypeCodeResolver[] types, boolean isStatic) {
		int i = isStatic ? 0 : 1;
		for( TypeCodeResolver type : types) {
			++i;
			i += type.extendLocals();
		}
		return i;
	}
}
