package org.deuce.transform.asm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transaction.ContextDelegator;
import org.deuce.transform.Exclude;

/**
 * A java agent to dynamically instrument transactional supported classes/  
 * 
 * Modified by FMC@2012-09-14:
 * * Included support for ClassEnhancer objects, which act like ASM transformations
 *   that we can add after the standard instrumentation made by the DeuceStm 
 *   to transactional classes.
 *    
 * @author Guy Korland
 * @since 1.0
 */
@Exclude
public class Agent implements ClassFileTransformer {
	final private static Logger logger = Logger.getLogger("org.deuce.agent");
	final private static boolean VERBOSE = Boolean.getBoolean("org.deuce.verbose");
	final private static boolean GLOBAL_TXN = Boolean.getBoolean("org.deuce.transaction.global");

	/**
	 * The name of the class that implements a post transformation (FMC@2012-09-14).
	 */
	final private static ClassEnhancerChain postEnhancer;

	static{
		try {
			/*
			 * Here, we force to load the ContextDelegator specified by the end user, because it may depends
			 * on post transformations.  
			 * In this case the specified ContextDelegator must define the required post transformations 
			 * in the corresponding system property: org.deuce.transform.post.   
			 * Then the Agent will process this transformation. 
			 */
			logger.info("context delegator = " + ContextDelegator.CONTEXT_DELEGATOR_INTERNAL);
			/*
			 * Loads the post ClassEnhancer objects. 
			 */
			String enhancer = System.getProperty("org.deuce.transform.post");
			ClassEnhancerChain aux = null;
			if(enhancer != null && !enhancer.equals("")){
				for(String eh : enhancer.split(",")){
					/*
					 * The ClassEnhancerChain implement a chain of transformations. 
					 * This solution was adapted from the the Decorator design pattern. 
					 * We cannot make the own implementations of the ClassEnhancer as 
					 * decorators, because we need to instantiate a new ClassEnhancer 
					 * for each instrumentation.  
					 */
					Class<ClassEnhancer> klassEnhancer = (Class<ClassEnhancer>) Class.forName(eh);
					aux = new ClassEnhancerChain(klassEnhancer, aux);
				}
			}
			postEnhancer = aux;                
		} 
		catch (ClassNotFoundException e) {throw new RuntimeException(e);} 
	}

