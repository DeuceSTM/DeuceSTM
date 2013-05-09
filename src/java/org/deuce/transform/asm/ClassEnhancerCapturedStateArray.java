package org.deuce.transform.asm;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.deuce.Atomic;
import org.deuce.Irrevocable;
import org.deuce.objectweb.asm.AnnotationVisitor;
import org.deuce.objectweb.asm.ClassAdapter;
import org.deuce.objectweb.asm.ClassReader;
import org.deuce.objectweb.asm.ClassWriter;
import org.deuce.objectweb.asm.FieldVisitor;
import org.deuce.objectweb.asm.Label;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.transaction.Context;
import org.deuce.transaction.ContextDelegator;
import org.deuce.transaction.capmem.CapturedStateArrayBase;
import org.deuce.transaction.capmem.CapturedStateByteArray;
import org.deuce.transaction.capmem.CapturedStateCharArray;
import org.deuce.transaction.capmem.CapturedStateDoubleArray;
import org.deuce.transaction.capmem.CapturedStateFloatArray;
import org.deuce.transaction.capmem.CapturedStateIntArray;
import org.deuce.transaction.capmem.CapturedStateLongArray;
import org.deuce.transaction.capmem.CapturedStateObjectArray;
import org.deuce.transaction.capmem.CapturedStateShortArray;
import org.deuce.transform.Exclude;
import org.deuce.transform.asm.type.TypeCodeResolver;
import org.deuce.transform.asm.type.TypeCodeResolverFactory;
import org.deuce.transform.util.IgnoreTree;

/*
 * Enhancement for wrapping arrays in CapturedStateArray objects.
 */
@Exclude
public class ClassEnhancerCapturedStateArray  extends ClassAdapter implements Opcodes, ClassEnhancer{

	/*---------------------------------------------------------*
	 *~~~~~~~~~~~~~~~  AUXILIARY CLASSES   ~~~~~~~~~~~~~~~~~~~~*
	 *---------------------------------------------------------*/    

	@SuppressWarnings("serial")
	@Exclude
	final private static class StopInstrumentationException extends RuntimeException{
	}

	final private static class MethodInfo{
		final int access;
		final String methodName;
		final String desc;
		final String signature;
		final String[] exceptions;
		public MethodInfo(int access, String methodName, String desc, String signature, String[] exceptions) {
			super();
			this.access = access;
			this.methodName = methodName;
			this.desc = desc;
			this.signature = signature;
			this.exceptions = exceptions;
		}
	}

