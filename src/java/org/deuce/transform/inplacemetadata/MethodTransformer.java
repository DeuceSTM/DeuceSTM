package org.deuce.transform.inplacemetadata;

import java.util.HashMap;
import java.util.Stack;

import org.deuce.Unsafe;
import org.deuce.objectweb.asm.AnnotationVisitor;
import org.deuce.objectweb.asm.Attribute;
import org.deuce.objectweb.asm.Label;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.objectweb.asm.commons.AnalyzerAdapter;
import org.deuce.objectweb.asm.commons.Method;
import org.deuce.transaction.Context;
import org.deuce.transform.asm.ExcludeIncludeStore;
import org.deuce.transform.asm.FieldsHolder;
import org.deuce.transform.asm.method.AtomicMethod;
import org.deuce.transform.asm.method.MethodAnnotationVisitor;
import org.deuce.transform.inplacemetadata.ClassTransformer.MultiArrayMethod;
import org.deuce.transform.inplacemetadata.ClassTransformer.NonInsnMethod;

/**
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
public class MethodTransformer implements MethodVisitor {
	final static private String UNSAFE_DESCRIPTOR = Type.getDescriptor(Unsafe.class);

	private MethodVisitor originalMethod;

	final private MethodVisitor originalCopyMethod;

	private MethodVisitor copyMethod;

	final private String className;
	final private String methodName;
	final private String descriptor; // original descriptor
	final private boolean isStatic;
	final private Method newMethod;

	final private HashMap<Label, Label> labelMap = new HashMap<Label, Label>();

	private AnalyzerAdapter analyzerAdapter = null;
	private MethodAdapter duplicateMethod;
	private ClassTransformer clazzT;

	public MethodTransformer(MethodVisitor originalMethod, MethodVisitor copyMethod, String className, int access,
			String methodName, String descriptor, String originalDesc, Method newMethod, FieldsHolder fieldsHolder,
			ClassTransformer clazzT) {
		this.originalMethod = originalMethod;
		this.newMethod = newMethod;
		this.isStatic = (access & Opcodes.ACC_STATIC) != 0;
		this.originalCopyMethod = copyMethod;

		// The AnalyzerAdapter delegates the call to the DuplicateMethod, while
		// the
		// DuplicateMethod uses the analyzer for stack state in the original
		// method.
		// AnalyzerAdapter analyzerAdapter = null;
		analyzerAdapter = new AnalyzerAdapter(className, access, methodName, originalDesc, null);

		if (originalCopyMethod != null) {
			duplicateMethod = new DuplicateMethod(copyMethod, isStatic, newMethod, fieldsHolder);
			((DuplicateMethod) duplicateMethod).setAnalyzer(analyzerAdapter);
		}

		this.copyMethod = duplicateMethod;
		this.className = className;
		this.methodName = methodName;
		this.descriptor = descriptor;

		this.clazzT = clazzT;
	}

	public void visitCode() {
		originalMethod.visitCode();
		if (originalCopyMethod != null) {
			copyMethod.visitCode();
		}
	}

	private Stack<Boolean> enables = new Stack<Boolean>();

	public void disableDuplicateInstrumentation(boolean state) {
		if (!enables.empty()) {
			if (enables.peek() == state) {
				enables.push(state);
				return;
			} else {
				enables.pop();
				if (!enables.isEmpty()) {
					return;
				}
			}
		} else {
			enables.push(state);
		}
		if (originalCopyMethod != null) {
			((DuplicateMethod) duplicateMethod).setIgnore(state);
		}
	}

	private boolean ignore = false;

	public void disableMethodInstrumentation(boolean state) {
		this.ignore = state;
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		// FIXME we might saw other annotations before and we need to put it on
		// the
		// new AtomicMethod need to create an atomic method from the original
		// method
		if (AtomicMethod.ATOMIC_DESCRIPTOR.equals(desc) && !(originalMethod instanceof AtomicMethod)
				&& originalCopyMethod != null) {
			originalMethod = new AtomicMethod(originalMethod, className, methodName, descriptor, newMethod, isStatic);
		}

		// if marked as Unsafe no just duplicate the method as is.
		if (UNSAFE_DESCRIPTOR.equals(desc))
			copyMethod = originalCopyMethod;

		// TODO find another way
		if (!desc.contains("org/junit")) {
			return new MethodAnnotationVisitor(originalMethod.visitAnnotation(desc, visible),
					originalCopyMethod != null ? copyMethod.visitAnnotation(desc, visible) : null);
		} else {
			return originalMethod.visitAnnotation(desc, visible);
		}
	}

	public AnnotationVisitor visitAnnotationDefault() {
		return new MethodAnnotationVisitor(originalMethod.visitAnnotationDefault(), copyMethod.visitAnnotationDefault());
	}

	public void visitAttribute(Attribute attr) {
		analyzerAdapter.visitAttribute(attr);
		originalMethod.visitAttribute(attr);
		if (originalCopyMethod != null) {
			copyMethod.visitAttribute(attr);
		}
	}

	public void visitEnd() {
		originalMethod.visitEnd();
		if (originalCopyMethod != null) {
			copyMethod.visitEnd();
		}
	}

	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		if (!ignore) {
			analyzerAdapter.visitFieldInsn(opcode, owner, name, desc);
			if (Type.getType(desc).getSort() == Type.ARRAY) {
				desc = ArrayUtil.getTxArrayArrayType(desc).getDescriptor();
				originalMethod.visitFieldInsn(opcode, owner, name, desc);
				if (originalCopyMethod != null) {
					copyMethod.visitFieldInsn(opcode, owner, name, desc);
				}
				return;
			}
		}
		originalMethod.visitFieldInsn(opcode, owner, name, desc);
		if (originalCopyMethod != null) {
			copyMethod.visitFieldInsn(opcode, owner, name, desc);
		}
	}

	public void visitFrame(int type, int local, Object[] local2, int stack, Object[] stack2) {
		if (!ignore) {
			analyzerAdapter.visitFrame(type, local, local2, stack, stack2);
		}
		originalMethod.visitFrame(type, local, local2, stack, stack2);
		if (originalCopyMethod != null) {
			copyMethod.visitFrame(type, local, local2, stack, stack2);
		}
	}

	public void visitIincInsn(int var, int increment) {
		if (!ignore) {
			analyzerAdapter.visitIincInsn(var, increment);
		}
		originalMethod.visitIincInsn(var, increment);
		if (originalCopyMethod != null) {
			copyMethod.visitIincInsn(var, increment);
		}
	}

	private String getAnalyzerTopStackObjectDesc() {
		if (analyzerAdapter.stack.size() == 0) {
			return null;
		}

		String tos = null;

		if (analyzerAdapter.stack.get(analyzerAdapter.stack.size() - 1) instanceof Integer) {
			return Type.INT_TYPE.getDescriptor();
		} else {
			tos = (String) analyzerAdapter.stack.get(analyzerAdapter.stack.size() - 1);
		}

		int i = tos.lastIndexOf("[");
		if (i != -1) {
			return tos;
		}

		return "L" + tos + ";";
	}

	private String getAnalyzerAaloadArrayDesc() {
		return (String) analyzerAdapter.stack.get(analyzerAdapter.stack.size() - 2);
	}

	private String getAnalyzerAastoreArrayDesc() {
		return (String) analyzerAdapter.stack.get(analyzerAdapter.stack.size() - 3);
	}

	public void visitInsn(int opcode) {
		if (!ignore) {
			String arrayDesc = null;

			if (opcode == Opcodes.AALOAD)
				arrayDesc = getAnalyzerAaloadArrayDesc();
			else if (opcode == Opcodes.AASTORE)
				arrayDesc = getAnalyzerAastoreArrayDesc();

			analyzerAdapter.visitInsn(opcode);
			Type t = null;

			/*
			 * If the opcode is one of the ALOAD family, that means the stack is
			 * of the form:
			 * 
			 * [ ..., arrayref, index =>
			 * 
			 * We must replace the instruction with an AALOAD, because the array
			 * elements are TxArrField. Then we cast it to the appropriate
			 * TxArrField type (correspondent to the original _ALOAD specific
			 * type instruction), and load TxArrField.array[TxArrField.index].
			 * 
			 * Legend: arr -> arrayref idx -> array access index (int) obj ->
			 * objectref obj* -> objectref (correct TxArrField type) val ->
			 * value of the original array's element val* -> value of the
			 * original array's element (correct type) newval -> new value to be
			 * inserted
			 */
			if (opcode == Opcodes.AALOAD || opcode == Opcodes.BALOAD || opcode == Opcodes.CALOAD
					|| opcode == Opcodes.SALOAD || opcode == Opcodes.IALOAD || opcode == Opcodes.LALOAD
					|| opcode == Opcodes.FALOAD || opcode == Opcodes.DALOAD) {
				switch (opcode) {
				case Opcodes.AALOAD:
					if (Type.getType(getAnalyzerTopStackObjectDesc()).getSort() == Type.ARRAY) {
						t = ArrayUtil.getTxArrayType(Type.getType(Object.class));
					} else {
						t = ArrayUtil.getTxArrayType(getAnalyzerTopStackObjectDesc());
					}
					break;
				case Opcodes.BALOAD:
					t = ArrayUtil.getTxArrayType(Type.BYTE_TYPE);
					break;
				case Opcodes.CALOAD:
					t = ArrayUtil.getTxArrayType(Type.CHAR_TYPE);
					break;
				case Opcodes.SALOAD:
					t = ArrayUtil.getTxArrayType(Type.SHORT_TYPE);
					break;
				case Opcodes.IALOAD:
					t = ArrayUtil.getTxArrayType(Type.INT_TYPE);
					break;
				case Opcodes.LALOAD:
					t = ArrayUtil.getTxArrayType(Type.LONG_TYPE);
					break;
				case Opcodes.FALOAD:
					t = ArrayUtil.getTxArrayType(Type.FLOAT_TYPE);
					break;
				case Opcodes.DALOAD:
					t = ArrayUtil.getTxArrayType(Type.DOUBLE_TYPE);
					break;
				}

				// ..., arr, idx =>
				originalMethod.visitInsn(Opcodes.AALOAD);
				// ..., obj =>
				if (Type.getType(getAnalyzerTopStackObjectDesc()).getSort() == Type.ARRAY) {
					originalMethod.visitTypeInsn(Opcodes.CHECKCAST, ArrayUtil.getTxArrayArrayType(t).getInternalName());
				} else {
					originalMethod.visitTypeInsn(Opcodes.CHECKCAST, t.getInternalName());
				}
				// ..., obj* =>
				if (opcode == Opcodes.AALOAD) {
					/*
					 * If the original arrayref was multidimensional, that means
					 * the objectref on top of this stack is a TxArrObjectField.
					 * It also means it is being used in a multiarray context.
					 * Therefore we must get the nextDim field reference instead
					 * of loading TxArrField.array[TxArrField.index].
					 */
					if (ArrayUtil.getArrayDim(arrayDesc) > 1) {
						originalMethod.visitFieldInsn(Opcodes.GETFIELD, t.getInternalName(), "nextDim",
								"Ljava/lang/Object;");
					} else {
						originalMethod.visitInsn(Opcodes.DUP);
						// ..., obj*, obj* =>
						originalMethod.visitFieldInsn(Opcodes.GETFIELD, t.getInternalName(), "array",
								"[" + Type.getDescriptor(Object.class));
						// ..., obj*, arr =>
						originalMethod.visitInsn(Opcodes.SWAP);
						// ..., arr, obj* =>
						originalMethod.visitFieldInsn(Opcodes.GETFIELD, t.getInternalName(), "index",
								Type.getDescriptor(int.class));
						// ..., arr, idx =>
						originalMethod.visitInsn(opcode);
					}
				} else {
					originalMethod.visitInsn(Opcodes.DUP);
					// ..., obj*, obj* =>
					originalMethod.visitFieldInsn(Opcodes.GETFIELD, t.getInternalName(), "array", "["
							+ ArrayUtil.getTypeFromTxArray(t).getDescriptor());
					// ..., obj*, arr =>
					originalMethod.visitInsn(Opcodes.SWAP);
					// ..., arr, obj* =>
					originalMethod.visitFieldInsn(Opcodes.GETFIELD, t.getInternalName(), "index",
							Type.getDescriptor(int.class));
					// ..., arr, idx =>
					originalMethod.visitInsn(opcode);
				}
				// ..., val =>
				if (opcode == Opcodes.AALOAD) {
					if (Type.getType(getAnalyzerTopStackObjectDesc()).getSort() == Type.ARRAY) {
						originalMethod.visitTypeInsn(Opcodes.CHECKCAST,
								ArrayUtil.getTxArrayArrayType(getAnalyzerTopStackObjectDesc()).getInternalName());
					} else {
						originalMethod.visitTypeInsn(Opcodes.CHECKCAST, Type.getType(getAnalyzerTopStackObjectDesc())
								.getInternalName());
					}
				}
				// ..., val* =>

				if (originalCopyMethod != null) {
					copyMethod.visitInsn(opcode);
				}
				return;

				/*
				 * If the opcode is one of the ASTORE family, that means the
				 * stack is of the form:
				 * 
				 * [ ..., arrayref, index, value =>
				 * 
				 * We must AALOAD the arrayref[index] element first, cast it to
				 * the appropriate TxArrField type, and substitute the arrayref
				 * and index below with the TxArrField.array and
				 * TxArrField.index, respectively.
				 * 
				 * Legend: arr -> arrayref idx -> array access index (int) obj
				 * -> objectref obj* -> objectref (correct TxArrField type) val
				 * -> value of the original array's element val* -> value of the
				 * original array's element (correct type) newval -> new value
				 * to be inserted
				 */
			} else if (opcode == Opcodes.AASTORE || opcode == Opcodes.BASTORE || opcode == Opcodes.CASTORE
					|| opcode == Opcodes.SASTORE || opcode == Opcodes.IASTORE || opcode == Opcodes.LASTORE
					|| opcode == Opcodes.FASTORE || opcode == Opcodes.DASTORE) {
				switch (opcode) {
				case Opcodes.AASTORE:
					t = Type.getType(Object.class);
					break;
				case Opcodes.BASTORE:
					t = Type.BYTE_TYPE;
					break;
				case Opcodes.CASTORE:
					t = Type.CHAR_TYPE;
					break;
				case Opcodes.SASTORE:
					t = Type.SHORT_TYPE;
					break;
				case Opcodes.IASTORE:
					t = Type.INT_TYPE;
					break;
				case Opcodes.LASTORE:
					t = Type.LONG_TYPE;
					break;
				case Opcodes.FASTORE:
					t = Type.FLOAT_TYPE;
					break;
				case Opcodes.DASTORE:
					t = Type.DOUBLE_TYPE;
					break;
				}

				Type arrayType = ArrayUtil.getTxArrayType(t);

				// ..., arr, idx, newval =>
				if (t == Type.LONG_TYPE || t == Type.DOUBLE_TYPE) {
					originalMethod.visitInsn(Opcodes.DUP2_X2);
					originalMethod.visitInsn(Opcodes.POP2);
				} else {
					originalMethod.visitInsn(Opcodes.DUP_X2);
					originalMethod.visitInsn(Opcodes.POP);
				}
				// ..., newval, arr, idx =>
				originalMethod.visitInsn(Opcodes.AALOAD);
				// ..., newval, obj =>
				originalMethod.visitTypeInsn(Opcodes.CHECKCAST, arrayType.getInternalName());
				// ..., newval, obj* =>

				/*
				 * If the original arrayref was multidimensional, that means the
				 * objectref on this stack is a TxArrObjectField. It also means
				 * it is being used in a multiarray context. Therefore we must
				 * update the nextDim field reference instead of
				 * TxArrField.array[TxArrField.index].
				 */
				if (opcode == Opcodes.AASTORE && ArrayUtil.getArrayDim(arrayDesc) > 1) {
					if (t == Type.LONG_TYPE || t == Type.DOUBLE_TYPE) {
						originalMethod.visitInsn(Opcodes.DUP_X2);
						originalMethod.visitInsn(Opcodes.POP);
					} else {
						originalMethod.visitInsn(Opcodes.SWAP);
					}
					// ..., obj*, newval =>
					originalMethod.visitFieldInsn(Opcodes.PUTFIELD, arrayType.getInternalName(), "nextDim",
							"Ljava/lang/Object;");
				} else {
					originalMethod.visitInsn(Opcodes.DUP);
					// ..., newval, obj*, obj* =>
					originalMethod.visitFieldInsn(Opcodes.GETFIELD, arrayType.getInternalName(), "array",
							"[" + t.getDescriptor());
					// ..., newval, obj*, arr =>
					originalMethod.visitInsn(Opcodes.SWAP);
					// ..., newval, arr, obj* =>
					originalMethod.visitFieldInsn(Opcodes.GETFIELD, arrayType.getInternalName(), "index",
							Type.getDescriptor(int.class));
					// ..., newval, arr, idx =>
					if (t == Type.LONG_TYPE || t == Type.DOUBLE_TYPE)
						originalMethod.visitInsn(Opcodes.DUP2_X2);
					else
						originalMethod.visitInsn(Opcodes.DUP2_X1);
					// ..., arr, idx, newval, arr, idx =>
					originalMethod.visitInsn(Opcodes.POP2);
					// ..., arr, idx, newval =>
					originalMethod.visitInsn(opcode);
				}
				// ... =>

				if (originalCopyMethod != null) {
					copyMethod.visitInsn(opcode);
				}
				return;
			}
		}

		originalMethod.visitInsn(opcode);
		if (originalCopyMethod != null) {
			copyMethod.visitInsn(opcode);
		}
	}

	public void visitIntInsn(int opcode, int operand) {
		if (!ignore) {
			analyzerAdapter.visitIntInsn(opcode, operand);
		}

		originalMethod.visitIntInsn(opcode, operand);
		if (originalCopyMethod != null) {
			copyMethod.visitIntInsn(opcode, operand);
		}

		if (!ignore) {
			/*
			 * If the opcode is NEWARRAY, that means the stack is of the form:
			 * 
			 * [ ..., arrayref =>
			 * 
			 * So, the desired transformation is to start with the original
			 * arrayref on top of the stack, and finish with the new TxArray
			 * arrayref instead.
			 * 
			 * [ ..., arrayref (TxArray) =>
			 * 
			 * In the end, the original array is "lost" in the heap, and the
			 * only objects holding a reference to it will be the TxArray
			 * elements.
			 * 
			 * Legend: arr -> arrayref obj -> objectref len -> result of
			 * ARRAYLENGTH (int) arr* -> arrayref (TxArray)
			 */
			if (opcode == Opcodes.NEWARRAY) {

				Label l1 = new Label();
				Label l2 = new Label();
				Label l3 = new Label();

				Type arrayType = ArrayUtil.getTxArrayType(operand);

				// ..., arr =>
				originalMethod.visitInsn(Opcodes.DUP);
				// ..., arr, arr =>
				originalMethod.visitInsn(Opcodes.ARRAYLENGTH);
				// ..., arr, len =>
				originalMethod.visitTypeInsn(Opcodes.ANEWARRAY, arrayType.getInternalName());
				// ..., arr, arr* =>
				originalMethod.visitInsn(Opcodes.DUP2);
				// ..., arr, arr*, arr, arr* =>
				originalMethod.visitInsn(Opcodes.SWAP);
				// ..., arr, arr*, arr*, arr =>
				originalMethod.visitInsn(Opcodes.DUP);
				// ..., arr, arr*, arr*, arr, arr =>
				originalMethod.visitInsn(Opcodes.ARRAYLENGTH);
				// ..., arr, arr*, arr*, arr, i (len) =>
				originalMethod.visitLabel(l3);
				// ..., arr, arr*, arr*, arr, i =>
				originalMethod.visitInsn(Opcodes.DUP);
				// ..., arr, arr*, arr*, arr, i, i =>
				originalMethod.visitJumpInsn(Opcodes.IFGT, l1);
				// ..., arr, arr*, arr*, arr, i =>
				originalMethod.visitInsn(Opcodes.POP2);
				// ..., arr, arr*, arr* =>
				originalMethod.visitInsn(Opcodes.POP);
				// ..., arr, arr* =>
				originalMethod.visitInsn(Opcodes.SWAP);
				// ..., arr*, arr =>
				originalMethod.visitInsn(Opcodes.POP);
				// ..., arr* =>
				originalMethod.visitJumpInsn(Opcodes.GOTO, l2);
				// ..., arr* =>
				originalMethod.visitLabel(l1);
				// ..., arr, arr*, arr*, arr, i =>
				originalMethod.visitInsn(Opcodes.ICONST_1);
				// ..., arr, arr*, arr*, arr, i, 1 =>
				originalMethod.visitInsn(Opcodes.ISUB);
				// ..., arr, arr*, arr*, arr, i-1 =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, arr*, i-1, arr*, arr, i-1 =>
				originalMethod.visitInsn(Opcodes.DUP_X1);
				// ..., arr, arr*, i-1, arr*, i-1, arr, i-1 =>
				originalMethod.visitTypeInsn(Opcodes.NEW, arrayType.getInternalName());
				// ..., arr, arr*, i-1, arr*, i-1, arr, i-1, obj =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, arr*, i-1, arr*, i-1, obj, arr, i-1, obj =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, arr*, i-1, arr*, i-1, obj, obj, arr, i-1, obj =>
				originalMethod.visitInsn(Opcodes.POP);
				// ..., arr, arr*, i-1, arr*, i-1, obj, obj, arr, i-1 =>
				originalMethod.visitMethodInsn(Opcodes.INVOKESPECIAL, arrayType.getInternalName(), "<init>", "(["
						+ ArrayUtil.getTypeFromNewarrayOpcode(operand) + "I)V");
				// ..., arr, arr*, i-1, arr*, i-1, obj =>
				originalMethod.visitInsn(Opcodes.AASTORE);
				// ..., arr, arr*, i-1 =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., i-1, arr, arr*, i-1 =>
				originalMethod.visitInsn(Opcodes.POP);
				// ..., i-1, arr, arr* =>
				originalMethod.visitInsn(Opcodes.SWAP);
				// ..., i-1, arr*, arr =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, i-1, arr*, arr =>
				originalMethod.visitInsn(Opcodes.SWAP);
				// ..., arr, i-1, arr, arr* =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, arr*, i-1, arr, arr* =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, arr*, arr*, i-1, arr, arr* =>
				originalMethod.visitInsn(Opcodes.POP);
				// ..., arr, arr*, arr*, i-1, arr =>
				originalMethod.visitInsn(Opcodes.SWAP);
				// ..., arr, arr*, arr*, arr, i-1 =>
				originalMethod.visitJumpInsn(Opcodes.GOTO, l3);
				// ..., arr, arr*, arr*, arr, i-1 =>
				originalMethod.visitLabel(l2);
				// ..., arr* =>

				/*
				 * Exactly the same code as above.
				 */
				if (originalCopyMethod != null) {
					disableDuplicateInstrumentation(true);

					l1 = new Label();
					l2 = new Label();
					l3 = new Label();

					copyMethod.visitInsn(Opcodes.DUP);
					copyMethod.visitInsn(Opcodes.ARRAYLENGTH);
					copyMethod.visitTypeInsn(Opcodes.ANEWARRAY, arrayType.getInternalName());
					copyMethod.visitInsn(Opcodes.DUP2);
					copyMethod.visitInsn(Opcodes.SWAP);
					copyMethod.visitInsn(Opcodes.DUP);
					copyMethod.visitInsn(Opcodes.ARRAYLENGTH);
					copyMethod.visitLabel(l3);
					copyMethod.visitInsn(Opcodes.DUP);
					copyMethod.visitJumpInsn(Opcodes.IFGT, l1);
					copyMethod.visitInsn(Opcodes.POP2);
					copyMethod.visitInsn(Opcodes.POP);
					copyMethod.visitInsn(Opcodes.SWAP);
					copyMethod.visitInsn(Opcodes.POP);
					copyMethod.visitJumpInsn(Opcodes.GOTO, l2);
					copyMethod.visitLabel(l1);
					copyMethod.visitInsn(Opcodes.ICONST_1);
					copyMethod.visitInsn(Opcodes.ISUB);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.DUP_X1);
					copyMethod.visitTypeInsn(Opcodes.NEW, arrayType.getInternalName());
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.POP);
					copyMethod.visitMethodInsn(Opcodes.INVOKESPECIAL, arrayType.getInternalName(), "<init>", "(["
							+ ArrayUtil.getTypeFromNewarrayOpcode(operand) + "I)V");
					copyMethod.visitInsn(Opcodes.AASTORE);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.POP);
					copyMethod.visitInsn(Opcodes.SWAP);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.SWAP);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.POP);
					copyMethod.visitInsn(Opcodes.SWAP);
					copyMethod.visitJumpInsn(Opcodes.GOTO, l3);
					copyMethod.visitLabel(l2);

					disableDuplicateInstrumentation(false);
				}
			}
		}
	}

	public void visitJumpInsn(int opcode, Label label) {
		if (!ignore) {
			analyzerAdapter.visitJumpInsn(opcode, label);
		}
		originalMethod.visitJumpInsn(opcode, label);
		if (originalCopyMethod != null) {
			copyMethod.visitJumpInsn(opcode, getLabel(label));
		}
	}

	public void visitLabel(Label label) {
		if (!ignore) {
			analyzerAdapter.visitLabel(label);
		}
		originalMethod.visitLabel(label);
		if (originalCopyMethod != null) {
			copyMethod.visitLabel(getLabel(label));
		}
	}

	public void visitLdcInsn(Object cst) {
		if (!ignore) {
			analyzerAdapter.visitLdcInsn(cst);
		}
		originalMethod.visitLdcInsn(cst);
		if (originalCopyMethod != null) {
			copyMethod.visitLdcInsn(cst);
		}
	}

	public void visitLineNumber(int line, Label start) {
		originalMethod.visitLineNumber(line, start);
		if (originalCopyMethod != null) {
			copyMethod.visitLineNumber(line, getLabel(start));
		}
	}

	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		originalMethod.visitLocalVariable(name, desc, signature, start, end, index);
		if (originalCopyMethod != null) {
			copyMethod.visitLocalVariable(name, desc, signature, getLabel(start), getLabel(end), index);
		}
	}

	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		if (!ignore) {
			analyzerAdapter.visitLookupSwitchInsn(dflt, keys, labels);
		}
		originalMethod.visitLookupSwitchInsn(dflt, keys, labels);
		if (originalCopyMethod != null) {
			copyMethod.visitLookupSwitchInsn(getLabel(dflt), keys, getCopyLabels(labels));
		}
	}

	public void visitMaxs(int maxStack, int maxLocals) {
		if (!ignore) {
			analyzerAdapter.visitMaxs(maxStack, maxLocals);
		}
		originalMethod.visitMaxs(maxStack, maxLocals);
		if (originalCopyMethod != null) {
			copyMethod.visitMaxs(maxStack, maxLocals);
		}
	}

	public boolean isBootstrapClass(String owner) {
		owner = owner.replace('/', '.');
		return owner.matches("java\\..*|javax\\..*");
	}

	public boolean hasArrayReturnOrParameter(String desc) {
		Method m = new Method("dummy", desc);
		for (Type t : m.getArgumentTypes()) {
			if (t.getSort() == Type.ARRAY) {
				return true;
			}
		}
		return m.getReturnType().getSort() == Type.ARRAY;
	}

	public boolean hasArrayReturnOrArgument(String desc) {
		Method m = new Method("dummy", desc);
		int numberOfParameters = m.getArgumentTypes().length;

		Object obj;
		String str;
		int stackSize = analyzerAdapter.stack.size();
		for (int i = numberOfParameters; i > 0; i--) {
			obj = analyzerAdapter.stack.get(stackSize - i);

			if (!(obj instanceof Integer)) {
				str = (String) obj;
				if (str.lastIndexOf('[') != -1)
					return true;
			}
		}

		return m.getReturnType().getSort() == Type.ARRAY;
	}

	public String updateParametersToArray(String desc) {
		Method m = new Method("dummy", desc);
		Type[] params = m.getArgumentTypes();
		int numberOfParameters = params.length;
		Type[] newParams = new Type[numberOfParameters];

		Object obj;
		String str;
		int stackSize = analyzerAdapter.stack.size();

		// i is the argument's offset from the top of the stack
		// j is its index on the Type[] arrays
		for (int i = numberOfParameters, j = 0; i > 0; i--, j++) {
			obj = analyzerAdapter.stack.get(stackSize - i);

			if (!(obj instanceof Integer)) {
				str = (String) obj;
				if (str.lastIndexOf('[') != -1)
					newParams[j] = Type.getType(str);
				else
					newParams[j] = params[j];
			} else {
				newParams[j] = params[j];
			}
		}

		return new Method("dummy", m.getReturnType(), newParams).getDescriptor();
	}

	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		if (!ignore && !(name.equals("main") && desc.equals("([Ljava/lang/String;)V"))) {
//			boolean isBC = isBootstrapClass(owner); 
//
//			// If it is a Bootstrap class with at least one array as argument.
//			if (isBC && hasArrayReturnOrArgument(desc)) {
			// If it's a method from an excluded class with at least one array as argument.
			boolean isExcludedClass = ExcludeIncludeStore.exclude(owner);
			if (isExcludedClass && hasArrayReturnOrParameter(desc)) {
				String nimDesc = updateParametersToArray(desc);

				NonInsnMethod nim = clazzT.new NonInsnMethod(name, nimDesc, desc, owner, opcode);
				if (!clazzT.nonInsnMethods.contains(nim)) {
					clazzT.nonInsnMethods.add(nim);
				}

				originalMethod.visitInsn(Opcodes.ACONST_NULL);
				originalMethod.visitMethodInsn(Opcodes.INVOKESTATIC, nim.insnOwner, nim.insnName, nim.insnDesc);

				if (opcode == Opcodes.INVOKESPECIAL) {
					originalMethod.visitInsn(Opcodes.DUP_X2);
					originalMethod.visitInsn(Opcodes.POP);
					originalMethod.visitInsn(Opcodes.POP2);
				}

				if (originalCopyMethod != null) {
					disableDuplicateInstrumentation(true);

					copyMethod.visitInsn(Opcodes.ACONST_NULL);
					copyMethod.visitMethodInsn(Opcodes.INVOKESTATIC, nim.insnOwner, nim.insnName, nim.insnDesc);

					if (opcode == Opcodes.INVOKESPECIAL) {
						copyMethod.visitInsn(Opcodes.DUP_X2);
						copyMethod.visitInsn(Opcodes.POP);
						copyMethod.visitInsn(Opcodes.POP2);
					}

					disableDuplicateInstrumentation(false);
				}
			} else {
				if (owner.charAt(0) == '[') {
					Type t = Type.getType(owner);
					if (t.getSort() == Type.ARRAY) {
						owner = ArrayUtil.getTxArrayArrayType(t).getDescriptor();
					}
				}

				originalMethod.visitMethodInsn(opcode, owner, name, ArrayUtil.updateMethodArrayArgumentsAndReturn(desc)
						.getDescriptor());
				if (originalCopyMethod != null) {
					copyMethod.visitMethodInsn(opcode, owner, name, ArrayUtil.updateMethodArrayArgumentsAndReturn(desc)
							.getDescriptor());
				}
			}
		} else {
			originalMethod.visitMethodInsn(opcode, owner, name, desc);
			if (originalCopyMethod != null) {
				copyMethod.visitMethodInsn(opcode, owner, name, desc);
			}
		}

		if (!ignore) {
			analyzerAdapter.visitMethodInsn(opcode, owner, name, desc);
		}
	}

	public void visitMultiANewArrayInsn(String desc, int dims) {
		if (!ignore) {
			analyzerAdapter.visitMultiANewArrayInsn(desc, dims);
		}

		originalMethod.visitMultiANewArrayInsn(desc, dims);
		if (originalCopyMethod != null)
			copyMethod.visitMultiANewArrayInsn(desc, dims);

		if (!ignore) {
			String name = ArrayUtil.getTxArrayType(desc).getInternalName().replace("/", "_") + "_multiarray";
			Type[] args = new Type[] { Type.getType(desc), Context.CONTEXT_TYPE };
			// desc is always [[something which evaluates to an object
			Type returnType = ArrayUtil.getTxArrayArrayType(desc);
			int totalDims = ArrayUtil.getArrayDim(getAnalyzerTopStackObjectDesc());

			MultiArrayMethod mim = clazzT.new MultiArrayMethod(name,
					new Method(name, returnType, args).getDescriptor(), dims, totalDims, Type.getType(desc));
			if (!clazzT.multiArrayMethods.contains(mim)) {
				clazzT.multiArrayMethods.add(mim);
			}

			// ..., arr =>
			originalMethod.visitInsn(Opcodes.ACONST_NULL);
			// ..., arr, null =>
			originalMethod.visitMethodInsn(Opcodes.INVOKESTATIC, className, mim.name, mim.desc);
			// ... arr* =>

			if (originalCopyMethod != null) {
				disableDuplicateInstrumentation(true);

				// ..., arr =>
				copyMethod.visitInsn(Opcodes.ACONST_NULL);
				// ..., arr, null =>
				copyMethod.visitMethodInsn(Opcodes.INVOKESTATIC, className, mim.name, mim.desc);
				// ..., arr* =>

				disableDuplicateInstrumentation(false);
			}
		}
	}

	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		return new MethodAnnotationVisitor(originalMethod.visitParameterAnnotation(parameter, desc, visible),
				copyMethod.visitParameterAnnotation(parameter, desc, visible));
	}

	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
		if (!ignore) {
			analyzerAdapter.visitTableSwitchInsn(min, max, dflt, labels);
		}
		originalMethod.visitTableSwitchInsn(min, max, dflt, labels);
		if (originalCopyMethod != null) {
			copyMethod.visitTableSwitchInsn(min, max, getLabel(dflt), getCopyLabels(labels));
		}
	}

	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		originalMethod.visitTryCatchBlock(start, end, handler, type);
		if (originalCopyMethod != null) {
			copyMethod.visitTryCatchBlock(getLabel(start), getLabel(end), getLabel(handler), type);
		}
	}

	public void visitTypeInsn(int opcode, String type) {
		if (!ignore) {
			analyzerAdapter.visitTypeInsn(opcode, type);

			/*
			 * If the opcode is ANEWARRAY, that means the stack is of the form:
			 * 
			 * [ ..., arrayref =>
			 * 
			 * So, the desired transformation is to start with the original
			 * arrayref on top of the stack, and finish with the new TxArray
			 * arrayref instead.
			 * 
			 * [ ..., arrayref (TxArray) =>
			 * 
			 * In the end, the original array is "lost" in the heap, and the
			 * only objects holding a reference to it will be the TxArray
			 * elements.
			 * 
			 * Legend: arr -> arrayref obj -> objectref len -> result of
			 * ARRAYLENGTH (int) arr* -> arrayref (TxArray)
			 */
			if (opcode == Opcodes.ANEWARRAY) {
				// ..., count (int) =>
				originalMethod.visitTypeInsn(opcode, type);
				if (originalCopyMethod != null)
					copyMethod.visitTypeInsn(opcode, type);
				// ..., arr =>

				String ctorSignature;
				boolean typeIsArray = false;
				if (type.lastIndexOf("[") == -1) {
					type = "L" + type + ";";
					// ([Ljava/lang/Object;I)V
					ctorSignature = "([" + Type.getDescriptor(Object.class) + Type.getDescriptor(int.class) + ")"
							+ Type.getDescriptor(void.class);
				} else {
					type = "[" + type;
					typeIsArray = true;
					// ([Ljava/lang/Object;ILjava/lang/Object;)V
					ctorSignature = "([" + Type.getDescriptor(Object.class) + Type.getDescriptor(int.class)
							+ Type.getDescriptor(Object.class) + ")" + Type.getDescriptor(void.class);
				}

				Label l1 = new Label();
				Label l2 = new Label();
				Label l3 = new Label();

				Type arrayType = ArrayUtil.getArrayType(ArrayUtil.getTxArrayArrayType(type));

				// ..., arr =>
				originalMethod.visitInsn(Opcodes.DUP);
				// ..., arr, arr =>
				originalMethod.visitInsn(Opcodes.ARRAYLENGTH);
				// ..., arr, len =>
				originalMethod.visitTypeInsn(Opcodes.ANEWARRAY, arrayType.getInternalName());
				// ..., arr, arr* =>
				originalMethod.visitInsn(Opcodes.DUP2);
				// ..., arr, arr*, arr, arr* =>
				originalMethod.visitInsn(Opcodes.SWAP);
				// ..., arr, arr*, arr*, arr =>
				originalMethod.visitInsn(Opcodes.DUP);
				// ..., arr, arr*, arr*, arr, arr =>
				originalMethod.visitInsn(Opcodes.ARRAYLENGTH);
				// ..., arr, arr*, arr*, arr, i (len) =>
				originalMethod.visitLabel(l3);
				// ..., arr, arr*, arr*, arr, i =>
				originalMethod.visitInsn(Opcodes.DUP);
				// ..., arr, arr*, arr*, arr, i, i =>
				originalMethod.visitJumpInsn(Opcodes.IFGT, l1);
				// ..., arr, arr*, arr*, arr, i =>
				originalMethod.visitInsn(Opcodes.POP2);
				// ..., arr, arr*, arr* =>
				originalMethod.visitInsn(Opcodes.POP);
				// ..., arr, arr* =>
				originalMethod.visitInsn(Opcodes.SWAP);
				// ..., arr*, arr =>
				originalMethod.visitInsn(Opcodes.POP);
				// ..., arr* =>
				originalMethod.visitJumpInsn(Opcodes.GOTO, l2);
				// ..., arr* =>
				originalMethod.visitLabel(l1);
				// ..., arr, arr*, arr*, arr, i =>
				originalMethod.visitInsn(Opcodes.ICONST_1);
				// ..., arr, arr*, arr*, arr, i, 1 =>
				originalMethod.visitInsn(Opcodes.ISUB);
				// ..., arr, arr*, arr*, arr, i-1 =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, arr*, i-1, arr*, arr, i-1 =>
				originalMethod.visitInsn(Opcodes.DUP_X1);
				// ..., arr, arr*, i-1, arr*, i-1, arr, i-1 =>
				originalMethod.visitTypeInsn(Opcodes.NEW, arrayType.getInternalName());
				// ..., arr, arr*, i-1, arr*, i-1, arr, i-1, obj =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, arr*, i-1, arr*, i-1, obj, arr, i-1, obj =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, arr*, i-1, arr*, i-1, obj, obj, arr, i-1, obj =>
				originalMethod.visitInsn(Opcodes.POP);
				// ..., arr, arr*, i-1, arr*, i-1, obj, obj, arr, i-1 =>
				if (typeIsArray)
					originalMethod.visitInsn(Opcodes.ACONST_NULL);
				// ..., arr, arr*, i-1, arr*, i-1, obj, obj, arr, i-1, (null) =>
				originalMethod.visitMethodInsn(Opcodes.INVOKESPECIAL, arrayType.getInternalName(), "<init>",
						ctorSignature);
				// ..., arr, arr*, i-1, arr*, i-1, obj =>
				originalMethod.visitInsn(Opcodes.AASTORE);
				// ..., arr, arr*, i-1 =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., i-1, arr, arr*, i-1 =>
				originalMethod.visitInsn(Opcodes.POP);
				// ..., i-1, arr, arr* =>
				originalMethod.visitInsn(Opcodes.SWAP);
				// ..., i-1, arr*, arr =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, i-1, arr*, arr =>
				originalMethod.visitInsn(Opcodes.SWAP);
				// ..., arr, i-1, arr, arr* =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, arr*, i-1, arr, arr* =>
				originalMethod.visitInsn(Opcodes.DUP_X2);
				// ..., arr, arr*, arr*, i-1, arr, arr* =>
				originalMethod.visitInsn(Opcodes.POP);
				// ..., arr, arr*, arr*, i-1, arr =>
				originalMethod.visitInsn(Opcodes.SWAP);
				// ..., arr, arr*, arr*, arr, i-1 =>
				originalMethod.visitJumpInsn(Opcodes.GOTO, l3);
				// ..., arr, arr*, arr*, arr, i-1 =>
				originalMethod.visitLabel(l2);
				// ..., arr* =>

				/*
				 * Exactly the same code as above.
				 */
				if (originalCopyMethod != null) {
					disableDuplicateInstrumentation(true);

					l1 = new Label();
					l2 = new Label();
					l3 = new Label();

					copyMethod.visitInsn(Opcodes.DUP);
					copyMethod.visitInsn(Opcodes.ARRAYLENGTH);
					copyMethod.visitTypeInsn(Opcodes.ANEWARRAY, arrayType.getInternalName());
					copyMethod.visitInsn(Opcodes.DUP2);
					copyMethod.visitInsn(Opcodes.SWAP);
					copyMethod.visitInsn(Opcodes.DUP);
					copyMethod.visitInsn(Opcodes.ARRAYLENGTH);
					copyMethod.visitLabel(l3);
					copyMethod.visitInsn(Opcodes.DUP);
					copyMethod.visitJumpInsn(Opcodes.IFGT, l1);
					copyMethod.visitInsn(Opcodes.POP2);
					copyMethod.visitInsn(Opcodes.POP);
					copyMethod.visitInsn(Opcodes.SWAP);
					copyMethod.visitInsn(Opcodes.POP);
					copyMethod.visitJumpInsn(Opcodes.GOTO, l2);
					copyMethod.visitLabel(l1);
					copyMethod.visitInsn(Opcodes.ICONST_1);
					copyMethod.visitInsn(Opcodes.ISUB);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.DUP_X1);
					copyMethod.visitTypeInsn(Opcodes.NEW, arrayType.getInternalName());
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.POP);
					if (typeIsArray)
						copyMethod.visitInsn(Opcodes.ACONST_NULL);
					copyMethod.visitMethodInsn(Opcodes.INVOKESPECIAL, arrayType.getInternalName(), "<init>",
							ctorSignature);
					copyMethod.visitInsn(Opcodes.AASTORE);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.POP);
					copyMethod.visitInsn(Opcodes.SWAP);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.SWAP);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.DUP_X2);
					copyMethod.visitInsn(Opcodes.POP);
					copyMethod.visitInsn(Opcodes.SWAP);
					copyMethod.visitJumpInsn(Opcodes.GOTO, l3);
					copyMethod.visitLabel(l2);

					disableDuplicateInstrumentation(false);
				}

				return;
			} else if (opcode == Opcodes.CHECKCAST) {
				if (type.lastIndexOf("[") == -1)
					type = "L" + type + ";";

				Type t = Type.getType(type);

				if (t.getSort() == Type.ARRAY)
					t = ArrayUtil.getTxArrayArrayType(t);

				originalMethod.visitTypeInsn(opcode, t.getInternalName());
				if (originalCopyMethod != null)
					copyMethod.visitTypeInsn(opcode, t.getInternalName());

				return;
			}
		}

		originalMethod.visitTypeInsn(opcode, type);
		if (originalCopyMethod != null)
			copyMethod.visitTypeInsn(opcode, type);
	}

	public void visitVarInsn(int opcode, int var) {
		if (!ignore) {
			analyzerAdapter.visitVarInsn(opcode, var);
		}
		originalMethod.visitVarInsn(opcode, var);
		if (originalCopyMethod != null) {
			copyMethod.visitVarInsn(opcode, var);
		}
	}

	private Label[] getCopyLabels(Label[] labels) {
		Label[] copyLabels = new Label[labels.length];
		for (int i = 0; i < labels.length; ++i) {
			copyLabels[i] = getLabel(labels[i]);
		}
		return copyLabels;
	}

	private Label getLabel(Label label) {
		Label duplicateLabel = labelMap.get(label);
		if (duplicateLabel == null) {
			duplicateLabel = new Label();
			labelMap.put(label, duplicateLabel);
		}
		return duplicateLabel;
	}
}
