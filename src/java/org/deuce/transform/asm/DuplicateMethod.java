package org.deuce.transform.asm;

import org.deuce.objectweb.asm.Label;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.objectweb.asm.commons.AnalyzerAdapter;
import org.deuce.objectweb.asm.commons.Method;
import org.deuce.transaction.Context;
import org.deuce.transaction.ContextDelegator;
import org.deuce.transform.util.Util;

public class DuplicateMethod extends MethodAdapter{

	final static public String LOCAL_VARIBALE_NAME = "__transactionContext__";

	private Label firstLabel;
	private Label lastLabel;
	private final int argumentsSize;
	
	private boolean addContextToTable = false;
	
	private AnalyzerAdapter analyzerAdapter;

	private final String className;

	public DuplicateMethod( String className, MethodVisitor mv,
			boolean isstatic, Method newMethod) {
		super(mv);
		this.className = className;
		this.argumentsSize = calcArgumentsSize( isstatic, newMethod); 
	}
	
	public void setAnalyzer(AnalyzerAdapter analyzerAdapter) {
		this.analyzerAdapter = analyzerAdapter;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) 
	{
	//if( owner.startsWith("java") || owner.startsWith("sun"))
		if( Agent.IGNORE_TREE.contains( owner))
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
		if( Agent.IGNORE_TREE.contains( owner) || 
				name.contains("$")){ // Syntactic TODO remove this limitation
			super.visitFieldInsn(opcode, owner, name, desc); // ... = foo( ...
			return;
		}
		final Type type = Type.getType(desc);
		switch( opcode) {
		case Opcodes.GETFIELD:  //	ALOAD 0: this (stack status)
			
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, className,
					(name +AccessorsAdder.GETTER_ENDING), AccessorsAdder.instanceGetterDesc(type, className));
			
			break;
		case Opcodes.PUTFIELD:
			
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, className,
					(name +AccessorsAdder.SETTER_ENDING), AccessorsAdder.instanceSetterDesc(type, className));
			
			break;
		case Opcodes.GETSTATIC: // check support for static fields
			
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, className,
					(name +AccessorsAdder.GETTER_ENDING), AccessorsAdder.staticGetterDesc(type));
			
			break;
		case Opcodes.PUTSTATIC:
			
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, className,
					(name +AccessorsAdder.SETTER_ENDING), AccessorsAdder.staticSetterDesc(type));
			break;
		default:
			super.visitFieldInsn(opcode, owner, name, desc);
		}
	}

	private void addBeforeReadCall(String owner, String name) {
		super.visitInsn(Opcodes.DUP);
		super.visitFieldInsn( Opcodes.GETSTATIC, owner, Util.getAddressField(name) , "J");
		super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
		super.visitMethodInsn( Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
				ContextDelegator.BEFORE_READ_METHOD_NAME, ContextDelegator.BEFORE_READ_METHOD_DESC);
	}
	
	/**
	 * Adds for each array cell visited a call to the context
	 */
	@Override
	public void visitInsn(int opcode) {
		boolean load = false;
		boolean store = false;
		String desc = null;
		String arrayMemeberType = null;
		switch( opcode) {
		
		case Opcodes.AALOAD:
			// handle Object[] arrays type, the type before the last is the array. 
			// The substring removes the '[' from the array type
			String arrayType = 
				(String)this.analyzerAdapter.stack.get(this.analyzerAdapter.stack.size() - 2);
			arrayMemeberType = getArrayMemberType( arrayType);
			
			desc = ContextDelegator.READ_ARRAY_METHOD_OBJ_DESC;
			load = true;
			break;
		case Opcodes.BALOAD:
			desc = ContextDelegator.READ_ARRAY_METHOD_BYTE_DESC;
			load = true;
			break;
		case Opcodes.CALOAD:
			desc = ContextDelegator.READ_ARRAY_METHOD_CHAR_DESC;
			load = true;
			break;
		case Opcodes.SALOAD:
			desc = ContextDelegator.READ_ARRAY_METHOD_SHORT_DESC;
			load = true;
			break;
		case Opcodes.IALOAD:
			desc = ContextDelegator.READ_ARRAY_METHOD_INT_DESC;
			load = true;
			break;
		case Opcodes.LALOAD:
			desc = ContextDelegator.READ_ARRAY_METHOD_LONG_DESC;
			load = true;
			break;
		case Opcodes.FALOAD:
			desc = ContextDelegator.READ_ARRAY_METHOD_FLOAT_DESC;
			load = true;
			break;
		case Opcodes.DALOAD:
			desc = ContextDelegator.READ_ARRAY_METHOD_DOUBLE_DESC;
			load = true;
			break;
			
		case Opcodes.AASTORE:
			desc = ContextDelegator.WRITE_ARRAY_METHOD_OBJ_DESC;
			store = true;
			break;
		case Opcodes.BASTORE:
			desc = ContextDelegator.WRITE_ARRAY_METHOD_BYTE_DESC;
			store = true;
			break;
		case Opcodes.CASTORE:
			desc = ContextDelegator.WRITE_ARRAY_METHOD_CHAR_DESC;
			store = true;
			break;
		case Opcodes.SASTORE:
			desc = ContextDelegator.WRITE_ARRAY_METHOD_SHORT_DESC;
			store = true;
			break;
		case Opcodes.IASTORE:
			desc = ContextDelegator.WRITE_ARRAY_METHOD_INT_DESC;
			store = true;
			break;
		case Opcodes.LASTORE:
			desc = ContextDelegator.WRITE_ARRAY_METHOD_LONG_DESC;
			store = true;
			break;
		case Opcodes.FASTORE:
			desc = ContextDelegator.WRITE_ARRAY_METHOD_FLOAT_DESC;
			store = true;
			break;
		case Opcodes.DASTORE:
			desc = ContextDelegator.WRITE_ARRAY_METHOD_DOUBLE_DESC;
			store = true;
			break;
		}
			
		if( load)
		{
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.READ_ARR_METHOD_NAME, desc);

			if( opcode == Opcodes.AALOAD){ // non primitive array need cast
				super.visitTypeInsn( Opcodes.CHECKCAST, arrayMemeberType);
			}
		}
		else if( store)
		{
			super.visitVarInsn(Opcodes.ALOAD, argumentsSize - 1); // load context
			super.visitMethodInsn( Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.WRITE_ARR_METHOD_NAME, desc);
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
			super.visitLocalVariable(LOCAL_VARIBALE_NAME, Context.CONTEXT_DESC, null,
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
	
	private String getArrayMemberType( String arrayType){
		if( arrayType.charAt(arrayType.length() - 1) == ';' && // primitive array 
				arrayType.charAt(1) != '[' ) // array of arrays  
			return arrayType.substring(2, arrayType.length()-1);

		return arrayType.substring(1, arrayType.length()); // array of Objects
	}
}
