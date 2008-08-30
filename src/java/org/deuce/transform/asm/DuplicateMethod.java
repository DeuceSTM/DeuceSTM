package org.deuce.transform.asm;

import org.deuce.objectweb.asm.Label;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.objectweb.asm.commons.Method;
import org.deuce.transaction.AbstractContext;
import org.deuce.transform.util.Util;

public class DuplicateMethod extends MethodAdapter{


	final static public String LOCAL_VARIBALE_NAME = "__transactionContext__";
	final static public String CONTEXT_DESCRIPTOR = Type.getDescriptor( AbstractContext.class);

	private Label firstLabel;
	private Label lastLabel;
	private final int argumentsSize;
	
	private boolean addContextToTable = false;

	public DuplicateMethod(MethodVisitor mv, boolean isstatic, Method newMethod) {
		super(mv);
		this.argumentsSize = calcArgumentsSize( isstatic, newMethod); 
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) 
	{
		if( owner.startsWith("java") || owner.startsWith("sun"))
		{
			super.visitMethodInsn(opcode, owner, name, desc); // ... = foo( ...
		}
		else
		{
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			Method newMethod = ClassTransformer.createNewMethod(name, desc);
			super.visitMethodInsn(opcode, owner, name, newMethod.getDescriptor()); // ... = foo( ...
		}
	}
	
	/**
	 * Adds for each field visited a call to the context.
	 */
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		if( owner.startsWith("java") || owner.startsWith("sun")) // TODO remove this limitation
		{
			super.visitFieldInsn(opcode, owner, name, desc); // ... = foo( ...
			return;
		}
		switch( opcode) {
		case Opcodes.GETFIELD:  //	ALOAD 0: this (stack status)
			super.visitInsn(Opcodes.DUP);
			super.visitFieldInsn(opcode, owner, name, desc);
			super.visitFieldInsn( Opcodes.GETSTATIC, owner, Util.getAddressField(name) , "J");
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME,
					AbstractContext.READ_METHOD_NAME, AbstractContext.getReadMethodDesc(desc));
			
