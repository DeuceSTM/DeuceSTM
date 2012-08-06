package org.deuce.transform.inplacemetadata;

import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.objectweb.asm.commons.Method;
import org.deuce.transaction.ContextDelegator;

public class ArrayUtil {

	public static Type getArrayType(String desc) {
		return Type.getType(desc.substring(desc.lastIndexOf("[") + 1));
	}

	public static Type getArrayType(Type t) {
		return getArrayType(t.getDescriptor());
	}

	public static int getArrayDim(String desc) {
		int i = 0;
		while (desc.charAt(i) == '[') {
			i++;
		}
		return i;
	}

	public static int getArrayDim(Type t) {
		return getArrayDim(t.getDescriptor());
	}

	public static Type getTxType(String desc) {
		Type t = getArrayType(desc);
		switch (t.getSort()) {
		case Type.INT:
			return Type.getType(ContextDelegator.getFieldIntClass());
		case Type.SHORT:
			return Type.getType(ContextDelegator.getFieldShortClass());
		case Type.LONG:
			return Type.getType(ContextDelegator.getFieldLongClass());
		case Type.BYTE:
			return Type.getType(ContextDelegator.getFieldByteClass());
		case Type.BOOLEAN:
			return Type.getType(ContextDelegator.getFieldBooleanClass());
		case Type.CHAR:
			return Type.getType(ContextDelegator.getFieldCharClass());
		case Type.FLOAT:
			return Type.getType(ContextDelegator.getFieldFloatClass());
		case Type.DOUBLE:
			return Type.getType(ContextDelegator.getFieldDoubleClass());
		default: // object and multiarrays
			return Type.getType(ContextDelegator.getFieldObjectClass());
		}
	}

	public static Type getTxArrayType(String desc) {
		Type t = getArrayType(desc);
		switch (t.getSort()) {
		case Type.INT:
			return Type.getType(ContextDelegator.getArrayIntClass());
		case Type.SHORT:
			return Type.getType(ContextDelegator.getArrayShortClass());
		case Type.LONG:
			return Type.getType(ContextDelegator.getArrayLongClass());
		case Type.BYTE:
			return Type.getType(ContextDelegator.getArrayByteClass());
		case Type.BOOLEAN:
			return Type.getType(ContextDelegator.getArrayBooleanClass());
		case Type.CHAR:
			return Type.getType(ContextDelegator.getArrayCharClass());
		case Type.FLOAT:
			return Type.getType(ContextDelegator.getArrayFloatClass());
		case Type.DOUBLE:
			return Type.getType(ContextDelegator.getArrayDoubleClass());
		default: // object and multiarrays
			return Type.getType(ContextDelegator.getArrayObjectClass());
		}
	}

	public static Type getTxArrayType(Type t) {
		return getTxArrayType(t.getDescriptor());
	}

	public static Type getTxArrayType(int opcode) {
		return getTxArrayType(getTypeFromNewarrayOpcode(opcode));
	}

	private static String getArrayDimDesc(int dim) {
		String res = "";
		for (int i = 0; i < dim; i++) {
			res += "[";
		}
		return res;
	}

	public static Type getTxArrayArrayType(String desc) {
		Type arrT = getTxArrayType(desc);
		int dim = getArrayDim(desc);
		return Type.getType(dim > 1 ? getArrayDimDesc(1) + Type.getType(ContextDelegator.getArrayObjectClass())
				: getArrayDimDesc(dim) + arrT.getDescriptor());
	}

	public static Type getTxArrayArrayType(Type t) {
		return getTxArrayArrayType(t.getDescriptor());
	}

	public static Type getArrayFromTxArray(Type t) {
		int dim = getArrayDim(t);
		Type tt = getArrayType(t);

		if (tt.equals(Type.getType(ContextDelegator.getArrayIntClass())))
			return Type.getType(getArrayDimDesc(dim) + Type.INT_TYPE.getDescriptor());

		else if (tt.equals(Type.getType(ContextDelegator.getArrayShortClass())))
			return Type.getType(getArrayDimDesc(dim) + Type.SHORT_TYPE.getDescriptor());

		else if (tt.equals(Type.getType(ContextDelegator.getArrayLongClass())))
			return Type.getType(getArrayDimDesc(dim) + Type.LONG_TYPE.getDescriptor());

		else if (tt.equals(Type.getType(ContextDelegator.getArrayByteClass())))
			return Type.getType(getArrayDimDesc(dim) + Type.BYTE_TYPE.getDescriptor());

		else if (tt.equals(Type.getType(ContextDelegator.getArrayBooleanClass())))
			return Type.getType(getArrayDimDesc(dim) + Type.BOOLEAN_TYPE.getDescriptor());

		else if (tt.equals(Type.getType(ContextDelegator.getArrayCharClass())))
			return Type.getType(getArrayDimDesc(dim) + Type.CHAR_TYPE.getDescriptor());

		else if (tt.equals(Type.getType(ContextDelegator.getArrayFloatClass())))
			return Type.getType(getArrayDimDesc(dim) + Type.FLOAT_TYPE.getDescriptor());

		else if (tt.equals(Type.getType(ContextDelegator.getArrayDoubleClass())))
			return Type.getType(getArrayDimDesc(dim) + Type.DOUBLE_TYPE.getDescriptor());

		else
			// object or array type: impossible to know the source type
			return null;
	}

