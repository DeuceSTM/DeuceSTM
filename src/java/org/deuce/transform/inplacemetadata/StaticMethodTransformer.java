package org.deuce.transform.inplacemetadata;

import java.util.List;

import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;

public class StaticMethodTransformer extends MethodAdapter {

	final static public String CLASS_BASE = "__CLASS_BASE__";

	private final List<Field> fields;
	private final String className;
	private final MethodVisitor staticMethod;
	private final String fieldsHolderName;
	private final String staticField;

	public StaticMethodTransformer(MethodVisitor mv, MethodVisitor staticMethod, List<Field> fields,
			String staticField, String className, String fieldsHolderName) {
		super(mv);
		this.staticMethod = staticMethod;
		this.fields = fields;
		this.staticField = staticField;
		this.className = className;
		this.fieldsHolderName = fieldsHolderName;
	}

	@Override
	public void visitCode() {
		((MethodTransformer) mv).disableMethodInstrumentation(true);
		if (staticField != null) {
			addClassBase(staticField);
		}

		if (fields.size() > 0) {
			for (Field field : fields) {
				if ((field.getAccess() & Opcodes.ACC_STATIC) != 0) {
					addField(field);
				} else {
					addField2(field);
				}
			}
		}
		((MethodTransformer) mv).disableMethodInstrumentation(false);
	}

	private void addField2(Field field) {
		super.visitLdcInsn(Type.getObjectType(className));
		super.visitLdcInsn(field.getFieldName());
		super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField",
				"(Ljava/lang/String;)Ljava/lang/reflect/Field;");
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/deuce/reflection/AddressUtil", "getAddress",
				"(Ljava/lang/reflect/Field;)J");

		super.visitFieldInsn(Opcodes.PUTSTATIC, fieldsHolderName, "__STATIC__" + field.getFieldNameAddress(), "J");

	}

	private void addField(Field field) {
		/*
		 * Example for class cA { int mA; }
		 * 
		 * (...)
		 * 
		 * cA inst = new cA(); <- during clinit
		 * 
		 * Concept code: cAFieldHolder.mA__ADDRESS__ = new TxField(inst,
		 * AddressUtil.getAddress(java.lang.Class<cA>.getDeclaredField("mA")));
		 */
		// stack: ... =>
		super.visitTypeInsn(Opcodes.NEW, field.getType().getInternalName());
		// stack: ..., TxField =>
		super.visitInsn(Opcodes.DUP);
		// stack: ..., TxField, TxField =>
		super.visitFieldInsn(Opcodes.GETSTATIC, fieldsHolderName, CLASS_BASE, "Ljava/lang/Object;");
		// stack: ..., TxField, TxField, Object =>
		super.visitLdcInsn(Type.getObjectType(this.className));
		// stack: ..., TxField, TxField, Object, Class (owner) =>
		super.visitLdcInsn(field.getFieldName());
		// stack: ..., TxField, TxField, Object, Class (owner), String (this
		// field) =>
		super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField",
				"(Ljava/lang/String;)Ljava/lang/reflect/Field;");
		// stack: ..., TxField, TxField, Object, Field =>
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/deuce/reflection/AddressUtil", "getAddress",
				"(Ljava/lang/reflect/Field;)J");
		// stack: ..., TxField, TxField, Object, long (this field's address) =>
		super.visitMethodInsn(Opcodes.INVOKESPECIAL, field.getType().getInternalName(), "<init>",
				"(Ljava/lang/Object;J)V");
		// stack: ..., TxField =>
		super.visitFieldInsn(Opcodes.PUTSTATIC, fieldsHolderName, field.getFieldNameAddress(), field.getType()
				.getDescriptor());
		// stack: ... =>
	}

	private void addClassBase(String staticFieldBase) {
		super.visitLdcInsn(Type.getObjectType(className));
		super.visitLdcInsn(staticFieldBase);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/deuce/reflection/AddressUtil", "staticFieldBase",
				"(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;");
		super.visitFieldInsn(Opcodes.PUTSTATIC, fieldsHolderName, CLASS_BASE, "Ljava/lang/Object;");
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(maxStack, maxLocals);
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
		// TODO can we do it cleaner?
		if (staticMethod != null && super.mv != staticMethod)
			staticMethod.visitEnd();
	}
}
