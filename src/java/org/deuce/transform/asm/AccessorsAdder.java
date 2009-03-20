package org.deuce.transform.asm;

import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.transaction.Context;
import org.deuce.transaction.ContextDelegator;
import org.deuce.transform.asm.type.TypeCodeResolver;
import org.deuce.transform.asm.type.TypeCodeResolverFactory;
import org.deuce.transform.util.Util;

/**
 * Add "setters"/"getters".
 * These accessors monitor each field access and call the ContextDelegator.
 * 
 * @author Guy Korland
 */
public class AccessorsAdder {
	
	public final static String SETTER_ENDING = "___deuceSetter$";
	public final static String GETTER_ENDING = "___deuceGetter$";

	final private static Type CONTEXT_TYPE = Type.getType( Context.class);

	private final ByteCodeVisitor visitor;
	public AccessorsAdder(ByteCodeVisitor visitor){
		this.visitor = visitor;
	}

	public String addSetter(int access, String name, String desc) {

		boolean isFinal  = (access & Opcodes.ACC_FINAL) != 0;
		if( (access & Opcodes.ACC_STATIC) != 0)
			return addSaticSetter(name, desc, isFinal);

		return addInstanceSetter(name, desc, isFinal);
	}

	private String addSaticSetter(String name, String desc, boolean isFinal) {

		final Type type = Type.getType(desc);
		final TypeCodeResolver resolver = TypeCodeResolverFactory.getReolver(type);
		final String setterName = name + SETTER_ENDING;

		String methodDescriptor = staticSetterDesc(type);

		MethodVisitor mv = visitor.createMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				setterName, methodDescriptor, null, null);

		mv.visitCode();

		if( isFinal){
			mv.visitVarInsn(resolver.loadCode(), 0); // load value
			mv.visitFieldInsn( Opcodes.PUTSTATIC, visitor.getClassName(), name , desc);
		}
		else{
			//(Ljava/lang/Object;Ljava/lang/Object;J" + Context.CONTEXT_DESC +")V
			// ContextDelegator.addStaticWriteAccess( value, MyClass.CLASS_BASE, fieldOffset ,context);
			mv.visitVarInsn(resolver.loadCode(), 0); // load value
			mv.visitFieldInsn(Opcodes.GETSTATIC, visitor.getClassName(), 
					StaticMethodTransformer.CLASS_BASE, "Ljava/lang/Object;"); 
			mv.visitFieldInsn( Opcodes.GETSTATIC, visitor.getClassName(), Util.getAddressField(name) , "J");
			mv.visitVarInsn(Opcodes.ALOAD, resolver.localSize()); // load context
			mv.visitMethodInsn( Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.STATIC_WRITE_METHOD_NAME, ContextDelegator.getStaticWriteMethodDesc(type));
		}
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(resolver.localSize(), 1+resolver.localSize()); // TODO fix it
		mv.visitEnd();

