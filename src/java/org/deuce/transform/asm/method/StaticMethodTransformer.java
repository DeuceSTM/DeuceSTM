package org.deuce.transform.asm.method;

import java.util.List;

import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.transform.asm.Field;

public class StaticMethodTransformer extends MethodAdapter {

	final static public String CLASS_BASE = "__CLASS_BASE__";
	
	private final List<Field> fields;
	private final String className;
	

	public StaticMethodTransformer(MethodVisitor mv, List<Field> fields, String className) {
		super(mv);
		this.fields = fields;
		this.className = className;
	}

	@Override
	public void visitCode() {
		for( Field field : fields)
			addField( field);

		addClassBase();
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

	private void addClassBase() {
		super.visitLdcInsn(Type.getObjectType(className));
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/deuce/reflection/AddressUtil",
				"staticFieldBase", "(Ljava/lang/Class;)Ljava/lang/Object;");
		super.visitFieldInsn(Opcodes.PUTSTATIC, className, CLASS_BASE, "Ljava/lang/Object;");
	}

}
