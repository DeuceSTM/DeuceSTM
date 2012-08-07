package org.deuce.transform.inplacemetadata;

import org.deuce.objectweb.asm.Label;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.transform.inplacemetadata.ClassTransformer.MultiArrayMethod;

/**
 * This class creates a static method that receives a multiarray as its first
 * parameter. This means the original multiarray can be accessed by ALOAD_0.
 * 
 * @author tvale
 */
public class MultiArrayMethodTransformer extends MethodAdapter {
	private MultiArrayMethod insnM;

	public MultiArrayMethodTransformer(MethodVisitor mv) {
		super(mv);
	}

	public void setMultiArrayMethod(MultiArrayMethod mim) {
		this.insnM = mim;
	}

	public void visitCode() {
		// ... =>
		initializeMatrixDimension(mv, 1, insnM.dims, insnM.totalDims, insnM.type);
		// ... =>
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		// ..., arr* =>
		mv.visitInsn(Opcodes.ARETURN);
	}

	/*
	 * This method will do something like this:
	 * 
	 * int[][][] arr;
	 * 
	 * TxArrObjectField_new[] tmp0 = new TxArrObjectField_new[arr.length]; 
	 * int i = tmp0.length; 
	 * while (i > 0) { 
	 * 		i--; 
	 * 		tmp0[i] = new TxArrObjectField_new(arr, i);
	 * 
	 * 		TxArrObjectField_new[] tmp1 = new TxArrObjectField_new[arr[i].length];
	 * 
	 * 		int j = tmp1.length; 
	 * 		while (j > 0) { 
	 * 			j--; 
	 * 			tmp1[j] = new TxArrObjectField_new(arr[i], j);
	 * 
	 * 			TxArrIntField_new[] tmp2 = new TxArrIntField_new[arr[i][j].length];
	 * 
	 * 			int k = tmp2.length; 
	 * 			while (k > 0) { 
	 * 				k--; 
	 * 				tmp2[k] = new TxArrIntField_new(arr[i][j], k); 
	 * 			} 
	 * 			tmp1[j].nextDim = tmp2; 
	 * 		}
	 * 		tmp0[i].nextDim = tmp1; 
	 * }
	 * 
	 * The local variables of this method, regarding the example, are as
	 * follows: 
	 * 		0: original multiarray; 
	 * 		1: IContext; 
	 * 		2: TxArrObjectField[] (first dimension) 
	 * 		3: int (first dimension for cycle counter variable) 
	 * 		4: TxArrObjectField[] (second dimension) 
	 * 		5: int (second dimension for cycle counter variable) 
	 * 		6: TxArrIntField[] (third and last dimension) 
	 * 		7: int (third and last dimension for cycle counter variable)
	 */
	private void initializeMatrixDimension(MethodVisitor mv, int currDim, int nDims, int totalDims, Type originalType) {
		int arr_idx = 0;
		int arrM_idx = currDim * 2;
		int i_idx = arrM_idx + 1;
		Label l1 = new Label();
		Label l2 = new Label();
		Label l3 = new Label();

		Type arrayType = currDim < totalDims ? ArrayUtil.getTxArrayType(Type.getDescriptor(Object.class)) : ArrayUtil
				.getTxArrayType(originalType.getDescriptor());

		String ctorParamDesc = null;
		if (currDim < totalDims) {
			// ([Ljava/lang/Object;ILjava/lang/Object;)V
			ctorParamDesc = "([" + Type.getDescriptor(Object.class) + Type.getDescriptor(int.class)
					+ Type.getDescriptor(Object.class) + ")" + Type.getDescriptor(void.class);
		}
		else {
			// ([Ljava/lang/Object;I)V
			// ([II)V
			// etc...
			ctorParamDesc = "([" + ArrayUtil.getArrayFromTxArray(arrayType).getDescriptor()
					+ Type.getDescriptor(int.class) + ")" + Type.getDescriptor(void.class);
		}

		// ... =>
		mv.visitVarInsn(Opcodes.ALOAD, arr_idx);
		// ..., arr =>
		for (int i = 1; i < currDim; i++) {
			// ..., arr =>
			mv.visitVarInsn(Opcodes.ILOAD, 2 * i + 1);
			// ..., arr, idx =>
			mv.visitInsn(Opcodes.AALOAD);
			// ..., arr =>
		}
		// ..., arr =>
		mv.visitInsn(Opcodes.ARRAYLENGTH);
		// ..., len =>
		mv.visitInsn(Opcodes.DUP);
		// ..., len, len =>
		mv.visitTypeInsn(Opcodes.ANEWARRAY, arrayType.getInternalName());
		// ..., len, arr* =>
		mv.visitVarInsn(Opcodes.ASTORE, arrM_idx);
		// ... i (len) =>
		mv.visitVarInsn(Opcodes.ISTORE, i_idx);
		// ... =>
		mv.visitLabel(l3);
		// ... =>
		mv.visitVarInsn(Opcodes.ILOAD, i_idx);
		// ..., i =>
		mv.visitJumpInsn(Opcodes.IFGT, l1);
		// ... =>
		mv.visitJumpInsn(Opcodes.GOTO, l2);
		// ... =>
		mv.visitLabel(l1);
		// ... =>
		mv.visitIincInsn(i_idx, -1);
		// ... =>
		mv.visitVarInsn(Opcodes.ALOAD, arrM_idx);
		// ..., arr* =>
		mv.visitVarInsn(Opcodes.ILOAD, i_idx);
		// ..., arr*, i =>
		mv.visitTypeInsn(Opcodes.NEW, arrayType.getInternalName());
		// ..., arr*, i, obj =>
		mv.visitInsn(Opcodes.DUP);
		// ..., arr*, i, obj, obj =>
		mv.visitVarInsn(Opcodes.ALOAD, arr_idx);
		// ..., arr*, i, obj, obj, arr =>
		for (int i = 1; i < currDim; i++) {
			// ..., arr*, i, obj, obj, arr =>
			mv.visitVarInsn(Opcodes.ILOAD, (i * 2) + 1);
			// ..., arr*, i, obj, obj, arr, i (from outer loop) =>
			mv.visitInsn(Opcodes.AALOAD);
			// ..., arr*, i, obj, obj, arr =>
		}
		// ..., arr*, i, obj, obj, arr =>
		mv.visitVarInsn(Opcodes.ILOAD, i_idx);
		// ..., arr*, i, obj, obj, arr, i =>
		if (currDim < totalDims)
			mv.visitInsn(Opcodes.ACONST_NULL);
		// ..., arr*, i, obj, obj, arr, i, (null) =>
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, arrayType.getInternalName(), "<init>", ctorParamDesc);
		// ..., arr*, i, obj =>
		mv.visitInsn(Opcodes.AASTORE);
		// ... =>
		if (currDim < nDims) {
			// ... =>
			initializeMatrixDimension(mv, currDim + 1, nDims, totalDims, originalType);
			// ... =>
			mv.visitVarInsn(Opcodes.ALOAD, arrM_idx);
			// ..., arr* =>
			mv.visitVarInsn(Opcodes.ILOAD, i_idx);
			// ..., arr*, i =>
			mv.visitInsn(Opcodes.AALOAD);
			// ..., obj =>
			mv.visitVarInsn(Opcodes.ALOAD, arrM_idx + 2);
			// ..., obj, arr* (from inner loop) =>
			mv.visitFieldInsn(Opcodes.PUTFIELD, arrayType.getInternalName(), "nextDim",
					Type.getDescriptor(Object.class));
			// ... =>
		}
		// ... =>
		mv.visitJumpInsn(Opcodes.GOTO, l3);
		// ... =>
		mv.visitLabel(l2);
		// ... =>
	}
}
