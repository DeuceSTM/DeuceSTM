package org.deuce.transform.asm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

import org.deuce.reflection.UnsafeHolder;

public class Transformer{
    
    private static final Logger logger = Logger.getLogger("org.deuce.agent");
    final private static boolean VERBOSE = Boolean.getBoolean("org.deuce.verbose");

    public static void premain(String agentArguments, Instrumentation instrumentation) 
    throws Exception{
	UnsafeHolder.getUnsafe();
//	instrumentation.addTransformer(new Transformer());
	
	Class[] classes =  instrumentation.getAllLoadedClasses();
//	ClassDefinition[] definitions = new ClassDefinition[ classes.length];
	
//	ClassPool cp = ClassPool.getDefault();
	
	for( int i=0; i< classes.length ; ++i) {
	    
//	    String 
//	    String name = classes[i].getName().replace('.', '/') + ".class";
//	    CtClass ctClass = cp.get(classes[i].getName());
	    System.out.println(classes[i].getName());
//	    InputStream stream = classes[i].getClassLoader().getResourceAsStream( name);
//	    System.out.println( name + stream);
//	    ByteArrayOutputStream ous = new ByteArrayOutputStream();
//	    for( int b = stream.read() ; b != -1 ; b = stream.read()) {
//		ous.write( b);
//	    }
//	    byte[] bytes = ous.toByteArray();
	    
	    String className =  classes[i].getName().replace('.', '/'); 
	    
	    byte[] bytecode =  new ClassTransformer( className).toByteArray();

	    if( VERBOSE){
		try {
		    verbose(className, bytecode);
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	    
//	    definitions[i] = new ClassDefinition( classes[i], );
	}
	
//	instrumentation.redefineClasses( definitions);
    }


    private static void verbose(String className, byte[] bytecode) throws FileNotFoundException,
    IOException {
	File file = new File( "verbose");
	file.mkdir();

	String[] packages = className.split("\\.");
	for( int i=0 ; i<packages.length -1 ; ++i){
	    file = new File( file, packages[i]);
	    file.mkdir();
	}
	file = new File( file, packages[packages.length -1]);
	FileOutputStream fs = new FileOutputStream( file + ".class");
	fs.write(bytecode);
	fs.close();
    }
}
