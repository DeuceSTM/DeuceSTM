package org.deuce.transform.inplacemetadata;

import org.deuce.objectweb.asm.Label;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;

public class MainMethodTransformer extends MethodAdapter {
	final static public String CLASS_BASE = "__CLASS_BASE__";

	public MainMethodTransformer(MethodVisitor mv) {
		super(mv);
	}

	@Override
	public void visitCode() {
		((MethodTransformer) mv).disableMethodInstrumentation(true);
		((MethodTransformer) mv).disableDuplicateInstrumentation(true);

		Label l1 = new Label();
		Label l2 = new Label();
		Label l3 = new Label();

		Type arrayType = ArrayUtil.getTxArrayType(Type.getType(String.class));

		// ... =>
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		// ..., arr =>
		mv.visitInsn(Opcodes.ARRAYLENGTH);
		// ..., len =>
		mv.visitTypeInsn(Opcodes.ANEWARRAY, arrayType.getInternalName());
		// ..., arr* =>
		mv.visitInsn(Opcodes.DUP);
		// ..., arr*, arr* =>
		mv.visitInsn(Opcodes.DUP);
		// ..., arr*, arr*, arr* =>
		mv.visitInsn(Opcodes.ARRAYLENGTH);
		// ..., arr*, arr*, i (len) =>
		mv.visitLabel(l3);
		// ..., arr*, arr*, i =>
		mv.visitInsn(Opcodes.DUP);
		// ..., arr*, arr*, i, i =>
		mv.visitJumpInsn(Opcodes.IFGT, l1);
		// ..., arr*, arr*, i =>
		mv.visitInsn(Opcodes.POP2);
		// ..., arr* =>
		mv.visitVarInsn(Opcodes.ASTORE, 0);
		// ... =>
		mv.visitJumpInsn(Opcodes.GOTO, l2);
		// ... =>
		mv.visitLabel(l1);
		// ..., arr*, arr*, i =>
		mv.visitInsn(Opcodes.ICONST_1);
		// ..., arr*, arr*, i, 1 =>
		mv.visitInsn(Opcodes.ISUB);
		// ..., arr*, arr*, i-1 =>
		mv.visitInsn(Opcodes.DUP_X1);
		// ..., arr*, i-1, arr*, i-1 =>
		mv.visitInsn(Opcodes.DUP);
		// ..., arr*, i-1, arr*, i-1, i-1 =>
		mv.visitTypeInsn(Opcodes.NEW, arrayType.getInternalName());
		// ..., arr*, i-1, arr*, i-1, i-1, obj =>
		mv.visitInsn(Opcodes.DUP_X1);
		// ..., arr*, i-1, arr*, i-1, obj, i-1, obj =>
		mv.visitInsn(Opcodes.DUP_X1);
		// ..., arr*, i-1, arr*, i-1, obj, obj, i-1, obj =>
		mv.visitInsn(Opcodes.POP);
		// ..., arr*, i-1, arr*, i-1, obj, obj, i-1 =>
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		// ..., arr*, i-1, arr*, i-1, obj, obj, i-1, arr =>
		mv.visitInsn(Opcodes.SWAP);
		// ..., arr*, i-1, arr*, i-1, obj, obj, arr, i-1 =>
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, arrayType.getInternalName(), "<init>",
				"([" + Type.getDescriptor(Object.class) + "I)V");
		// ..., arr*, i-1, arr*, i-1, obj =>
		mv.visitInsn(Opcodes.AASTORE);
		// ..., arr*, i-1 =>
		mv.visitInsn(Opcodes.SWAP);
		// ..., i-1, arr* =>
		mv.visitInsn(Opcodes.DUP_X1);
		// ..., arr*, i-1, arr* =>
		mv.visitInsn(Opcodes.SWAP);
		// ..., arr*, arr*, i-1 =>
		mv.visitJumpInsn(Opcodes.GOTO, l3);
		// ..., arr*, arr*, i-1 =>
		mv.visitLabel(l2);
		// ... =>

		((MethodTransformer) mv).disableMethodInstrumentation(false);
		((MethodTransformer) mv).disableDuplicateInstrumentation(false);
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}
}