	final private static class MethodTarget{
		final int opcode; 
		final String owner;
		final String name;
		final String desc;
		final String newDesc;
		public MethodTarget(int opcode, String owner, String name, String desc, String newDesc) {
			super();
			this.opcode = opcode;
			this.owner = owner;
			this.name = name;
			this.desc = desc;
			this.newDesc = newDesc;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((desc == null) ? 0 : desc.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MethodTarget other = (MethodTarget) obj;
			if (desc == null) {
				if (other.desc != null)
					return false;
			} else if (!desc.equals(other.desc))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}

	/*---------------------------------------------------------*
	 *~~~~~~~~~~~~~~~       CONSTANTS      ~~~~~~~~~~~~~~~~~~~~*
	 *---------------------------------------------------------*/    

	final private static Logger logger = Logger.getLogger("org.deuce");    
	final private static String ALERT_MSG_UNWRAP = "!!! ALERT: you cannot unwrap a multi-dimensional array from a CapturedStateArray object! Consider to access its elements through an atomic getter indexer or exclude its owner class from the instrumentation.";
	final private static String ALERT_MSG_EXCL_CLASS_WITH_ARRAYS = "ERROR: You cannot pass arrays as arguments of constructors of classes that have been excluded from Deuce instrumentation.!!!";

	final private static String CAP_BYTE_ARRAY_DESC = Type.getDescriptor(CapturedStateByteArray.class);
	final private static String CAP_CHAR_ARRAY_DESC = Type.getDescriptor(CapturedStateCharArray.class);
	final private static String CAP_SHORT_ARRAY_DESC = Type.getDescriptor(CapturedStateShortArray.class);
	final private static String CAP_INT_ARRAY_DESC = Type.getDescriptor(CapturedStateIntArray.class);
	final private static String CAP_LONG_ARRAY_DESC = Type.getDescriptor(CapturedStateLongArray.class);
	final private static String CAP_FLOAT_ARRAY_DESC = Type.getDescriptor(CapturedStateFloatArray.class);
	final private static String CAP_DOUBLE_ARRAY_DESC = Type.getDescriptor(CapturedStateDoubleArray.class);
	final private static String CAP_OBJECT_ARRAY_DESC = Type.getDescriptor(CapturedStateObjectArray.class);

	final private static String CAP_BYTE_ARRAY_NAME = Type.getInternalName(CapturedStateByteArray.class);
	final private static String CAP_CHAR_ARRAY_NAME = Type.getInternalName(CapturedStateCharArray.class);
	final private static String CAP_SHORT_ARRAY_NAME = Type.getInternalName(CapturedStateShortArray.class);
	final private static String CAP_INT_ARRAY_NAME = Type.getInternalName(CapturedStateIntArray.class);
	final private static String CAP_LONG_ARRAY_NAME = Type.getInternalName(CapturedStateLongArray.class);
	final private static String CAP_FLOAT_ARRAY_NAME = Type.getInternalName(CapturedStateFloatArray.class);
	final private static String CAP_DOUBLE_ARRAY_NAME = Type.getInternalName(CapturedStateDoubleArray.class);
	final private static String CAP_OBJECT_ARRAY_NAME = Type.getInternalName(CapturedStateObjectArray.class);

	/*
	 * Many of these classes are excluded because they instantiate other excluded classes, 
	 * which receive arrays in the constructor parameters.
	 * According to our solution when a transactional class invokes a method of a 
	 * non-transactional one and passes arguments of array type, then we must unwrap 
	 * the arrays from the CapturedStateArray objects. 
	 * For this purpose we invoke a mediator method, which unwraps the arrays and 
	 * invokes the target method.
	 * Yet, this solution does not work when we are instantiating an object because 
	 * we cannot separate the constructor invocation from the corresponding NEW operation,
	 * so we cannot move the constructor invocation into the mediator method. 
	 * 
	 */
	final private static IgnoreTree excludeTree = new IgnoreTree(
			"java.awt.*," +
					"java.beans.*," +
					"java.io.*," +
					"java.lang.*," +
					"java.math.*," +
					"java.net.*," +
					"java.nio.*," +
					"java.rmi.*," +
					"java.security.*," + 
					"java.sql.*," +
					"java.text.*," +
					"java.util.concurrent.*," +
					"java.util.Currency.*," +
					"java.util.Formatter.*," +
					"java.util.jar.*," +
					"java.util.Locale.*," + 
					"java.util.Properties.*," +
					"java.util.regex.*," +
					"java.util.zip.*," +
					"javax.*," +
					"sun.*," + 
					"com.sun.*," +
					"org.apache.tools.ant.*," + 
					"org.ietf.*," +
					"org.jcp.*," +
			"org.eclipse.*");

	final private static Collection<String> excludeClasses = Arrays.asList(
			"java/util/Arrays",
			"java/util/CurrencyData",
			"java/util/Vector");

	/*---------------------------------------------------------*
	 *~~~~~~~~~~~~~~~  AUXILIARY FUNCTIONS   ~~~~~~~~~~~~~~~~~~*
	 *---------------------------------------------------------*/

	/**
	 * Checks if the given className corresponds to a class that cannot be enhanced
	 * with CapturedStatearrays. 
	 */
	final private static boolean exclude(String className){   
		if(excludeClasses.contains(className))
			return true;

		// We replace $ by / to deal with inner classes as subpackages.
		className = className.replace('$', '/');

		return excludeTree.contains(className);
	} 


	final private static String getCapturedStateArrayDescriptor(Type t){
		switch( t.getSort()) {
		case Type.BOOLEAN:
		case Type.BYTE:
			return CAP_BYTE_ARRAY_DESC;
		case Type.CHAR:
			return CAP_CHAR_ARRAY_DESC;
		case Type.SHORT:
			return CAP_SHORT_ARRAY_DESC;
		case Type.INT:
			return CAP_INT_ARRAY_DESC;
		case Type.LONG:
			return CAP_LONG_ARRAY_DESC;
		case Type.FLOAT:
			return CAP_FLOAT_ARRAY_DESC;
		case Type.DOUBLE:
			return CAP_DOUBLE_ARRAY_DESC;
		default:
			return CAP_OBJECT_ARRAY_DESC;
		}
	}

	final private static String getCapturedStateArrayInternalName(Type t){
		switch(t.getSort()) {
		case Type.BOOLEAN:
		case Type.BYTE:
			return CAP_BYTE_ARRAY_NAME;
		case Type.CHAR:
			return CAP_CHAR_ARRAY_NAME;
		case Type.SHORT:
			return CAP_SHORT_ARRAY_NAME;
		case Type.INT:
			return CAP_INT_ARRAY_NAME;
		case Type.LONG:
			return CAP_LONG_ARRAY_NAME;
		case Type.FLOAT:
			return CAP_FLOAT_ARRAY_NAME;
		case Type.DOUBLE:
			return CAP_DOUBLE_ARRAY_NAME;
		default:
			return CAP_OBJECT_ARRAY_NAME;
		}
	}

	final private static String getCapturedStateArrayInternalName(int operand){
		switch( operand) {
		case T_BOOLEAN:
		case T_BYTE:
			return CAP_BYTE_ARRAY_NAME;
		case T_CHAR:
			return CAP_CHAR_ARRAY_NAME;
		case T_SHORT:
			return CAP_SHORT_ARRAY_NAME;
		case T_INT:
			return CAP_INT_ARRAY_NAME;
		case T_LONG:
			return CAP_LONG_ARRAY_NAME;
		case T_FLOAT:
			return CAP_FLOAT_ARRAY_NAME;
		case T_DOUBLE:
			return CAP_DOUBLE_ARRAY_NAME;
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * Returns the name of the method in this class that unwraps arrays from 
	 * CapturedStateArray objects and invokes the destination method.
	 */
	final private static String mediatorMethodName(String owner, String methodName){
		owner = owner.replace('/', '$');
		methodName = methodName.replace('<', '_'); 
		methodName = methodName.replace('>', '_');
		return owner + '$' + methodName;
	}

	/**
	 * For transactional methods replace all arguments in the descriptor 
	 * from array type by CapturedStateArray
	 */
	final private static String replaceArgumentsOfArrayByCapturedState(String desc){
		String newNewthodDesc = "("; 
		for (String  d: DescUtil.argsDescIterator(desc)) {
			if(d.charAt(0) == '['){
				if(d.charAt(1) == '['){// multiarray
					newNewthodDesc += CAP_OBJECT_ARRAY_DESC;
				} else {
					newNewthodDesc += getCapturedStateArrayDescriptor(Type.getType(d).getElementType());
				}
			}else{
				newNewthodDesc += d;
			}
		}
		newNewthodDesc += ")";

		// replace the return type from array to CapturedStateArray
		int idxRet = desc.indexOf(')') + 1;
		String retDesc = desc.substring( idxRet);
		if(retDesc.charAt(0) == '['){
			if(retDesc.charAt(1) == '['){// multiarray
				retDesc = CAP_OBJECT_ARRAY_DESC;
			} else {
				retDesc = getCapturedStateArrayDescriptor(Type.getType(retDesc).getElementType());
			}
		}
		return newNewthodDesc + retDesc;
	}

	/**
	 * Adds an additional argument argDesc to the descriptor methodDesc.
	 */
	final private static String appendArgument(String methodDesc, String argDesc){
		return "(" + argDesc + methodDesc.substring(1);
	}

	/**
	 * Calculates the number of local variables from the TypeCodeResolver
	 */
	final private static int variablesSize(TypeCodeResolver[] types, boolean isStatic) {
		int i = isStatic ? 0 : 1;
		for( TypeCodeResolver type : types) {
			i += type.localSize();
		}
		return i;
	}

	/*---------------------------------------------------------*
	 *~~~~~~~~~~~~~~~          FIELDS      ~~~~~~~~~~~~~~~~~~~~*
	 *---------------------------------------------------------*/    
	String className;
	byte[] classBytecodes; 
	List<MethodInfo> atomicMethods = new LinkedList<MethodInfo>(); // Keeps the atomic methods with arguments of array type.
	Set<MethodTarget> mediatorsForExcludedClasses = new HashSet<MethodTarget>(); // Keeps the invoked methods of excluded classes, with arguments of array type. 

	/*---------------------------------------------------------*
	 *~~~~~~~~~~~~~~~        CONSTRUCTOR   ~~~~~~~~~~~~~~~~~~~~*
	 *---------------------------------------------------------*/

	public ClassEnhancerCapturedStateArray() {
		super(new ClassWriter(0));
	}

	/*---------------------------------------------------------*
	 *~~~~~~~~~~~~~~~         METHODS      ~~~~~~~~~~~~~~~~~~~~*
	 *---------------------------------------------------------*/

	/**
	 * If the class corresponding to the classfileBuffer received by argument is not Transactional,
	 * then it returns a new List just containing this unmodified class.
	 * We considerer all non abstract types as Transactional classes.
	 */
	@Override
	public List<ClassByteCode> visit(boolean offline, String className, byte[] classfileBuffer){
		this.className = className;
		this.classBytecodes = classfileBuffer;

		if(exclude(className))
			return Arrays.asList(new ClassByteCode(className, classfileBuffer));

		ClassReader cr = new ClassReader(classBytecodes);
		try{
			cr.accept(this, 0); // this call will dispatch the invocation to the visit method bellow
			byte[] transformedClass = ((ClassWriter)super.cv).toByteArray();
			return Arrays.asList(new ClassByteCode(className, transformedClass));
		}catch(StopInstrumentationException e){
			return Arrays.asList(new ClassByteCode(className, classfileBuffer));
		}
	}

	/*---------------------------------------------------------*
	 *~~~~~~~~~~~~~~  ClassVisitor INTERFACE  ~~~~~~~~~~~~~~~~~*
	 *---------------------------------------------------------*/
	/**
	 * We intercept all the fields of the type T[] and replace it by  CapturedState<T>Array   
	 */
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if(desc.charAt(0) == '['){
			if(desc.charAt(1) == '['){
				// In case of multi-dim array
				desc = CAP_OBJECT_ARRAY_DESC;
			}
			else{
				final Type fieldType = Type.getType(desc);
				Type elemType = fieldType.getElementType();
				desc = getCapturedStateArrayDescriptor(elemType);
			}
			signature = null; // we do not keep parameter type information 
		}
		return super.visitField(access, name, desc, signature, value);
	}
	/**
	 * For each method contained in the mediatorsForExcludedClasses structure, 
	 * creates a new mediator method, which receives CapturedStateArray objects as arguments, 
	 * unwraps the encapsulated elements array and invokes the target method.
	 * For each original atomic method, which receives or return arrays, 
	 * creates a third twin that convert arrays to CapturedStateArray objects.
	 */
	@Override
	public void visitEnd() {
		for(MethodTarget target : mediatorsForExcludedClasses){
			createMediatorForExcludedClass(target);
		}
		for (MethodInfo mInfo : atomicMethods) {
			createThirdTwinForAtomicMethod(mInfo);
		}
	}

	/**
	 * Creates a new mediator method, which receives CapturedStateArray objects as arguments, 
	 * unwraps the encapsulated elements array and invokes the target method.
	 * In the reverser direction and if the target method returns an array, then the mediator
	 * must wrap that array in a CapturedStateArray object.
	 */
	private void createMediatorForExcludedClass(MethodTarget target) {
		MethodVisitor mv = super.visitMethod(
				ACC_FINAL | ACC_PRIVATE | ACC_STATIC, 
				mediatorMethodName(target.owner, target.name), 
				target.newDesc, 
				null, // signature, 
				null // exceptions
				);
		Type returnType = Type.getReturnType(target.desc);
		Type[] targetArgs = Type.getArgumentTypes(target.desc);
		Type[] currentArgs = Type.getArgumentTypes(target.newDesc);
		if(targetArgs.length != currentArgs.length){
			if(targetArgs.length != (currentArgs.length - 1))
				throw new IllegalStateException("STRANGE case not covered by this mediator method!");
			Type[] aux = new Type[currentArgs.length];
			System.arraycopy(targetArgs, 0, aux, 1, targetArgs.length);
			aux[0] = currentArgs[0];
			targetArgs = aux;
		}

		TypeCodeResolver returnReolver = TypeCodeResolverFactory.getReolver(returnType);
		TypeCodeResolver[] argumentReolvers = new TypeCodeResolver[targetArgs.length];
		for( int i=0; i< targetArgs.length ; ++i) {
			argumentReolvers[ i] = TypeCodeResolverFactory.getReolver( targetArgs[ i]);
		}

		// load the rest of the arguments
		int local = 0;
		for( int i=0 ; i < argumentReolvers.length ; ++i) { 
			mv.visitVarInsn(argumentReolvers[i].loadCode(), local);
			if(targetArgs[i].getSort() == Type.ARRAY){
				// In this case we have to unwrap the array from the CapturedStateArray object
				String capMemName = null;
				if(targetArgs[i].getDescriptor().charAt(1) == '['){ //multiarray
					capMemName = CAP_OBJECT_ARRAY_NAME;
				}else{
					capMemName = getCapturedStateArrayInternalName(targetArgs[i].getElementType());
				}

				// Test if the result is null
				mv.visitInsn(Opcodes.DUP); // ref, ref->
				Label l0 = new Label();
				mv.visitJumpInsn(IFNULL, l0); // ref ->
				Label l1 = new Label();
				mv.visitJumpInsn(GOTO, l1);
				mv.visitLabel(l0);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitInsn(Opcodes.POP); // ->
				mv.visitInsn(ACONST_NULL); // null ->
				Label l2 = new Label();
				mv.visitJumpInsn(GOTO, l2);
				mv.visitLabel(l1);
				mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {capMemName}, 0, null);
				if(targetArgs[i].getDescriptor().charAt(1) == '['){ //multi-array
					mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
					mv.visitInsn(DUP);
					mv.visitLdcInsn(ALERT_MSG_UNWRAP);
					mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V");
					mv.visitInsn(ATHROW);
					/*
                    mv.visitMethodInsn(INVOKEVIRTUAL, CAP_OBJECT_ARRAY_NAME, "unwrapp", "()Ljava/lang/Object;");
                    mv.visitTypeInsn( CHECKCAST, targetArgs[i].getInternalName());
					 */
				}else if(capMemName == CAP_OBJECT_ARRAY_NAME){
					mv.visitFieldInsn(GETFIELD, capMemName, "elements", "[Ljava/lang/Object;"); // array ->
					mv.visitTypeInsn( CHECKCAST, targetArgs[i].getInternalName());
				}
				else{
					mv.visitFieldInsn(GETFIELD, capMemName, "elements", targetArgs[i].getDescriptor()); // array ->
				}
				mv.visitLabel(l2);
			}
			local += argumentReolvers[i].localSize(); // move to the next argument
		}

		// invoke the corresponding method with Arrays as argument instead of CapturedStateArray 
		mv.visitMethodInsn(target.opcode, target.owner, target.name, target.desc);

		// returns
		if( returnReolver == null) {
			mv.visitInsn( RETURN); // return;
		}
		else {
			if(returnType.getSort() == Type.ARRAY){
				// In this case we have to wrap the array into a CapturedStateArray object
				String capMemName = null;
				if(returnType.getDescriptor().charAt(1) == '['){ //multiarray
					capMemName = CAP_OBJECT_ARRAY_NAME;
				}else{
					capMemName = getCapturedStateArrayInternalName(returnType.getElementType());
				}

				// stack = array ->
				mv.visitTypeInsn(NEW, capMemName);// array, ref ->
				mv.visitInsn(Opcodes.DUP_X1);// ref, array, ref ->
				mv.visitInsn(Opcodes.SWAP); // ref, ref, array ->
				String desc = null;
				if(capMemName == CAP_OBJECT_ARRAY_NAME){
					desc = "[Ljava/lang/Object;";
				}else{
					desc = returnType.getDescriptor(); 
				}
				mv.visitMethodInsn(INVOKESPECIAL, capMemName, "<init>", "(" + desc +")V"); // ref ->
				mv.visitInsn(returnReolver.returnCode()); // RETURN
			}else{
				mv.visitInsn(returnReolver.returnCode()); // RETURN
			}
		}
		int varsSize = variablesSize( argumentReolvers, true);
		mv.visitMaxs(6 + varsSize , varsSize);
		mv.visitEnd();
	}

	/**
	 * For each atomic method that receives or return arrays, the it  
	 * creates a third twin that convert arrays to CapturedStateArray objects
	 * and vice-versa.
	 */
	private void createThirdTwinForAtomicMethod(MethodInfo mInfo) {
		MethodVisitor mv = super.visitMethod(mInfo.access, mInfo.methodName, mInfo.desc, mInfo.signature, mInfo.exceptions);

		Type returnType = Type.getReturnType(mInfo.desc);
		Type[] argumentTypes = Type.getArgumentTypes(mInfo.desc);

		TypeCodeResolver returnReolver = TypeCodeResolverFactory.getReolver(returnType);
		TypeCodeResolver[] argumentReolvers = new TypeCodeResolver[ argumentTypes.length];
		for( int i=0; i< argumentTypes.length ; ++i) {
			argumentReolvers[ i] = TypeCodeResolverFactory.getReolver( argumentTypes[ i]);
		}

		// load the rest of the arguments
		boolean isStatic = (mInfo.access & ACC_STATIC) != 0;
		if(!isStatic)
			mv.visitVarInsn(ALOAD, 0);

		// load the rest of the arguments
		int local = isStatic ? 0 : 1;
		for( int i=0 ; i < argumentReolvers.length ; ++i) { 
			mv.visitVarInsn(argumentReolvers[i].loadCode(), local);
			if(argumentTypes[i].getSort() == Type.ARRAY){
				// In this case we have to wrap the array into a CapturedStateArray object
				String capMemName = null;
				if(argumentTypes[i].getDescriptor().charAt(1) == '['){ //multiarray
					capMemName = CAP_OBJECT_ARRAY_NAME;
				}else{
					capMemName = getCapturedStateArrayInternalName(argumentTypes[i].getElementType());
				}

				// stack = array ->
				mv.visitTypeInsn(NEW, capMemName);// array, ref ->
				mv.visitInsn(Opcodes.DUP_X1);// ref, array, ref ->
				mv.visitInsn(Opcodes.SWAP); // ref, ref, array ->
				String desc = null;
				if(capMemName == CAP_OBJECT_ARRAY_NAME){
					desc = "[Ljava/lang/Object;";
				}else{
					desc = argumentTypes[i].getDescriptor(); 
				}
				mv.visitMethodInsn(INVOKESPECIAL, capMemName, "<init>", "(" + desc +")V"); // ref ->

			}
			local += argumentReolvers[i].localSize(); // move to the next argument
		}

		// invoke the corresponding method with CapturedStateArray as argument instead of array. 
		if( isStatic)
			mv.visitMethodInsn(INVOKESTATIC, className, mInfo.methodName, replaceArgumentsOfArrayByCapturedState(mInfo.desc)); // ... = foo( ...
		else
			mv.visitMethodInsn(INVOKEVIRTUAL, className, mInfo.methodName, replaceArgumentsOfArrayByCapturedState(mInfo.desc)); // ... = foo( ...

		// returns
		if( returnReolver == null) {
			mv.visitInsn( RETURN); // return;
		}
		else {
			if(returnType.getSort() == Type.ARRAY){
				// In this case we have to unwrap the array from the CapturedStateArray object
				String capMemName = getCapturedStateArrayInternalName(returnType.getElementType());

				// Test if the result is null
				mv.visitVarInsn(ASTORE, local); // last argument => next a new local variable
				mv.visitVarInsn(ALOAD, local);
				Label l0 = new Label();
				mv.visitJumpInsn(IFNONNULL, l0); // Test if the result is NULL
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ARETURN); // RETURN NULL
				mv.visitLabel(l0);
				mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {capMemName}, 0, null);
				mv.visitVarInsn(ALOAD, local);
				if(returnType.getDescriptor().charAt(1) == '['){ //multi-array
					mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
					mv.visitInsn(DUP);
					mv.visitLdcInsn(ALERT_MSG_UNWRAP);
					mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V");
					mv.visitInsn(ATHROW);
					/*
                    mv.visitMethodInsn(INVOKEVIRTUAL, CAP_OBJECT_ARRAY_NAME, "unwrapp", "()Ljava/lang/Object;");
                    mv.visitTypeInsn( CHECKCAST, returnType.getInternalName());
					 */
				}else if(capMemName == CAP_OBJECT_ARRAY_NAME){
					mv.visitFieldInsn(GETFIELD, capMemName, "elements", "[Ljava/lang/Object;"); // array ->
					mv.visitTypeInsn( CHECKCAST, returnType.getInternalName());
				}
				else{
					mv.visitFieldInsn(GETFIELD, capMemName, "elements", returnType.getDescriptor()); // array ->
				}
				mv.visitInsn(returnReolver.returnCode()); // RETURN
			}else{
				mv.visitInsn(returnReolver.returnCode()); // RETURN
			}
		}
		int varsSize = variablesSize( argumentReolvers, isStatic) + 1; // add 1 for the auxiliar local variable
		mv.visitMaxs(6 + varsSize , varsSize);
		mv.visitEnd();		
	}