	public static Type getTypeFromTxArray(Type t) {
		if (t.equals(Type.getType(ContextDelegator.getArrayIntClass())))
			return Type.getType(Type.INT_TYPE.getDescriptor());

		else if (t.equals(Type.getType(ContextDelegator.getArrayShortClass())))
			return Type.getType(Type.SHORT_TYPE.getDescriptor());

		else if (t.equals(Type.getType(ContextDelegator.getArrayLongClass())))
			return Type.getType(Type.LONG_TYPE.getDescriptor());

		else if (t.equals(Type.getType(ContextDelegator.getArrayByteClass())))
			return Type.getType(Type.BYTE_TYPE.getDescriptor());

		else if (t.equals(Type.getType(ContextDelegator.getArrayBooleanClass())))
			return Type.getType(Type.BOOLEAN_TYPE.getDescriptor());

		else if (t.equals(Type.getType(ContextDelegator.getArrayCharClass())))
			return Type.getType(Type.CHAR_TYPE.getDescriptor());

		else if (t.equals(Type.getType(ContextDelegator.getArrayFloatClass())))
			return Type.getType(Type.FLOAT_TYPE.getDescriptor());

		else if (t.equals(Type.getType(ContextDelegator.getArrayDoubleClass())))
			return Type.getType(Type.DOUBLE_TYPE.getDescriptor());

		else
			// object or array type: impossible to know the source type
			return null;
	}

	public static Type getTypeFromNewarrayOpcode(int opcode) {
		switch (opcode) {
		case Opcodes.T_BOOLEAN:
			return Type.BOOLEAN_TYPE;
		case Opcodes.T_CHAR:
			return Type.CHAR_TYPE;
		case Opcodes.T_FLOAT:
			return Type.FLOAT_TYPE;
		case Opcodes.T_DOUBLE:
			return Type.DOUBLE_TYPE;
		case Opcodes.T_BYTE:
			return Type.BYTE_TYPE;
		case Opcodes.T_SHORT:
			return Type.SHORT_TYPE;
		case Opcodes.T_INT:
			return Type.INT_TYPE;
		case Opcodes.T_LONG:
			return Type.LONG_TYPE;
		default:
			return null;
		}
	}

	public static Method updateMethodArrayArgumentsAndReturn(Method m) {

		Type[] arguments = m.getArgumentTypes();
		Type[] newArguments = new Type[arguments.length];
		System.arraycopy(arguments, 0, newArguments, 0, arguments.length);

		for (int i = 0; i < newArguments.length; i++) {
			if (newArguments[i].getSort() == Type.ARRAY) {
				newArguments[i] = getTxArrayArrayType(newArguments[i]);
			}
		}

		Type newReturn = m.getReturnType();
		if (newReturn.getSort() == Type.ARRAY) {
			newReturn = getTxArrayArrayType(newReturn);
		}

		return new Method(m.getName(), newReturn, newArguments);
	}

	public static Method updateMethodArrayArgumentsAndReturn(String desc) {
		return updateMethodArrayArgumentsAndReturn(new Method("<dummy>", desc));
	}

	public static int getArrayTypeOpcode(Type t) {
		switch (t.getSort()) {
		case Type.INT:
			return Opcodes.T_INT;
		case Type.SHORT:
			return Opcodes.T_SHORT;
		case Type.BYTE:
			return Opcodes.T_BYTE;
		case Type.BOOLEAN:
			return Opcodes.T_BOOLEAN;
		case Type.FLOAT:
			return Opcodes.T_FLOAT;
		case Type.CHAR:
			return Opcodes.T_CHAR;
		case Type.DOUBLE:
			return Opcodes.T_DOUBLE;
		case Type.LONG:
			return Opcodes.T_LONG;
		default:
			return -1;
		}
	}
}
