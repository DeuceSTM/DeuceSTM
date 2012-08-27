package org.deuce.transform.util;

import org.deuce.objectweb.asm.Type;
import org.deuce.objectweb.asm.commons.Method;

public class Util {
	final static private String ADDRESS_FIELD_POSTFIX = "__ADDRESS__";

	public static String getAddressField( String field) {
		return field + ADDRESS_FIELD_POSTFIX;
	}
	
	public static int calcArgumentsSize( boolean isStatic, Method newMethod){
		int size = isStatic ? 0 : 1; // if not static "this" is the first argument
		for( Type type : newMethod.getArgumentTypes()){
			size += type.getSize();
		}
		return size;
	}
}