	/**
	 * For transactional methods replaces arguments of array type by the corresponding
	 * CapturedStateArray wrapper. 
	 */
	@Override
	public MethodVisitor visitMethod(int access,final String methodName,String desc,String signature,String[] exceptions){
		String oldDesc = desc; // Keep the original descriptor

		/*
		 * It returns a negative value if this method does not contain 
		 * a Context argument - original regular method.
		 * Otherwise, this method includes a Context argument and this means
		 * that it is a transactional method.
		 */
		int argumentsSize = DescUtil.ctorArgumentsSize(access, desc);
		if(argumentsSize >= 0){
			desc = replaceArgumentsOfArrayByCapturedState(desc);
		}

		MethodVisitor mv = super.visitMethod(access, methodName, desc, signature, exceptions);

		if(argumentsSize >= 0){
			// In this case this is a transactional method
			MethodInfo mInfo = null;
			if(!desc.equals(oldDesc)){
				/*
				 * In this case the transactional method has arguments of array types and 
				 * if it is also Atomic, then we have to create a 3rd method. 
				 * Otherwise, we do not have to change anything in the method definition.
				 */
				mInfo = new MethodInfo(access, methodName, oldDesc, signature, exceptions);
			}
			mv = replaceArraysAccessesByCapturedState(mv, argumentsSize, mInfo);
		}else{
			// In this case this is the original regular method
			mv = unwrapArrayFromCapturedState(mv, argumentsSize, methodName);
		}

		return mv;
	}

