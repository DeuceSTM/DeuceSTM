package org.deuce.transform.asm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.Exclude;
import org.deuce.transform.util.IgnoreTree;

/**
 * @author Guy Korland
 * @since 1.0
 */
@Exclude
public class Agent implements ClassFileTransformer {
	private static final Logger logger = Logger.getLogger("org.deuce.agent");
	final private static boolean VERBOSE = Boolean.getBoolean("org.deuce.verbose");
	final private static boolean GLOBAL_TXN = Boolean.getBoolean("org.deuce.transaction.global");
	final public static IgnoreTree IGNORE_TREE;
	static
	{
		String property = System.getProperty("org.deuce.exclude");
		if( property == null)
			property = "java.*,sun.*,org.eclipse.*,org.junit.*,junit.*";
		IGNORE_TREE = new IgnoreTree( property);
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
			if (loader == null || className.startsWith("$") || IGNORE_TREE.contains(className)) // Don't transform classes from the boot classLoader.
				return classfileBuffer;

			if (logger.isLoggable(Level.FINER))
				logger.finer("Transforming: Class=" + className + " ClassLoader=" + loader);

			classfileBuffer = addFrames(className, classfileBuffer);
			
			ByteCodeVisitor cv;
			if( GLOBAL_TXN)
				cv = new org.deuce.transaction.global.ClassTransformer( className); 
			else
				cv = new org.deuce.transform.asm.ClassTransformer( className); 

			byte[] bytecode = cv.visit(classfileBuffer);

			if( VERBOSE){
				try {
					verbose(className, bytecode);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return bytecode;
		}
		catch(Exception e) {
			logger.log( Level.SEVERE, "Fail on class transform: " + className, e);
			return classfileBuffer;
		}
	}

	/**
	 * Reads the bytecode and calculate the frames, to support 1.5- code.
	 * 
	 * @param className class to manipluate 
	 * @param classfileBuffer original byte code
	 *  
	 * @return bytecode with frames
	 */
	private byte[] addFrames(String className, byte[] classfileBuffer) {
		
		// TODO retrun the same bytecode if 1.6+ 
		ByteCodeVisitor frameCompute = new ByteCodeVisitor( className);
		frameCompute.visit( classfileBuffer);
		classfileBuffer = frameCompute.toByteArray();
		return classfileBuffer;
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		UnsafeHolder.getUnsafe();
		logger.fine("Starting Duece agent");
		inst.addTransformer(new Agent());
	}

	private void verbose(String className, byte[] bytecode) throws FileNotFoundException,
	IOException {
		File file = new File( "verbose");
		file.mkdir();

		String[] packages = className.split("/");
		for( int i=0 ; i<packages.length-1 ; ++i){
			file = new File( file, packages[i]);
			file.mkdir();
		}
		file = new File( file, packages[packages.length -1]);
		FileOutputStream fs = new FileOutputStream( file + ".class");
		fs.write(bytecode);
		fs.close();
	}
}
