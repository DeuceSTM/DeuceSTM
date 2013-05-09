package org.deuce.transform.asm;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.deuce.objectweb.asm.ClassAdapter;
import org.deuce.objectweb.asm.ClassReader;
import org.deuce.objectweb.asm.ClassWriter;
import org.deuce.objectweb.asm.MethodAdapter;
import org.deuce.objectweb.asm.MethodVisitor;
import org.deuce.objectweb.asm.Opcodes;
import org.deuce.objectweb.asm.Type;
import org.deuce.transaction.Context;
import org.deuce.transaction.capmem.CapturedState;
import org.deuce.transform.Exclude;


/**
 * This transformation will replace the top of the transactional classes hierarchy 
 * from Object to CapturedState. 
 * So, all classes will have CapturedState as the superclass.
 *
 */
@Exclude
public class ClassEnhancerCapturedState  extends ClassAdapter implements Opcodes, ClassEnhancer{

	@SuppressWarnings("serial")
	@Exclude
	private static class StopInstrumentationException extends RuntimeException{
	}

	public static String CAP_ST_DESC = Type.getInternalName(CapturedState.class);

	private static Collection<String> excludeClasses = Arrays.asList(
			"java/lang/Enum", 
			"java/lang/String", 
			"java/lang/ThreadLocal",
			"java/lang/Exception", 
			"java/lang/RuntimeException", 
			"java/lang/Throwable",
			"java/lang/StackTraceElement",
			"java/util/Vector");

	protected boolean exclude(String className){   
		return excludeClasses.contains(className);
	}

	/*---------------------------------------------------------*
	 *~~~~~~~~~~~~~~~          FIELDS      ~~~~~~~~~~~~~~~~~~~~*
	 *---------------------------------------------------------*/    
	String className;
	byte[] classBytecodes;
	boolean interceptConstructors = false; 

	/*---------------------------------------------------------*
	 *~~~~~~~~~~~~~~~        CONSTRUCTOR   ~~~~~~~~~~~~~~~~~~~~*
	 *---------------------------------------------------------*/

	public ClassEnhancerCapturedState() {
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
	 * Adds the CapturedState class as the root of the classes hierarchy.
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces){
		if((access & ACC_INTERFACE) != 0 ){
			throw new StopInstrumentationException();   
		}
		if(excludeClasses.contains(superName)){
			throw new StopInstrumentationException();
		}
		if(superName.equals("java/lang/Object")){
			interceptConstructors = true;
			superName = CAP_ST_DESC;
			if(signature == null){
				signature = "L" + CAP_ST_DESC  + ";";
				for (String interf : interfaces) {
					signature += "L" + interf + ";";
				}
			}else{
				String typeParams = null;
				// If this class starts with < then it declares 
				// type parameters.
				if(signature.indexOf("<") == 0){
					int idx = signature.indexOf(">");
					typeParams = signature.substring(0, idx + 1);
					signature = signature.substring(idx + 1);
				}
				if(signature.indexOf("Ljava/lang/Object;") == 0){
					signature = signature.replace("Ljava/lang/Object;", "L" + CAP_ST_DESC  + "<L"+ className + ";>;");
				}
				if(typeParams != null) 
					signature = typeParams + signature;
			}
		}
		super.visit(
				version, 
				access & (~ACC_FINAL), // Removes final if exists,
				name, 
				signature, 
				superName, 
				interfaces);
	}

	/**
	 * Changes the invocation of the super constructor from Object to CapturedState class.
	 */
	@Override
	public MethodVisitor visitMethod(int access,final String methodName,String desc,String signature,String[] exceptions){
		// It returns a negative value if this constructor does not contain a Context argument - original regular method
		int argumentsSize = DescUtil.ctorArgumentsSize(access, desc);


		MethodVisitor mv = super.visitMethod(access, methodName, desc, signature, exceptions);

		if(interceptConstructors && methodName.equals("<init>")){
			mv = interceptsInitReplaceSuperInvoke(mv, argumentsSize );
			return mv;
		}else{
			return mv;
		}
	}


	/**
	 * Changes the invocation of the super constructor from Object to CapturedState class.
	 */
	private static MethodVisitor interceptsInitReplaceSuperInvoke(MethodVisitor mv, final int argumentsSize){
		return new MethodAdapter(mv) {
			boolean firstCallToSuper = true;
			@Override
			public void visitMethodInsn(int opcode,String owner,String name,String desc) {
				if(firstCallToSuper && name.equals("<init>") && opcode == INVOKESPECIAL && owner.equals("java/lang/Object")){
					// Changes the invocation of the super constructor from Object to CapturedState class.
					// Just do this for the first invocation corresponding to the super() call.
					// Following usages of the invokespecial to Object constructor could be for 
					// new instances of Object, that should not be replaced by CapturedState call.
					// !!!!! Eventual mistake => it can instruments the wrong invokespecial call. 
					// !!!!! e.g. Bug: super(..., new Object()) - in this case instead of the first call 
					// to Object invokespecial we should instrument the second one. 
					// !!!!! For now, we will keep it like this as a minor workaround !!!!!!
					//
					firstCallToSuper = false;

					// Changes the invocation of the super constructor from Object to CapturedState class
					owner = CAP_ST_DESC; 
					if(argumentsSize >= 0){ 
						// It returns a negative value if this constructor 
						// does not contain a Context argument.
						super.visitVarInsn(ALOAD, argumentsSize - 1); // load context
						desc = "(" + Context.CONTEXT_DESC + ")V";
					}
				}
				super.visitMethodInsn(opcode, owner, name, desc);
			}
			@Override
			public void visitMaxs(int maxStack, int maxLocals) {
				if(argumentsSize >= 0){ 
					maxStack++;
				}
				super.visitMaxs(maxStack, maxLocals);
			}
		};

	}
	
}
