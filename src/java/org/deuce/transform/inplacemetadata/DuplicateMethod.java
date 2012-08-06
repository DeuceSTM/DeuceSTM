package org.deuce.transform.inplacemetadata;

import org.deuce.objectweb.asm.Label;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.objectweb.asm.commons.AnalyzerAdapter;
import org.deuce.objectweb.asm.commons.Method;
import org.deuce.transaction.Context;
import org.deuce.transaction.ContextDelegator;
import org.deuce.transform.asm.ExcludeIncludeStore;
import org.deuce.transform.asm.FieldsHolder;
import org.deuce.transform.util.Util;

public class DuplicateMethod extends MethodAdapter {

	final static public String LOCAL_VARIBALE_NAME = "__transactionContext__";

	private final int argumentsSize;
	private final FieldsHolder fieldsHolder;

	private Label firstLabel;
	private Label lastLabel;
	private boolean addContextToTable = false;
	private AnalyzerAdapter analyzerAdapter;
	private boolean ignore = false;

	public DuplicateMethod(MethodVisitor mv, boolean isstatic, Method newMethod, FieldsHolder fieldsHolder) {
		super(mv);
		this.fieldsHolder = fieldsHolder;
		this.argumentsSize = calcArgumentsSize(isstatic, newMethod);
	}

	public void setIgnore(boolean state) {
		this.ignore = state;
	}

	public void setAnalyzer(AnalyzerAdapter analyzerAdapter) {
		this.analyzerAdapter = analyzerAdapter;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		if (ignore) {
			super.visitMethodInsn(opcode, owner, name, desc);
			return;
		}

		if (ExcludeIncludeStore.exclude(owner)) {
			super.visitMethodInsn(opcode, owner, name, desc); // ... = foo( ...
		} else {
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load
																	// context
			Method newMethod = ClassTransformer.createNewMethod(name, desc);
			super.visitMethodInsn(opcode, owner, name, newMethod.getDescriptor()); // ...
																					// =
																					// foo(
																					// ...
		}
	}

	private void duplicateTargeObject(int opcode, String owner, String desc) {

		if (opcode == Opcodes.PUTFIELD) {
			if (desc.equals("J") || desc.equals("D")) {
				mv.visitInsn(Opcodes.DUP2_X1);
				mv.visitInsn(Opcodes.POP2);
				mv.visitInsn(Opcodes.DUP_X2);
			} else {
				mv.visitInsn(Opcodes.DUP_X1);
				mv.visitInsn(Opcodes.POP);
				mv.visitInsn(Opcodes.DUP_X1);
			}
		} else if (opcode == Opcodes.GETFIELD) {
			mv.visitInsn(Opcodes.DUP);
		}
	}

	private void loadTargeObject(int opcode, String owner, String desc) {

		if (opcode == Opcodes.PUTFIELD) {
			if (desc.equals("J") || desc.equals("D")) {
				mv.visitInsn(Opcodes.DUP2_X1);
				mv.visitInsn(Opcodes.POP2);
			} else {
				mv.visitInsn(Opcodes.DUP_X1);
				mv.visitInsn(Opcodes.POP);
			}
		}
	}