			Type type = Type.getType(desc);
			if( type.getSort() >= Type.ARRAY) // non primitive
				super.visitTypeInsn( Opcodes.CHECKCAST, Type.getType(desc).getInternalName());
			break;
		case Opcodes.PUTFIELD:
			super.visitFieldInsn( Opcodes.GETSTATIC, owner, Util.getAddressField(name) , "J");
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME,
					AbstractContext.WRITE_METHOD_NAME, AbstractContext.getWriteMethodDesc(desc));
			break;
		case Opcodes.GETSTATIC: // TODO check support for static fields
			super.visitFieldInsn(Opcodes.GETSTATIC, owner, 
					StaticMethodTransformer.CLASS_BASE, "Ljava/lang/Object;");
			super.visitFieldInsn(opcode, owner, name, desc);
			super.visitFieldInsn( Opcodes.GETSTATIC, owner, Util.getAddressField(name) , "J");
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME,
					AbstractContext.READ_METHOD_NAME, AbstractContext.getReadMethodDesc(desc));
			
			type = Type.getType(desc);
			if( type.getSort() >= Type.ARRAY) // non primitive
				super.visitTypeInsn( Opcodes.CHECKCAST, Type.getType(desc).getInternalName());
			break;
		case Opcodes.PUTSTATIC:
			super.visitFieldInsn(Opcodes.GETSTATIC, owner, 
					StaticMethodTransformer.CLASS_BASE, "Ljava/lang/Object;");
			super.visitFieldInsn( Opcodes.GETSTATIC, owner, Util.getAddressField(name) , "J");
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME,
					AbstractContext.STATIC_WRITE_METHOD_NAME, AbstractContext.getStaticWriteMethodDesc(desc));
			break;
		default:
			super.visitFieldInsn(opcode, owner, name, desc);
		}
	}
	
	/**
	 * Adds for each array cell visited a call to the context
	 */
	@Override
	public void visitInsn(int opcode) {
		boolean load = false;
		boolean store = false;
		String desc = null;
		switch( opcode) {
		
		case Opcodes.AALOAD:
			// TODO handle Object[] arrays
//			desc = AbstractContext.READ_ARRAY_METHOD_OBJ_DESC;
//			load = true;
			break;
		case Opcodes.BALOAD:
			desc = AbstractContext.READ_ARRAY_METHOD_BYTE_DESC;
			load = true;
			break;
		case Opcodes.CALOAD:
			desc = AbstractContext.READ_ARRAY_METHOD_CHAR_DESC;
			load = true;
			break;
		case Opcodes.SALOAD:
			desc = AbstractContext.READ_ARRAY_METHOD_SHORT_DESC;
			load = true;
			break;
		case Opcodes.IALOAD:
			desc = AbstractContext.READ_ARRAY_METHOD_INT_DESC;
			load = true;
			break;
		case Opcodes.LALOAD:
			desc = AbstractContext.READ_ARRAY_METHOD_LONG_DESC;
			load = true;
			break;
		case Opcodes.FALOAD:
			desc = AbstractContext.READ_ARRAY_METHOD_FLOAT_DESC;
			load = true;
			break;
		case Opcodes.DALOAD:
			desc = AbstractContext.READ_ARRAY_METHOD_DOUBLE_DESC;
			load = true;
			break;
			
		case Opcodes.AASTORE:
			desc = AbstractContext.WRITE_ARRAY_METHOD_OBJ_DESC;
			store = true;
			break;
		case Opcodes.BASTORE:
			desc = AbstractContext.WRITE_ARRAY_METHOD_BYTE_DESC;
			store = true;
			break;
		case Opcodes.CASTORE:
			desc = AbstractContext.WRITE_ARRAY_METHOD_CHAR_DESC;
			store = true;
			break;
		case Opcodes.SASTORE:
			desc = AbstractContext.WRITE_ARRAY_METHOD_SHORT_DESC;
			store = true;
			break;
		case Opcodes.IASTORE:
			desc = AbstractContext.WRITE_ARRAY_METHOD_INT_DESC;
			store = true;
			break;
		case Opcodes.LASTORE:
			desc = AbstractContext.WRITE_ARRAY_METHOD_LONG_DESC;
			store = true;
			break;
		case Opcodes.FASTORE:
			desc = AbstractContext.WRITE_ARRAY_METHOD_FLOAT_DESC;
			store = true;
			break;
		case Opcodes.DASTORE:
			desc = AbstractContext.WRITE_ARRAY_METHOD_DOUBLE_DESC;
			store = true;
			break;
		}
			
		if( load)
		{
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME,
					AbstractContext.READ_ARR_METHOD_NAME, desc);
			// TODO handle Object[] arrays
//			if( opcode == Opcodes.AALOAD) // non primitive
//				super.visitTypeInsn( Opcodes.CHECKCAST, Type.getType(desc).getInternalName());
		}
		else if( store)
		{
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, AbstractContext.ABSTRACT_CONTEXT_NAME,
					AbstractContext.WRITE_ARR_METHOD_NAME, desc);
		}
		else{
			super.visitInsn(opcode);
		}
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		super.visitIincInsn( newIndex(var), increment); // increase index due to context
	}

	@Override
	public void visitLabel(Label label) {
		if( firstLabel == null)
			firstLabel = label;
		lastLabel = label;
		super.visitLabel(label);
	}
// TODO handle methods with no arguments
	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start,
			Label end, int index) {
		if( this.argumentsSize >  index + 1) // argument
		{
			super.visitLocalVariable(name, desc, signature, start, end, index); // non static method has this
			return;
		}
		// add context as last argument
		// the first local variable and was never added before
		if( this.argumentsSize ==  index + 1 && !addContextToTable) 
		{
			addContextToTable = true;
			super.visitLocalVariable(LOCAL_VARIBALE_NAME, CONTEXT_DESCRIPTOR, null,
					firstLabel, lastLabel, index);
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
	 * Calculate the new local index according to its position.
	 * If it's not a function argument (local variable) its index increased by 1.
	 * @param currIndex current index
	 * @return new index
	 */
	private int newIndex( int currIndex){
		return currIndex + 1 < this.argumentsSize ? currIndex : currIndex + 1;
	}
	
	private int calcArgumentsSize( boolean isStatic, Method newMethod){
		int size = isStatic ? 0 : 1; // if not static "this" is the first argument
		for( Type type : newMethod.getArgumentTypes()){
			size += type.getSize();
		}
		return size;
	}
}