	/*
	 * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader,
	 *      java.lang.String, java.lang.Class, java.security.ProtectionDomain,
	 *      byte[])
	 */
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer)
	throws IllegalClassFormatException {
		try {
			// Don't transform classes from the boot classLoader.
			if (loader != null)
				return transform(className, classfileBuffer, false).get(0).getBytecode();
		}
		catch(Exception e) {
			logger.log( Level.SEVERE, "Fail on class transform: " + className, e);
		}
		return classfileBuffer;
	}
	
	/**
	 * (FMC@2012-09-14) After performing the Deuce transformation we check the postEnhancer field.
	 * If the user specified a post transformation then we will instrument also every class resulted 
	 * from the Deuce transformation with the enhancement defined by the postEnhancer.
	 * 
	 * @param offline <code>true</code> if this is an offline transform.
	 */
	private List<ClassByteCode> transform(String className, byte[] classfileBuffer, boolean offline)
	throws IllegalClassFormatException {

		ArrayList<ClassByteCode> byteCodes = new ArrayList<ClassByteCode>();
		if (className.startsWith("$") || ExcludeIncludeStore.exclude(className)){
			byteCodes.add(new ClassByteCode( className, classfileBuffer));
			return byteCodes;
		}
		
		if (logger.isLoggable(Level.FINER))
			logger.finer("Transforming: Class=" + className);

		classfileBuffer = addFrames(className, classfileBuffer);

		if( GLOBAL_TXN){
			ByteCodeVisitor cv = new org.deuce.transaction.global.ClassTransformer( className);
			byte[] bytecode = cv.visit(classfileBuffer);
			byteCodes.add(new ClassByteCode( className, bytecode));
		}
		else{
			ExternalFieldsHolder fieldsHolder = null;
			if(offline) {
				fieldsHolder = new ExternalFieldsHolder(className);
			}
			org.deuce.transform.asm.ClassTransformer cv = new org.deuce.transform.asm.ClassTransformer( className, fieldsHolder);
 			byte[] bytecode = cv.visit(classfileBuffer);
 			/*
 			 * Pos Deuce transformation for each class
 			 */
 			if(cv.exclude || postEnhancer == null){
 				byteCodes.add(new ClassByteCode( className, bytecode));
 			}
 			else{
 				List<ClassByteCode> res = postEnhancer.visit(offline, className, bytecode);
 				byteCodes.addAll(res);
 			}
			if(offline) {
				byteCodes.add(fieldsHolder.getClassByteCode());
			}
		}

		if( VERBOSE){
			try {
				verbose(byteCodes);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return byteCodes;
	}
	
	/**
	 * Reads the bytecode and calculate the frames, to support 1.5- code.
	 * 
	 * @param className class to manipulate 
	 * @param classfileBuffer original byte code
	 *  
	 * @return bytecode with frames
	 */
	private byte[] addFrames(String className, byte[] classfileBuffer) {

		try{
			FramesCodeVisitor frameCompute = new FramesCodeVisitor( className);
			return frameCompute.visit( classfileBuffer); // avoid adding frames to Java6
		}
		catch( FramesCodeVisitor.VersionException ex){
			return classfileBuffer;
		}
	}

	public static void premain(String agentArgs, Instrumentation inst) throws Exception{
		UnsafeHolder.getUnsafe();
		logger.fine("Starting Duece agent");
		inst.addTransformer(new Agent());
	}
	
	/**
	 * Used for offline instrumentation.
	 * @param args input jar & output jar
	 * e.g.: "C:\Java\jdk1.6.0_19\jre\lib\rt.jar" "C:\rt.jar"
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		UnsafeHolder.getUnsafe();
		logger.fine("Starting Duece translator");
		
		// TODO check args
		Agent agent = new Agent();
		agent.transformJar(args[0], args[1]);
	}
	
	private void transformJar( String inFileNames, String outFilenames) throws IOException, IllegalClassFormatException {
		
		String[] inFileNamesArr = inFileNames.split(";");
		String[] outFilenamesArr = outFilenames.split(";");
		if(inFileNamesArr.length != outFilenamesArr.length)
			throw new IllegalArgumentException("Input files list length doesn't match output files list.");
		
		for(int i=0 ; i<inFileNamesArr.length ; ++i){
			String inFileName = inFileNamesArr[i];
			String outFilename = outFilenamesArr[i];
			
			final int size = 4096;
			byte[] buffer = new byte[size];
			ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
			JarInputStream jarIS = new JarInputStream(new FileInputStream(inFileName));
			JarOutputStream jarOS = new JarOutputStream(new FileOutputStream(outFilename), jarIS.getManifest());

			logger.info("Start translating source:" + inFileName + " target:" + outFilename);

			String nextName = "";
			try {
				for (JarEntry nextJarEntry = jarIS.getNextJarEntry(); nextJarEntry != null;
				nextJarEntry = jarIS.getNextJarEntry()) {

					baos.reset();
					int read;
					while ((read = jarIS.read(buffer, 0, size)) > 0) {
						baos.write(buffer, 0, read);
					}
					byte[] bytecode = baos.toByteArray();

					nextName = nextJarEntry.getName();
					if( nextName.endsWith(".class")){
						if( logger.isLoggable(Level.FINE)){
							logger.fine("Transalating " + nextName);
						}
						String className = nextName.substring(0, nextName.length() - ".class".length());
						List<ClassByteCode> transformBytecodes = transform( className, bytecode, true);
						for(ClassByteCode byteCode : transformBytecodes){
							JarEntry transformedEntry = new JarEntry(byteCode.getClassName() + ".class");
							jarOS.putNextEntry( transformedEntry); 
							jarOS.write( byteCode.getBytecode());
						}
					}
					else{
						jarOS.putNextEntry( nextJarEntry);
						jarOS.write(bytecode);
					}
				}

			}
			catch(Exception e){
				logger.log(Level.SEVERE, "Failed to translate " + nextName, e);
			}
			finally {
				logger.info("Closing source:" + inFileName + " target:" + outFilename);
				jarIS.close();
				jarOS.close();
			}
		}
	}
	
	private void verbose(List<ClassByteCode> byteCodes) throws FileNotFoundException,
	IOException {
		File verbose = new File( "verbose");
		verbose.mkdir();

		for( ClassByteCode byteCode : byteCodes){
			String[] packages = byteCode.getClassName().split("/");
			File file = verbose;
			for( int i=0 ; i<packages.length-1 ; ++i){
				file = new File( file, packages[i]);
				file.mkdir();
			}
			file = new File( file, packages[packages.length -1]);
			FileOutputStream fs = new FileOutputStream( file + ".class");
			fs.write(byteCode.getBytecode());
			fs.close();
		}
	}
}