		return setterName;
	}

	public static String staticSetterDesc(final Type type) {
		return Type.getMethodDescriptor(Type.VOID_TYPE, 
				new Type[]{type, CONTEXT_TYPE}); // value,context

	}

	private String addInstanceSetter(String name, String desc, boolean isFinal) {

		final Type type = Type.getType(desc);
		final TypeCodeResolver resolver = TypeCodeResolverFactory.getReolver(type);
		final String setterName = name + SETTER_ENDING;

		String methodDescriptor = instanceSetterDesc(type, visitor.getClassName());

		MethodVisitor mv = visitor.createMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				setterName, methodDescriptor, null, null);

		mv.visitCode();

		if( isFinal){
			mv.visitVarInsn(Opcodes.ALOAD, 0); // load object
			mv.visitVarInsn(resolver.loadCode(), 1); // load value
			mv.visitFieldInsn( Opcodes.PUTFIELD, visitor.getClassName(), name , desc);
		}
		else{
			//"(Ljava/lang/Object;IJ" + Context.CONTEXT_DESC +")V";
			// ContextDelegetor.addWriteAccess( object, value, fieldOffset, context);
			mv.visitVarInsn(Opcodes.ALOAD, 0); // load object
			mv.visitVarInsn(resolver.loadCode(), 1); // load value
			mv.visitFieldInsn( Opcodes.GETSTATIC, visitor.getClassName(), Util.getAddressField(name) , "J");
			mv.visitVarInsn(Opcodes.ALOAD, resolver.localSize() + 1); // load context
			mv.visitMethodInsn( Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.WRITE_METHOD_NAME, ContextDelegator.getWriteMethodDesc(type));
		}
		mv.visitInsn(Opcodes.RETURN);
		
		mv.visitMaxs(1 + resolver.localSize(), 1 + resolver.localSize()); // TODO fix it
		mv.visitEnd();

		return setterName;
	}
	
	/**
	 * Owner,Value,Context 
	 */
	public static String instanceSetterDesc(final Type type, String ownerName) {
		return Type.getMethodDescriptor(Type.VOID_TYPE, 
				new Type[]{Type.getObjectType( ownerName), type, CONTEXT_TYPE}); // object,value,context
	}

	public String addGetter(int access, String name, String desc) {
		
		boolean isFinal  = (access & Opcodes.ACC_FINAL) != 0;

		if((access & Opcodes.ACC_STATIC) != 0){
			return addStaticGetter(name, desc, isFinal);
		}

		return addInstanceGetter(name, desc, isFinal);
	}

	/**
	 * static public MyValueClass field___deuceGetter$( Context context){
	 * 	ContextDelegetr.beforeReadAccess( MyClass.__CLASS_BASE__, fieldOffset, context);
	 * 	return (MyValueClass)ContextDelegetr.onReadAccess( MyClass.__CLASS_BASE__, MyClass.field, MyClass.field__ADDRESS__, context);
	 * }
	 * @param isFinal 
	 */
	private String addStaticGetter(String name, String desc, boolean isFinal) {

		final Type type = Type.getType(desc);
		final TypeCodeResolver resolver = TypeCodeResolverFactory.getReolver(type);
		final String getterName = name + GETTER_ENDING;

		String methodDescriptor = staticGetterDesc(type);

		MethodVisitor mv = visitor.createMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				getterName, methodDescriptor, null, null);
		
		mv.visitCode();

		if( isFinal){
			mv.visitFieldInsn(Opcodes.GETSTATIC, visitor.getClassName(), name, desc); // load value
		}
		else{
			// ContextDelegetr.beforeReadAccess( MyClass.__CLASS_BASE__, fieldOffset, context);		
			mv.visitFieldInsn(Opcodes.GETSTATIC, visitor.getClassName(), 
					StaticMethodTransformer.CLASS_BASE, "Ljava/lang/Object;");
			addBeforeReadCall(mv, visitor.getClassName(), name, 0);

			// return (MyClass)ContextDelegetr.onReadAccess( MyClass.__CLASS_BASE__, MyClass.field, fieldOffset, context);
			mv.visitFieldInsn(Opcodes.GETSTATIC, visitor.getClassName(), 
					StaticMethodTransformer.CLASS_BASE, "Ljava/lang/Object;");
			mv.visitFieldInsn(Opcodes.GETSTATIC, visitor.getClassName(), name, desc);
			mv.visitFieldInsn( Opcodes.GETSTATIC, visitor.getClassName(), Util.getAddressField(name) , "J");
			mv.visitVarInsn(Opcodes.ALOAD, 0); // load context
			mv.visitMethodInsn( Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.READ_METHOD_NAME, ContextDelegator.getReadMethodDesc(type));

			if( type.getSort() >= Type.ARRAY) // non primitive need to be cast back
				mv.visitTypeInsn( Opcodes.CHECKCAST, type.getInternalName());
		}
		mv.visitInsn(resolver.returnCode());
		mv.visitMaxs(resolver.localSize(), 1);
		mv.visitEnd();
		return getterName;
	}

	 public static String staticGetterDesc(final Type type) {
		return Type.getMethodDescriptor(type, new Type[]{CONTEXT_TYPE});
	}

	/**
	 * static public MyValueClass field___deuceGetter$( MyClass obj, Context context){
	 * 	ContextDelegetr.beforeReadAccess( obj, MyClass.field__ADDRESS__, context);
	 * 	return (MyValueClass)ContextDelegetr.onReadAccess( obj, obj.field, MyClass.field__ADDRESS__, context);
	 * }
	 * @param isFinal 
	 */
	private String addInstanceGetter(String name, String desc, boolean isFinal) {

		final Type type = Type.getType(desc);
		final TypeCodeResolver resolver = TypeCodeResolverFactory.getReolver(type);
		final String getterName = name + GETTER_ENDING;

		String methodDescriptor = instanceGetterDesc(type, visitor.getClassName());

		MethodVisitor mv = visitor.createMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				getterName, methodDescriptor, null, null);
		
		mv.visitCode();
		
		if( isFinal){
			mv.visitVarInsn(Opcodes.ALOAD, 0); // load obj
			mv.visitFieldInsn(Opcodes.GETFIELD, visitor.getClassName(), name, desc);
		}
		else{
			// ContextDelegetr.beforeReadAccess( obj, MyClass.field__ADDRESS__, context)
			mv.visitVarInsn(Opcodes.ALOAD, 0); // load this
			addBeforeReadCall(mv, visitor.getClassName(), name, 1);

			// return (MyValueClass)ContextDelegetr.onReadAccess( obj, obj.field, MyClass.field__ADDRESS__, context)
			mv.visitVarInsn(Opcodes.ALOAD, 0); // load obj
			mv.visitVarInsn(Opcodes.ALOAD, 0); // load obj
			mv.visitFieldInsn(Opcodes.GETFIELD, visitor.getClassName(), name, desc);
			mv.visitFieldInsn( Opcodes.GETSTATIC, visitor.getClassName(), Util.getAddressField(name) , "J");
			mv.visitVarInsn(Opcodes.ALOAD, 1); // load context
			mv.visitMethodInsn( Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
					ContextDelegator.READ_METHOD_NAME, ContextDelegator.getReadMethodDesc(type));
			if( type.getSort() >= Type.ARRAY) // non primitive need to be cast back
				mv.visitTypeInsn( Opcodes.CHECKCAST, type.getInternalName());
		}
		mv.visitInsn(resolver.returnCode());
		mv.visitMaxs(10, 10); // TODO calc this
		mv.visitEnd();
		return getterName;
	}

	public static String instanceGetterDesc(final Type type, String ownerName) {
		return Type.getMethodDescriptor(type, new Type[]{Type.getObjectType( ownerName),CONTEXT_TYPE});
	}

	private void addBeforeReadCall(MethodVisitor mv, String owner, String name, int contextIndex) {
		mv.visitFieldInsn( Opcodes.GETSTATIC, owner, Util.getAddressField(name) , "J");
		mv.visitVarInsn(Opcodes.ALOAD, contextIndex); // load context
		mv.visitMethodInsn( Opcodes.INVOKESTATIC, ContextDelegator.CONTEXT_DELEGATOR_INTERNAL,
				ContextDelegator.BEFORE_READ_METHOD_NAME, ContextDelegator.BEFORE_READ_METHOD_DESC);
	}
}
