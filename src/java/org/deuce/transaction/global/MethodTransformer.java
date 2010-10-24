package org.deuce.transaction.global;

import org.deuce.Atomic;
import org.deuce.objectweb.asm.AnnotationVisitor;
import org.deuce.objectweb.asm.Label;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Type;
import static org.deuce.objectweb.asm.Opcodes.*;

public class MethodTransformer extends MethodAdapter{

	final static private String ATOMIC_METHOD_POST = "__atomic__"; 

	private final ClassTransformer classTransformer;
	private final int access;
	private final String name;
	private final String desc;
	private final String signature;
	private final String[] exceptions;
	
	private MethodVisitor atomicVisitor = null; // visitor of the wrapping method if there's one 

	public MethodTransformer( MethodVisitor visitor, int access, String name, String desc,
			String signature, String[] exceptions, ClassTransformer classTransformer) {
		super(visitor);
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
		this.classTransformer = classTransformer;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		boolean atomic = Type.getDescriptor(Atomic.class).equals(desc);
		if( atomic) { 
			atomicVisitor = mv;
			
			// FIXME handle native methods
			
			buildAtomic();

			// replace with the logic method
			mv = classTransformer.createMethod(
					(access & ~ACC_PUBLIC & ~ACC_PROTECTED) | ACC_PRIVATE,
					name + ATOMIC_METHOD_POST, this.desc, this.signature, this.exceptions);
		}
		
		if( atomicVisitor != null){
			return atomicVisitor.visitAnnotation(desc, visible); // annotate the wrapping method
		}
		
		return super.visitAnnotation(desc, visible); 
	}

	private void buildAtomic() {

		final Type[] types = Type.getArgumentTypes(desc);
		final boolean isNonStatic = (access & ACC_STATIC) == 0;
		final int lockLocal = locals(types, isNonStatic);

		atomicVisitor.visitCode();

		// enter synchronized block 
		Label l0 = new Label();
		Label l1 = new Label();
		Label l2 = new Label();
		atomicVisitor.visitTryCatchBlock(l0, l1, l2, null);
		Label l3 = new Label();
		atomicVisitor.visitTryCatchBlock(l2, l3, l2, null);
		atomicVisitor.visitFieldInsn(GETSTATIC, "org/deuce/transaction/global/Lock",
				"lock", "Ljava/lang/Object;");
		atomicVisitor.visitInsn(DUP);
		atomicVisitor.visitVarInsn(ASTORE, lockLocal);
		atomicVisitor.visitInsn(MONITORENTER);
		atomicVisitor.visitLabel(l0);

		callMethod( atomicVisitor, types, isNonStatic); // Delegates call

		// exit synchronized block
		atomicVisitor.visitVarInsn(ALOAD, lockLocal);
		atomicVisitor.visitInsn(MONITOREXIT);
		atomicVisitor.visitLabel(l1);

		returnMethod( atomicVisitor); // Returns result

		// exit synchronized block
		atomicVisitor.visitLabel(l2);
		atomicVisitor.visitVarInsn(ALOAD, lockLocal);
		atomicVisitor.visitInsn(MONITOREXIT);
		atomicVisitor.visitLabel(l3);
		atomicVisitor.visitInsn(ATHROW);

		atomicVisitor.visitMaxs(0, 0); // compute MAX is set 
		atomicVisitor.visitEnd();
	}

	private void callMethod( MethodVisitor methodVisitor, Type[] types, boolean isNonStatic) {

		int offset = 0;
		if( isNonStatic){
			methodVisitor.visitVarInsn(ALOAD, 0); // load this
			offset = 1;
		}

		for( int i=0 ; i<types.length ;++i) {
			switch( types[i].getSort()) {
			case Type.BOOLEAN:
			case Type.BYTE:
			case Type.CHAR:
			case Type.SHORT:
			case Type.INT:
				methodVisitor.visitVarInsn(ILOAD, i + offset);
				break;
			case Type.LONG:
				methodVisitor.visitVarInsn(LLOAD, i + offset);
				break;
			case Type.FLOAT:
				methodVisitor.visitVarInsn(FLOAD, i + offset);
				break;
			case Type.DOUBLE:
				methodVisitor.visitVarInsn(DLOAD, i + offset);
				break;
			default:
				methodVisitor.visitVarInsn(ALOAD, i + offset);
			break;
			}
		}
		if( isNonStatic) {
			methodVisitor.visitMethodInsn(INVOKESPECIAL, classTransformer.getClassName(),
					name + ATOMIC_METHOD_POST, desc);
		}
		else {
			methodVisitor.visitMethodInsn(INVOKESTATIC, classTransformer.getClassName(),
					name + ATOMIC_METHOD_POST, desc);
		}
	}

	private void returnMethod( MethodVisitor methodVisitor) {
		Type type = Type.getReturnType(desc);
		switch( type.getSort()) {
		case Type.VOID:
			mv.visitInsn(RETURN);
			break;
		case Type.BOOLEAN:
		case Type.BYTE:
		case Type.CHAR:
		case Type.SHORT:
		case Type.INT:
			mv.visitInsn(IRETURN);
			break;
		case Type.LONG:
			mv.visitInsn(LRETURN);
			break;
		case Type.FLOAT:
			mv.visitInsn(FRETURN);
			break;
		case Type.DOUBLE:
			mv.visitInsn(DRETURN);
			break;
		default:
			mv.visitInsn(ARETURN);
		break;
		}
	}

	private int locals( Type[] types, boolean isNonStatic) {

		int locals = 0;
		if( isNonStatic)
			locals = 1;

		for( int i=0 ; i<types.length ;++i) {
			switch( types[i].getSort()) {
			case Type.LONG:
			case Type.DOUBLE:
				locals += 2;
				break;
			default:
				++locals;
			}
		}

		return locals;
	}
}
