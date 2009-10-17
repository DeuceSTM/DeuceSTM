package org.deuce.transform.asm;

import org.deuce.transform.util.IgnoreTree;

/**
 * Holds the include/exclude information for the classes to instrument.
 *  
 * @author guy
 * @since 1.1
 */
public class ExcludeIncludeStore {

	final private IgnoreTree excludeTree;
	final private IgnoreTree includeTree;
	
	public ExcludeIncludeStore(){

		String property = System.getProperty("org.deuce.exclude");
		if( property == null)
			property = "java.*,sun.*,org.eclipse.*,org.junit.*,junit.*";
		excludeTree = new IgnoreTree( property);

		property = System.getProperty("org.deuce.include");
		if( property == null)
			property = "";
		includeTree = new IgnoreTree( property);
	}
	
	public boolean exclude(String className){
		if(className.startsWith("java/lang/Object"))
			return true;
		return excludeTree.contains(className) && !includeTree.contains(className);
	} 
}
