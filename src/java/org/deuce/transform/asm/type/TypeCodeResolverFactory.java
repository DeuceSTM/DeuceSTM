package org.deuce.transform.asm.type;

import org.deuce.objectweb.asm.Type;

/**
 * Converts {@link Type} to {@link TypeCodeResolver}
 * @author Guy Korland
 *
 * @since 1.0
 */
public class TypeCodeResolverFactory {

	/**
	 * Static factory
	 */
	private TypeCodeResolverFactory() {}

	final private static IntTypeCodeResolver intResolver = 
		new IntTypeCodeResolver();
	final private static LongTypeCodeResolver longResolver = 
		new LongTypeCodeResolver();
	final private static FloatTypeCodeResolver floatResolver = 
		new FloatTypeCodeResolver();
	final private static DoubleTypeCodeResolver doubleResolver = 
		new DoubleTypeCodeResolver();
	final private static ReferenceTypeCodeResolver referenceResolver = 
		new ReferenceTypeCodeResolver();

	static public TypeCodeResolver getReolver( Type type) {
		switch( type.getSort()) {
		case Type.VOID:
			return null;
		case Type.BOOLEAN:
		case Type.BYTE:
		case Type.CHAR:
		case Type.SHORT:
		case Type.INT:
			return intResolver;
		case Type.LONG:
			return longResolver;
		case Type.FLOAT:
			return floatResolver;
		case Type.DOUBLE:
			return doubleResolver;
		default:
			return referenceResolver;
		}
	}

}
