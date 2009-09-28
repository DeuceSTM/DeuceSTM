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
	private final MethodVisitor staticMethod;
	

	public StaticMethodTransformer(MethodVisitor mv, MethodVisitor staticMethod, List<Field> fields, String className) {
		super(mv);
		this.staticMethod = staticMethod;
		this.fields = fields;
		this.className = className;
	}

	@Override
	public void visitCode() {
		if(fields.size() > 0){
			for( Field field : fields)
				addField( field);

			addClassBase(fields.get(0).getFieldNameAddress());
		}
	}

	private void addField( Field field) {
		staticMethod.visitLdcInsn(Type.getObjectType(className));
		staticMethod.visitLdcInsn(field.getFieldName());
		staticMethod.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField",
		"(Ljava/lang/String;)Ljava/lang/reflect/Field;");
		staticMethod.visitMethodInsn(Opcodes.INVOKESTATIC, "org/deuce/reflection/AddressUtil",
				"getAddress", "(Ljava/lang/reflect/Field;)J");
		staticMethod.visitFieldInsn(Opcodes.PUTSTATIC, className, field.getFieldNameAddress(), "J");
	}

	private void addClassBase(String staticFieldBase) {
		staticMethod.visitLdcInsn(Type.getObjectType(className));
		mv.visitLdcInsn(staticFieldBase);
		staticMethod.visitMethodInsn(Opcodes.INVOKESTATIC, "org/deuce/reflection/AddressUtil",
				"staticFieldBase", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;");
		staticMethod.visitFieldInsn(Opcodes.PUTSTATIC, className, CLASS_BASE, "Ljava/lang/Object;");
	}

}