	/**
	 * Adds for each field visited a call to the context.
	 */
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {

		if (ignore) {
			super.visitFieldInsn(opcode, owner, name, desc);
			return;
		}

		if (ExcludeIncludeStore.exclude(owner) || name.contains("$")) { // Syntactic
																		// TODO
																		// remove
																		// this
																		// limitation
			super.visitFieldInsn(opcode, owner, name, desc); // ... = foo( ...
			return;
		}

		String fieldsHolderName = fieldsHolder.getFieldsHolderName(owner);
		if (opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC) {
			mv.visitFieldInsn(Opcodes.GETSTATIC, fieldsHolderName, Util.getAddressField(name), ArrayUtil
					.getTxType(desc).getDescriptor());
		} else {
			duplicateTargeObject(opcode, owner, desc);
			mv.visitFieldInsn(Opcodes.GETFIELD, fieldsHolderName, Util.getAddressField(name), ArrayUtil.getTxType(desc)
					.getDescriptor());
		}

		// /* opt */mv.visitInsn(Opcodes.DUP);

		Label l1 = new Label();
		mv.visitJumpInsn(Opcodes.IFNONNULL, l1);

		// /* opt */mv.visitInsn(Opcodes.POP);
		super.visitFieldInsn(opcode, owner, name, desc);

		Label l2 = new Label();
		mv.visitJumpInsn(Opcodes.GOTO, l2);

		mv.visitLabel(l1);

		final Type type = Type.getType(desc);
		switch (opcode) {
		case Opcodes.GETFIELD: // ALOAD 0: this (stack status)

			addBeforeReadCall(fieldsHolderName, name, desc, false);

			super.visitInsn(Opcodes.DUP);
			super.visitFieldInsn(opcode, owner, name, desc);
			loadTargeObject(Opcodes.PUTFIELD, owner, desc.charAt(0) == '[' ? "I" : desc);
			super.visitFieldInsn(Opcodes.GETFIELD, fieldsHolderName, Util.getAddressField(name),
					ArrayUtil.getTxType(desc).getDescriptor());
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load
																	// context
			super.visitMethodInsn(Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.READ_METHOD_NAME, ContextDelegator.getReadMethodDesc(type));

			if (type.getSort() >= Type.ARRAY) {// non primitive
				super.visitTypeInsn(Opcodes.CHECKCAST, Type.getType(desc).getInternalName());
			}
			break;
		case Opcodes.PUTFIELD:
			loadTargeObject(opcode, owner, desc);
			super.visitFieldInsn(Opcodes.GETFIELD, fieldsHolderName, Util.getAddressField(name),
					ArrayUtil.getTxType(desc).getDescriptor());
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load
																	// context
			super.visitMethodInsn(Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.WRITE_METHOD_NAME, ContextDelegator.getWriteMethodDesc(type));
			break;
		case Opcodes.GETSTATIC: // check support for static fields

			addBeforeReadCall(fieldsHolderName, name, desc, true);

			super.visitFieldInsn(opcode, owner, name, desc);
			super.visitFieldInsn(Opcodes.GETSTATIC, fieldsHolderName, Util.getAddressField(name),
					ArrayUtil.getTxType(desc).getDescriptor());
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load
																	// context
			super.visitMethodInsn(Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.READ_METHOD_NAME, ContextDelegator.getReadMethodDesc(type));

			if (type.getSort() >= Type.ARRAY) { // non primitive
				super.visitTypeInsn(Opcodes.CHECKCAST, Type.getType(desc).getInternalName());
			}
			break;
		case Opcodes.PUTSTATIC:
			super.visitFieldInsn(Opcodes.GETSTATIC, fieldsHolderName, Util.getAddressField(name),
					ArrayUtil.getTxType(desc).getDescriptor());
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load
																	// context
			super.visitMethodInsn(Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.STATIC_WRITE_METHOD_NAME, ContextDelegator.getStaticWriteMethodDesc(type));
			break;
		default:
			super.visitFieldInsn(opcode, owner, name, desc);
		}
		mv.visitLabel(l2);
	}

	private void addBeforeReadCall(String owner, String name, String desc, boolean isStatic) {
		if (!isStatic) {
			duplicateTargeObject(Opcodes.GETFIELD, owner, null);
			super.visitFieldInsn(Opcodes.GETFIELD, owner, Util.getAddressField(name), ArrayUtil.getTxType(desc)
					.getDescriptor());
		} else {
			super.visitFieldInsn(Opcodes.GETSTATIC, owner, Util.getAddressField(name), ArrayUtil.getTxType(desc)
					.getDescriptor());
		}
		// /* opt */super.visitInsn(Opcodes.DUP);
		super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
		super.visitMethodInsn(Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
				ContextDelegator.BEFORE_READ_METHOD_NAME, ContextDelegator.BEFORE_READ_METHOD_DESC_META);
	}

	private String getAnalyzerTopStackObjectDesc() {
		if (analyzerAdapter.stack.size() == 0) {
			return null;
		}
		String tos = (String) analyzerAdapter.stack.get(analyzerAdapter.stack.size() - 1);

		int i = tos.lastIndexOf("[");

		if (i != -1) {
			return tos;
		}

		return "L" + tos + ";";
	}

