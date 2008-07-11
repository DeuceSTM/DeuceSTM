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

/**
 * @author Guy Korland
 * @since 1.0
 */
@Exclude
public class Agent implements ClassFileTransformer {
	private static final Logger logger = Logger.getLogger("org.deuce.agent");
	final private static boolean VERBOSE = Boolean.getBoolean("org.deuce.verbose");
	final private static boolean globaleTxn = Boolean.getBoolean("org.deuce.transaction.global");

	/*
	 * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader,
	 *      java.lang.String, java.lang.Class, java.security.ProtectionDomain,
	 *      byte[])
	 */
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer)
	throws IllegalClassFormatException {
		try {
			if (loader == null || className.startsWith("sun")  || className.startsWith("java") ) // Don't transform classes from the boot classLoader.
				return classfileBuffer;

			if (logger.isLoggable(Level.FINER))
				logger.finer("Transforming: Class=" + className + " ClassLoader=" + loader);

			ByteCodeVisitor cv;
			if( globaleTxn)
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