	/**
	 * All arrays fields are replaced by CapturedState<T>Array and in non-transactional 
	 * methods we must unwrap those arrays from the wrapper object.
	 */
	private MethodVisitor unwrapArrayFromCapturedState(MethodVisitor mv, final int argumentsSize, final String methodName){
		return new MethodAdapter(mv) {
			private boolean replaceAccessField = false;
			/**
			 * Replaces getfield, putfield, putstatic and getstatic of fields of array type,
			 * by the correspondent descriptor on CapturedStateArray object.
			 */
			@Override
			public void visitFieldInsn(int opcode, String owner, String name, String desc) {
				if(desc.charAt(0) == '['){
					replaceAccessField = true;
					String capMemName, capMemDesc = null;
					if(desc.charAt(1) == '['){
						capMemName = CAP_OBJECT_ARRAY_NAME;
						capMemDesc = CAP_OBJECT_ARRAY_DESC;
					}else{
						capMemName = getCapturedStateArrayInternalName(Type.getType(desc).getElementType());
						capMemDesc = getCapturedStateArrayDescriptor(Type.getType(desc).getElementType());
					}
					if(opcode == PUTFIELD || opcode == PUTSTATIC){
						// stack = array ->
						super.visitTypeInsn(NEW, capMemName);// array, ref ->
						super.visitInsn(Opcodes.DUP_X1);// ref, array, ref ->
						super.visitInsn(Opcodes.SWAP); // ref, ref, array ->
						if(capMemName == CAP_OBJECT_ARRAY_NAME){
							desc = "[Ljava/lang/Object;";
						}else if(desc.charAt(1) == 'Z'){
							desc = desc.replace('Z', 'B');
						}
						super.visitMethodInsn(INVOKESPECIAL, capMemName, "<init>", "(" + desc +")V"); // ref ->
						super.visitFieldInsn(opcode, owner, name, capMemDesc); // ->
						return;
					}else if(opcode == GETFIELD || opcode == GETSTATIC){
						super.visitFieldInsn(opcode, owner, name, capMemDesc); // ref ->
						/*
						 * Test if the result is null
						 */
						super.visitInsn(Opcodes.DUP); // ref, ref->
						Label l0 = new Label();
						super.visitJumpInsn(IFNULL, l0); // ref ->
						Label l1 = new Label();
						super.visitJumpInsn(GOTO, l1);
						super.visitLabel(l0);
						super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
						super.visitInsn(Opcodes.POP); // ->
						super.visitInsn(ACONST_NULL); // nul ->
						Label l2 = new Label();
						super.visitJumpInsn(GOTO, l2);
						super.visitLabel(l1);
						super.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {capMemName}); // array ->
						if(desc.charAt(1) == '['){//multi-array
							mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
							mv.visitInsn(DUP);
							mv.visitLdcInsn(ALERT_MSG_UNWRAP);
							mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V");
							mv.visitInsn(ATHROW);
							/*
                            super.visitMethodInsn(INVOKEVIRTUAL, CAP_OBJECT_ARRAY_NAME, "unwrapp", "()Ljava/lang/Object;");
                            super.visitTypeInsn( CHECKCAST, Type.getType(desc).getInternalName());
							 */
						}
						else if(capMemName == CAP_OBJECT_ARRAY_NAME){
							super.visitFieldInsn(GETFIELD, capMemName, "elements", "[Ljava/lang/Object;"); // array ->
							super.visitTypeInsn( CHECKCAST, Type.getType(desc).getInternalName());
						}
						else{
							if(desc.charAt(1) == 'Z'){
								desc = desc.replace('Z', 'B');
							}
							super.visitFieldInsn(GETFIELD, capMemName, "elements", desc); // array ->
						}
						super.visitLabel(l2);
						return;
					}
				}
				super.visitFieldInsn(opcode, owner, name, desc);
			}
			@Override
			public void visitMaxs(int maxStack, int maxLocals) {
				if(replaceAccessField)
					maxStack += 2;
				super.visitMaxs(maxStack, maxLocals);
			}
		};
	}

	/**
	 * For transactional methods we replace all array accesses T[] by CapturedState<T>Array. 
	 */
	private MethodVisitor replaceArraysAccessesByCapturedState(MethodVisitor mv, final int argumentsSize, final MethodInfo mInfo){
		return new MethodAdapter(mv) {
			private boolean isIrrevocable = false;

			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				if(Type.getType(desc).getClassName().equals(Irrevocable.class.getName())){
					isIrrevocable = true;
				}
				if(mInfo != null && Type.getType(desc).getClassName().equals(Atomic.class.getName())){
					atomicMethods.add(mInfo); // For later create the 3rd twin.
				}
				return super.visitAnnotation(desc, visible);
			}

			/**
			 * Replaces local variables declaration.
			 */
			@Override
			public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
				if(desc.charAt(0) == '['){
					if(desc.charAt(1) == '['){
						desc = CAP_OBJECT_ARRAY_DESC;
					}
					else{
						final Type fieldType = Type.getType(desc);
						Type elemType = fieldType.getElementType();
						desc = getCapturedStateArrayDescriptor(elemType);
					}
					signature = null; // we do not keep parameter type information 
				}
				super.visitLocalVariable(name, desc, signature, start, end, index);
			}

			/**
			 * Replaces getfield, putfield, putstatic and getstatic of fields of array type,
			 * by the correspondent descriptor on CapturedStateArray object.
			 */
			@Override
			public void visitFieldInsn(int opcode, String owner, String name, String desc) {
				if(desc.charAt(0) == '['){
					if(desc.charAt(1) == '['){
						desc = CAP_OBJECT_ARRAY_DESC;
					}
					else{
						final Type fieldType = Type.getType(desc);
						Type elemType = fieldType.getElementType();
						desc = getCapturedStateArrayDescriptor(elemType);
					}
				}
				super.visitFieldInsn(opcode, owner, name, desc);
			}

			/**
			 * On array length invokes the arrayLength method to the CapturedStateArray object.
			 */
			@Override
			public void visitInsn(int opcode) {
				if(opcode == ARRAYLENGTH)
					super.visitMethodInsn(INVOKEVIRTUAL, CapturedStateArrayBase.class.getName().replace('.', '/'), "arrayLength", "()I");
				else
					super.visitInsn(opcode);
			}

			/**
			 * Replaces object arrays instantiation.
			 */
			@Override
			public void visitTypeInsn(int opcode, String type) {
				if(opcode == ANEWARRAY){
					super.visitTypeInsn(NEW, CAP_OBJECT_ARRAY_NAME);// length, ref ->
					super.visitInsn(Opcodes.DUP_X1);// ref, length, ref ->
					super.visitInsn(Opcodes.SWAP);// ref, ref, length ->
					super.visitLdcInsn(type);// ref, ref, length, type ->
					super.visitVarInsn(ALOAD, argumentsSize - 1); // ref, ref, length, type, context ->
					super.visitMethodInsn(INVOKESPECIAL, CAP_OBJECT_ARRAY_NAME, "<init>", "(ILjava/lang/String;" + Context.CONTEXT_DESC +")V");
				}
				/**
				 * Intercept operations for CHECKCAST or INSTANCEOF.
				 */
				else if(type.charAt(0) == '['){
					if(type.charAt(1) == '['){
						type = CAP_OBJECT_ARRAY_NAME;
					}
					else{
						final Type fieldType = Type.getType(type);
						Type elemType = fieldType.getElementType();
						type = getCapturedStateArrayInternalName(elemType);
					}
					super.visitTypeInsn(opcode, type);
				}
				else{
					super.visitTypeInsn(opcode, type);          
				}
			}

			/**
			 * Replaces multi-arrays instantiation.
			 */
			@Override
			public void visitMultiANewArrayInsn(String desc, int dims) {
				super.visitMultiANewArrayInsn(desc, dims); // multiarr
				super.visitTypeInsn(NEW, CAP_OBJECT_ARRAY_NAME);// multiarr, ref ->
				super.visitInsn(Opcodes.DUP_X1);// ref, multiarr, ref ->
				super.visitInsn(Opcodes.SWAP);// ref, ref, multiarr->
				super.visitVarInsn(ALOAD, argumentsSize - 1); // ref, ref, multiarr, context ->
				super.visitMethodInsn(INVOKESPECIAL, CAP_OBJECT_ARRAY_NAME, "<init>", "([Ljava/lang/Object;" + Context.CONTEXT_DESC +")V");
			}

			/**
			 * Replaces arrays of primitive types.
			 */
			@Override
			public void visitIntInsn(int opcode, int operand) {
				if(opcode == NEWARRAY){
					// stack = length ->
					String capMemName= getCapturedStateArrayInternalName(operand);
					super.visitTypeInsn(NEW, capMemName);// length, ref ->
					super.visitInsn(Opcodes.DUP_X1);// ref, length, ref ->
					super.visitInsn(Opcodes.SWAP);// ref, ref, length ->
					super.visitVarInsn(ALOAD, argumentsSize - 1); // ref, ref, length, context ->
					super.visitMethodInsn(INVOKESPECIAL, capMemName, "<init>", "(I" + Context.CONTEXT_DESC +")V");
				}else{
					super.visitIntInsn(opcode, operand);
				}
			}

			/**
			 * Replaces the invocation of array barriers by the correspondent 
			 * methods with CapturedStateArray as argument.
			 * Replaces arguments of array type in method invocation, by the
			 * corresponding CapturedStateArray arguments. 
			 */
			@Override
			public void visitMethodInsn(int opcode, String owner, String name, String desc) {
				if(owner.equals(ContextDelegator.CONTEXT_DELEGATOR_INTERNAL)){
					if(name.equals(ContextDelegator.READ_ARR_METHOD_NAME) || name.equals(ContextDelegator.WRITE_ARR_METHOD_NAME)){
						// Get the first argument descriptor corresponding to an array type
						String  firstArgDesc = DescUtil.argsDescIterator(desc).iterator().next();
						Type elemType = Type.getType(firstArgDesc).getElementType();
						String newArgDesc = getCapturedStateArrayDescriptor(elemType);
						desc = desc.replace(firstArgDesc, newArgDesc);
						super.visitMethodInsn(opcode, owner, name, desc);
						return;
					}
				}
				if(owner.charAt(0) == '['){
					if(owner.charAt(1) == '['){
						// In case of multi-dim array
						owner = CAP_OBJECT_ARRAY_NAME;
					}
					else{
						final Type fieldType = Type.getType(desc);
						Type elemType = fieldType.getElementType();
						owner = getCapturedStateArrayInternalName(elemType);
					} 
				}

				/*
				 * System.arraycopy has Object arguments instead of [] and does not 
				 * satisfies the next verification.
				 * So we must differentiate the case of the System.arraycopy .
				 */
				if((owner.equals("java/lang/System") && name.equals("arraycopy"))){
					if(!ExcludeIncludeStore.exclude("java/lang/System") && !isIrrevocable){
						/*
						 * In this case the system class was not excluded and its methods
						 * are invoked with the additional parameter Context.
						 */
						mv.visitInsn(Opcodes.POP); // removes the Context object from the top of the Stack.
					}
					super.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CapturedStateArrayBase.class), "arraycopy", "(ILjava/lang/Object;II)V");
				}else{
					String newDesc = replaceArgumentsOfArrayByCapturedState(desc);
					if((ExcludeIncludeStore.exclude(owner) || exclude(owner))&& !desc.equals(newDesc)){
						/*
						 * In this case the owner of the invoked method is not transactional and
						 * simultaneously it receives arrays arguments that have been wrapped in 
						 * CapturedStateArray objects.
						 * So we will invoke a bridge/mediator method, with the name owner$name, 
						 * which receives CapturedStateArray objects as arguments, unwraps the encapsulated 
						 * elements arrays and finally invokes the target method.     
						 */ 

						if(name.equals("<init>")){
							/*
							 * In this case we cannot separate the INVOKESPECIAL to the <init>
							 * from the previously NEW. Otherwise we will get in the mediator 
							 * the following error:VerifyError: Expecting to find unitialized 
							 * object on stack.
							 * So abort it and advise to remove array parameters from the invoke 
							 * constructor. 
							 */
							logger.severe(
									"Trying to instantiate: " + owner + desc + " from " + className + " - " +  
											ALERT_MSG_EXCL_CLASS_WITH_ARRAYS);
							System.exit(-1);
						}

						int originalOpcode = opcode; 
						if(opcode != INVOKESTATIC){
							opcode = INVOKESTATIC;
							newDesc = appendArgument(newDesc, "L" + owner + ";");
						}
						mediatorsForExcludedClasses.add(new MethodTarget(originalOpcode, owner, name, desc, newDesc));
						name = mediatorMethodName(owner, name);
						owner = className;
					}
					super.visitMethodInsn(opcode, owner, name, newDesc);
				}
			}
			@Override
			public void visitMaxs(int maxStack, int maxLocals) {
				super.visitMaxs(maxStack + 6, maxLocals);
			}
		};
	}
}