	/**
	 * Adds for each array cell visited a call to the context
	 */
	@Override
	public void visitInsn(int opcode) {
		if (ignore) {
			super.visitInsn(opcode);
			return;
		}

		boolean load = false;
		boolean store = false;
		String desc = null;
		Type type = null;
		switch (opcode) {

		case Opcodes.AALOAD:
			type = Type.getType(Object.class);
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.READ_ARRAY_METHOD_OBJ_DESC_META
					: ContextDelegator.READ_ARRAY_METHOD_OBJ_DESC;
			load = true;
			break;
		case Opcodes.BALOAD:
			type = Type.BYTE_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.READ_ARRAY_METHOD_BYTE_DESC_META
					: ContextDelegator.READ_ARRAY_METHOD_BYTE_DESC;
			load = true;
			break;
		case Opcodes.CALOAD:
			type = Type.CHAR_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.READ_ARRAY_METHOD_CHAR_DESC_META
					: ContextDelegator.READ_ARRAY_METHOD_CHAR_DESC;
			load = true;
			break;
		case Opcodes.SALOAD:
			type = Type.SHORT_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.READ_ARRAY_METHOD_SHORT_DESC_META
					: ContextDelegator.READ_ARRAY_METHOD_SHORT_DESC;
			load = true;
			break;
		case Opcodes.IALOAD:
			type = Type.INT_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.READ_ARRAY_METHOD_INT_DESC_META
					: ContextDelegator.READ_ARRAY_METHOD_INT_DESC;
			load = true;
			break;
		case Opcodes.LALOAD:
			type = Type.LONG_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.READ_ARRAY_METHOD_LONG_DESC_META
					: ContextDelegator.READ_ARRAY_METHOD_LONG_DESC;
			load = true;
			break;
		case Opcodes.FALOAD:
			type = Type.FLOAT_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.READ_ARRAY_METHOD_FLOAT_DESC_META
					: ContextDelegator.READ_ARRAY_METHOD_FLOAT_DESC;
			load = true;
			break;
		case Opcodes.DALOAD:
			type = Type.DOUBLE_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.READ_ARRAY_METHOD_DOUBLE_DESC_META
					: ContextDelegator.READ_ARRAY_METHOD_DOUBLE_DESC;
			load = true;
			break;

		case Opcodes.AASTORE:
			type = Type.getType(Object.class);
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.WRITE_ARRAY_METHOD_OBJ_DESC_META
					: ContextDelegator.WRITE_ARRAY_METHOD_OBJ_DESC;
			store = true;
			break;
		case Opcodes.BASTORE:
			type = Type.BYTE_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.WRITE_ARRAY_METHOD_BYTE_DESC_META
					: ContextDelegator.WRITE_ARRAY_METHOD_BYTE_DESC;
			store = true;
			break;
		case Opcodes.CASTORE:
			type = Type.CHAR_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.WRITE_ARRAY_METHOD_CHAR_DESC_META
					: ContextDelegator.WRITE_ARRAY_METHOD_CHAR_DESC;
			store = true;
			break;
		case Opcodes.SASTORE:
			type = Type.SHORT_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.WRITE_ARRAY_METHOD_SHORT_DESC_META
					: ContextDelegator.WRITE_ARRAY_METHOD_SHORT_DESC;
			store = true;
			break;
		case Opcodes.IASTORE:
			type = Type.INT_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.WRITE_ARRAY_METHOD_INT_DESC_META
					: ContextDelegator.WRITE_ARRAY_METHOD_INT_DESC;
			store = true;
			break;
		case Opcodes.LASTORE:
			type = Type.LONG_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.WRITE_ARRAY_METHOD_LONG_DESC_META
					: ContextDelegator.WRITE_ARRAY_METHOD_LONG_DESC;
			store = true;
			break;
		case Opcodes.FASTORE:
			type = Type.FLOAT_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.WRITE_ARRAY_METHOD_FLOAT_DESC_META
					: ContextDelegator.WRITE_ARRAY_METHOD_FLOAT_DESC;
			store = true;
			break;
		case Opcodes.DASTORE:
			type = Type.DOUBLE_TYPE;
			desc = ContextDelegator.inLocalMetadata() ? ContextDelegator.WRITE_ARRAY_METHOD_DOUBLE_DESC_META
					: ContextDelegator.WRITE_ARRAY_METHOD_DOUBLE_DESC;
			store = true;
			break;
		}

		if (load) {
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load
																	// context
			super.visitMethodInsn(Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.getReadArrayMethodDesc(type), desc);

			if (opcode == Opcodes.AALOAD) { // non primitive array need cast
				String tyDesc = getAnalyzerTopStackObjectDesc();
				Type tt = ArrayUtil.getTxArrayArrayType(tyDesc);
				if (tyDesc.lastIndexOf("[") != -1) {
					super.visitTypeInsn(Opcodes.CHECKCAST, tt.getInternalName());
				} else {
					super.visitTypeInsn(Opcodes.CHECKCAST, Type.getType(tyDesc).getInternalName());
				}
			}
		} else if (store) {
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load
																	// context
			super.visitMethodInsn(Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.WRITE_ARR_METHOD_NAME, desc);
		} else {
			super.visitInsn(opcode);
		}
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		super.visitIincInsn(newIndex(var), increment); // increase index due to
														// context
	}

	@Override
	public void visitLabel(Label label) {
		if (firstLabel == null)
			firstLabel = label;
		lastLabel = label;
		super.visitLabel(label);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if (this.argumentsSize > index + 1) // argument
		{
			super.visitLocalVariable(name, desc, signature, start, end, index); // non
																				// static
																				// method
																				// has
																				// this
			return;
		}
		// add context as last argument
		// the first local variable and was never added before
		if (this.argumentsSize == index + 1 && !addContextToTable) {
			addContextToTable = true;
			super.visitLocalVariable(LOCAL_VARIBALE_NAME, Context.CONTEXT_DESC, null, firstLabel, lastLabel, index);
		}

		// increase all the locals index
		super.visitLocalVariable(name, desc, signature, start, end, index + 1);
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(maxStack + 3, maxLocals + 1);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		// increase the local variable index by 1
		super.visitVarInsn(opcode, newIndex(var));
	}

	/**
	 * Calculate the new local index according to its position. If it's not a
	 * function argument (local variable) its index increased by 1.
	 * 
	 * @param currIndex
	 *            current index
	 * @return new index
	 */
	private int newIndex(int currIndex) {
		return currIndex + 1 < this.argumentsSize ? currIndex : currIndex + 1;
	}

	private int calcArgumentsSize(boolean isStatic, Method newMethod) {
		int size = isStatic ? 0 : 1; // if not static "this" is the first
										// argument
		for (Type type : newMethod.getArgumentTypes()) {
			size += type.getSize();
		}
		return size;
	}

}
