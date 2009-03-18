package org.deuce.transform.asm;

import java.util.List;

import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;

public class StaticMethodTransformer extends MethodAdapter {

	final static public String CLASS_BASE = "__CLASS_BASE__";

	private final List<Field> fields;
	private final String className;
	private final Field staticField;

	public StaticMethodTransformer(MethodVisitor mv, List<Field> fields, String className, Field staticField) {
		super(mv);
		this.fields = fields;
		this.className = className;
		this.staticField = staticField;
	}

	@Override
	public void visitCode() {
		for( Field field : fields)
			addField( field);

		if( staticField != null)
			addClassBase(staticField);
	}

	private void addField( Field field) {
		super.visitLdcInsn(Type.getObjectType(className));
		super.visitLdcInsn(field.getFieldName());
		super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField",
		"(Ljava/lang/String;)Ljava/lang/reflect/Field;");
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/deuce/reflection/AddressUtil",
				"getAddress", "(Ljava/lang/reflect/Field;)J");
		super.visitFieldInsn(Opcodes.PUTSTATIC, className, field.getFieldNameAddress(), "J");
	}

	private void addClassBase(Field staticField) {
		super.visitLdcInsn(Type.getObjectType(className));
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/deuce/reflection/AddressUtil",
				"staticFieldBase", "(Ljava/lang/Class;)Ljava/lang/Object;");
		super.visitFieldInsn(Opcodes.PUTSTATIC, className, CLASS_BASE, "Ljava/lang/Object;");
	}

}
