package org.deuce.transform.asm;

import org.deuce.objectweb.asm.Label;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.transaction.AbstractContext;
import org.deuce.transform.util.Util;

public class DuplicateMethod extends MethodAdapter{

	public enum NextLocal { THIS, CONTEXT, OTHER}

	final static public String LOCAL_VARIBALE_NAME = "__transactionContext__";
	final static public String CONTEXT_DESCRIPTOR = Type.getDescriptor( AbstractContext.class);

	private NextLocal nextLocal;
	private Label firstLabel;
	private Label lastLabel;
	private final boolean isStatic;


	public DuplicateMethod(MethodVisitor mv, boolean isStatic) {
		super(mv);
		this.isStatic = isStatic;
		this.nextLocal = isStatic ? NextLocal.CONTEXT : NextLocal.THIS;
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		switch( opcode) {
		case Opcodes.GETFIELD:
			super.visitInsn(Opcodes.DUP); 
			super.visitFieldInsn(opcode, owner, name, desc);
			super.visitFieldInsn( Opcodes.GETSTATIC, owner, Util.getAddressField(name) , "J");
			super.visitVarInsn(Opcodes.ALOAD, isStatic ? 0 : 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME,
					AbstractContext.READ_METHOD_NAME, AbstractContext.getReadMethodDesc(desc));
			
			Type type = Type.getType(desc);
			if( type.getSort() > Type.DOUBLE)
				super.visitTypeInsn( Opcodes.CHECKCAST, Type.getType(desc).getInternalName());
			break;
		case Opcodes.PUTFIELD:
			super.visitFieldInsn( Opcodes.GETSTATIC, owner, Util.getAddressField(name) , "J");
			super.visitVarInsn(Opcodes.ALOAD, isStatic ? 0 : 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME,
					AbstractContext.WRITE_METHOD_NAME, AbstractContext.getWriteMethodDesc(desc));
			break;
		case Opcodes.GETSTATIC: // TODO check support for static fields
			super.visitFieldInsn(Opcodes.GETSTATIC, owner, 
					StaticMethodTransformer.CLASS_BASE, "Ljava/lang/Object;");
			super.visitFieldInsn(opcode, owner, name, desc);
			super.visitFieldInsn( Opcodes.GETSTATIC, owner, Util.getAddressField(name) , "J");
			super.visitVarInsn(Opcodes.ALOAD, isStatic ? 0 : 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME,
					AbstractContext.READ_METHOD_NAME, AbstractContext.getReadMethodDesc(desc));
			
			type = Type.getType(desc);
			if( type.getSort() > Type.DOUBLE)
				super.visitTypeInsn( Opcodes.CHECKCAST, Type.getType(desc).getInternalName());
			break;
		case Opcodes.PUTSTATIC:
			super.visitFieldInsn(Opcodes.GETSTATIC, owner, 
					StaticMethodTransformer.CLASS_BASE, "Ljava/lang/Object;");
			super.visitFieldInsn( Opcodes.GETSTATIC, owner, Util.getAddressField(name) , "J");
			super.visitVarInsn(Opcodes.ALOAD, isStatic ? 0 : 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME,
					AbstractContext.STATIC_WRITE_METHOD_NAME, AbstractContext.getStaticWriteMethodDesc(desc));
			break;
		default:
			super.visitFieldInsn(opcode, owner, name, desc);
		}
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		super.visitIincInsn( var == 0 ? var : var + 1, increment); // increase index due to context
	}

	@Override
	public void visitLabel(Label label) {
		if( firstLabel == null)
			firstLabel = label;
		lastLabel = label;
		super.visitLabel(label);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start,
			Label end, int index) {
		switch( nextLocal) {
		case THIS:
			super.visitLocalVariable(name, desc, signature, start, end, index); // non static method has this
		case CONTEXT:
			super.visitLocalVariable(LOCAL_VARIBALE_NAME, CONTEXT_DESCRIPTOR, null,
					firstLabel, lastLabel, isStatic ? 0 : 1);
			nextLocal = NextLocal.OTHER;
			break;
		case OTHER:
			super.visitLocalVariable(name, desc, signature, start, end, index + 1);
		}
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(maxStack + 3, maxLocals + 1);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {	
		super.visitVarInsn(opcode, (var == 0 && !isStatic) ? var : var + 1); // in non-static methods the index 0 is for this. 
	}
}
