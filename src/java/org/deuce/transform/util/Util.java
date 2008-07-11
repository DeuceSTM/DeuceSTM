package org.deuce.transform.util;

public class Util {
	final static private String ADDRESS_FIELD_POSTFIX = "__ADDRESS__";

	public static String getAddressField( String field) {
		return field + ADDRESS_FIELD_POSTFIX;
	}
}
