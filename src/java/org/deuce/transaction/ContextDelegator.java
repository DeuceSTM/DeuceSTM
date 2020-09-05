package org.deuce.transaction;

import org.deuce.InPlaceMetadata;
import org.deuce.objectweb.asm.Type;
import org.deuce.reflection.AddressUtil;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxArrBoolField;
import org.deuce.transform.inplacemetadata.type.TxArrByteField;
import org.deuce.transform.inplacemetadata.type.TxArrCharField;
import org.deuce.transform.inplacemetadata.type.TxArrDoubleField;
import org.deuce.transform.inplacemetadata.type.TxArrFloatField;
import org.deuce.transform.inplacemetadata.type.TxArrIntField;
import org.deuce.transform.inplacemetadata.type.TxArrLongField;
import org.deuce.transform.inplacemetadata.type.TxArrObjectField;
import org.deuce.transform.inplacemetadata.type.TxArrShortField;
import org.deuce.transform.inplacemetadata.type.TxField;

/**
 * Cluster static delegate methods.
 * These methods delegates calls from the dynamic generated code to the context.
 * 
 * 
 * @author	Guy Korland, Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 * @since	1.0
 *
 */
public class ContextDelegator {

	final static public String CONTEXT_DELEGATOR_INTERNAL = Type.getInternalName(ContextDelegator.class);
	
	final static public String BEFORE_READ_METHOD_NAME = "beforeReadAccess";
	final static public String BEFORE_READ_METHOD_DESC = "(Ljava/lang/Object;J" + Context.CONTEXT_DESC +")V";
	final static public String IRREVOCABLE_METHOD_NAME = "onIrrevocableAccess";
	final static public String IRREVOCABLE_METHOD_DESC = "(" + Context.CONTEXT_DESC + ")V";
	
	final static public String WRITE_METHOD_NAME = "onWriteAccess";
	final static public String WRITE_ARR_METHOD_NAME = "onArrayWriteAccess";
	final static public String STATIC_WRITE_METHOD_NAME = "addStaticWriteAccess";
	final static public String READ_METHOD_NAME = "onReadAccess";
	final static public String READ_ARR_METHOD_NAME = "onArrayReadAccess";

	final static private String WRITE_METHOD_BOOLEAN_DESC = "(Ljava/lang/Object;ZJ" + Context.CONTEXT_DESC +")V";
	final static private String WRITE_METHOD_BYTE_DESC = "(Ljava/lang/Object;BJ" + Context.CONTEXT_DESC +")V";
	final static private String WRITE_METHOD_CHAR_DESC = "(Ljava/lang/Object;CJ" + Context.CONTEXT_DESC +")V";
	final static private String WRITE_METHOD_SHORT_DESC = "(Ljava/lang/Object;SJ" + Context.CONTEXT_DESC +")V";
	final static private String WRITE_METHOD_INT_DESC = "(Ljava/lang/Object;IJ" + Context.CONTEXT_DESC +")V";
	final static private String WRITE_METHOD_LONG_DESC = "(Ljava/lang/Object;JJ" + Context.CONTEXT_DESC +")V";
	final static private String WRITE_METHOD_FLOAT_DESC = "(Ljava/lang/Object;FJ" + Context.CONTEXT_DESC +")V";
	final static private String WRITE_METHOD_DOUBLE_DESC = "(Ljava/lang/Object;DJ" + Context.CONTEXT_DESC +")V";
	final static private String WRITE_METHOD_OBJ_DESC = "(Ljava/lang/Object;Ljava/lang/Object;J" + Context.CONTEXT_DESC +")V";

	final static private String STATIC_WRITE_METHOD_BOOLEAN_DESC = "(ZLjava/lang/Object;J" + Context.CONTEXT_DESC +")V";
	final static private String STATIC_WRITE_METHOD_BYTE_DESC = "(BLjava/lang/Object;J" + Context.CONTEXT_DESC +")V";
	final static private String STATIC_WRITE_METHOD_CHAR_DESC = "(CLjava/lang/Object;J" + Context.CONTEXT_DESC +")V";
	final static private String STATIC_WRITE_METHOD_SHORT_DESC = "(SLjava/lang/Object;J" + Context.CONTEXT_DESC +")V";
	final static private String STATIC_WRITE_METHOD_INT_DESC = "(ILjava/lang/Object;J" + Context.CONTEXT_DESC +")V";
	final static private String STATIC_WRITE_METHOD_LONG_DESC = "(JLjava/lang/Object;J" + Context.CONTEXT_DESC +")V";
	final static private String STATIC_WRITE_METHOD_FLOAT_DESC = "(FLjava/lang/Object;J" + Context.CONTEXT_DESC +")V";
	final static private String STATIC_WRITE_METHOD_DOUBLE_DESC = "(DLjava/lang/Object;J" + Context.CONTEXT_DESC +")V";
	final static private String STATIC_WRITE_METHOD_OBJ_DESC = "(Ljava/lang/Object;Ljava/lang/Object;J" + Context.CONTEXT_DESC +")V";

	final static private String READ_METHOD_BOOLEAN_DESC = "(Ljava/lang/Object;ZJ" + Context.CONTEXT_DESC +")Z";
	final static private String READ_METHOD_BYTE_DESC = "(Ljava/lang/Object;BJ" + Context.CONTEXT_DESC +")B";
	final static private String READ_METHOD_CHAR_DESC = "(Ljava/lang/Object;CJ" + Context.CONTEXT_DESC +")C";
	final static private String READ_METHOD_SHORT_DESC = "(Ljava/lang/Object;SJ" + Context.CONTEXT_DESC +")S";
	final static private String READ_METHOD_INT_DESC = "(Ljava/lang/Object;IJ" + Context.CONTEXT_DESC +")I";
	final static private String READ_METHOD_LONG_DESC = "(Ljava/lang/Object;JJ" + Context.CONTEXT_DESC +")J";
	final static private String READ_METHOD_FLOAT_DESC = "(Ljava/lang/Object;FJ" + Context.CONTEXT_DESC +")F";
	final static private String READ_METHOD_DOUBLE_DESC = "(Ljava/lang/Object;DJ" + Context.CONTEXT_DESC +")D";
	final static private String READ_METHOD_OBJ_DESC = "(Ljava/lang/Object;Ljava/lang/Object;J" + Context.CONTEXT_DESC +")Ljava/lang/Object;";

	final static public String WRITE_ARRAY_METHOD_BYTE_DESC = "([BIB" + Context.CONTEXT_DESC +")V";
	final static public String WRITE_ARRAY_METHOD_CHAR_DESC = "([CIC" + Context.CONTEXT_DESC +")V";
	final static public String WRITE_ARRAY_METHOD_SHORT_DESC = "([SIS" + Context.CONTEXT_DESC +")V";
	final static public String WRITE_ARRAY_METHOD_INT_DESC = "([III" + Context.CONTEXT_DESC +")V";
	final static public String WRITE_ARRAY_METHOD_LONG_DESC = "([JIJ" + Context.CONTEXT_DESC +")V";
	final static public String WRITE_ARRAY_METHOD_FLOAT_DESC = "([FIF" + Context.CONTEXT_DESC +")V";
	final static public String WRITE_ARRAY_METHOD_DOUBLE_DESC = "([DID" + Context.CONTEXT_DESC +")V";
	final static public String WRITE_ARRAY_METHOD_OBJ_DESC = "([Ljava/lang/Object;ILjava/lang/Object;" + Context.CONTEXT_DESC +")V";

	final static public String READ_ARRAY_METHOD_BYTE_DESC = "([BI" + Context.CONTEXT_DESC +")B";
	final static public String READ_ARRAY_METHOD_CHAR_DESC = "([CI" + Context.CONTEXT_DESC +")C";
	final static public String READ_ARRAY_METHOD_SHORT_DESC = "([SI" + Context.CONTEXT_DESC +")S";
	final static public String READ_ARRAY_METHOD_INT_DESC = "([II" + Context.CONTEXT_DESC +")I";
	final static public String READ_ARRAY_METHOD_LONG_DESC = "([JI" + Context.CONTEXT_DESC +")J";
	final static public String READ_ARRAY_METHOD_FLOAT_DESC = "([FI" + Context.CONTEXT_DESC +")F";
	final static public String READ_ARRAY_METHOD_DOUBLE_DESC = "([DI" + Context.CONTEXT_DESC +")D";
	final static public String READ_ARRAY_METHOD_OBJ_DESC = "([Ljava/lang/Object;I" + Context.CONTEXT_DESC +")Ljava/lang/Object;";


	final static private int BYTE_ARR_BASE = AddressUtil.arrayBaseOffset(byte[].class);
	final static private int CHAR_ARR_BASE = AddressUtil.arrayBaseOffset(char[].class);
	final static private int SHORT_ARR_BASE = AddressUtil.arrayBaseOffset(short[].class);
	final static private int INT_ARR_BASE = AddressUtil.arrayBaseOffset(int[].class);
	final static private int LONG_ARR_BASE = AddressUtil.arrayBaseOffset(long[].class);
	final static private int FLOAT_ARR_BASE = AddressUtil.arrayBaseOffset(float[].class);
	final static private int DOUBLE_ARR_BASE = AddressUtil.arrayBaseOffset(double[].class);
	final static private int OBJECT_ARR_BASE = AddressUtil.arrayBaseOffset(Object[].class);

	final static private int BYTE_ARR_SCALE = AddressUtil.arrayIndexScale(byte[].class);
	final static private int CHAR_ARR_SCALE = AddressUtil.arrayIndexScale(char[].class);
	final static private int SHORT_ARR_SCALE = AddressUtil.arrayIndexScale(short[].class);
	final static private int INT_ARR_SCALE = AddressUtil.arrayIndexScale(int[].class);
	final static private int LONG_ARR_SCALE = AddressUtil.arrayIndexScale(long[].class);
	final static private int FLOAT_ARR_SCALE = AddressUtil.arrayIndexScale(float[].class);
	final static private int DOUBLE_ARR_SCALE = AddressUtil.arrayIndexScale(double[].class);
	final static private int OBJECT_ARR_SCALE = AddressUtil.arrayIndexScale(Object[].class);

	final static public String TXFIELD_DESCRIPTOR = Type.getDescriptor(TxField.class);
	static public String TX_OBJECT_FIELD_DESCRIPTOR = Type.getDescriptor(TxArrObjectField.class);
	static public String TX_INT_FIELD_DESCRIPTOR = Type.getDescriptor(TxArrIntField.class);
	static public String TX_SHORT_FIELD_DESCRIPTOR = Type.getDescriptor(TxArrShortField.class);
	static public String TX_FLOAT_FIELD_DESCRIPTOR = Type.getDescriptor(TxArrFloatField.class);
	static public String TX_CHAR_FIELD_DESCRIPTOR = Type.getDescriptor(TxArrCharField.class);
	static public String TX_BOOL_FIELD_DESCRIPTOR = Type.getDescriptor(TxArrBoolField.class);
	static public String TX_BYTE_FIELD_DESCRIPTOR = Type.getDescriptor(TxArrByteField.class);
	static public String TX_LONG_FIELD_DESCRIPTOR = Type.getDescriptor(TxArrLongField.class);
	static public String TX_DOUBLE_FIELD_DESCRIPTOR = Type.getDescriptor(TxArrDoubleField.class);

	static public String BEFORE_READ_METHOD_DESC_META = "(" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";

	static private String WRITE_METHOD_BOOLEAN_DESC_META = "(Z" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String WRITE_METHOD_BYTE_DESC_META = "(B" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String WRITE_METHOD_CHAR_DESC_META = "(C" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String WRITE_METHOD_SHORT_DESC_META = "(S" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String WRITE_METHOD_INT_DESC_META = "(I" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String WRITE_METHOD_LONG_DESC_META = "(J" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String WRITE_METHOD_FLOAT_DESC_META = "(F" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String WRITE_METHOD_DOUBLE_DESC_META = "(D" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String WRITE_METHOD_OBJ_DESC_META = "(Ljava/lang/Object;" + TXFIELD_DESCRIPTOR
			+ Context.CONTEXT_DESC + ")V";

	static private String STATIC_WRITE_METHOD_BOOLEAN_DESC_META = "(Z" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC
			+ ")V";
	static private String STATIC_WRITE_METHOD_BYTE_DESC_META = "(B" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String STATIC_WRITE_METHOD_CHAR_DESC_META = "(C" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String STATIC_WRITE_METHOD_SHORT_DESC_META = "(S" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String STATIC_WRITE_METHOD_INT_DESC_META = "(I" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String STATIC_WRITE_METHOD_LONG_DESC_META = "(J" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String STATIC_WRITE_METHOD_FLOAT_DESC_META = "(F" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")V";
	static private String STATIC_WRITE_METHOD_DOUBLE_DESC_META = "(D" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC
			+ ")V";
	static private String STATIC_WRITE_METHOD_OBJ_DESC_META = "(Ljava/lang/Object;" + TXFIELD_DESCRIPTOR
			+ Context.CONTEXT_DESC + ")V";

	static private String READ_METHOD_BOOLEAN_DESC_META = "(Z" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")Z";
	static private String READ_METHOD_BYTE_DESC_META = "(B" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")B";
	static private String READ_METHOD_CHAR_DESC_META = "(C" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")C";
	static private String READ_METHOD_SHORT_DESC_META = "(S" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")S";
	static private String READ_METHOD_INT_DESC_META = "(I" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")I";
	static private String READ_METHOD_LONG_DESC_META = "(J" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")J";
	static private String READ_METHOD_FLOAT_DESC_META = "(F" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")F";
	static private String READ_METHOD_DOUBLE_DESC_META = "(D" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC + ")D";
	static private String READ_METHOD_OBJ_DESC_META = "(Ljava/lang/Object;" + TXFIELD_DESCRIPTOR + Context.CONTEXT_DESC
			+ ")Ljava/lang/Object;";

	static public String WRITE_ARRAY_METHOD_BYTE_DESC_META = "([" + TX_BYTE_FIELD_DESCRIPTOR + "IB"
			+ Context.CONTEXT_DESC + ")V";
	static public String WRITE_ARRAY_METHOD_CHAR_DESC_META = "([" + TX_CHAR_FIELD_DESCRIPTOR + "IC"
			+ Context.CONTEXT_DESC + ")V";
	static public String WRITE_ARRAY_METHOD_SHORT_DESC_META = "([" + TX_SHORT_FIELD_DESCRIPTOR + "IS"
			+ Context.CONTEXT_DESC + ")V";
	static public String WRITE_ARRAY_METHOD_INT_DESC_META = "([" + TX_INT_FIELD_DESCRIPTOR + "II"
			+ Context.CONTEXT_DESC + ")V";
	static public String WRITE_ARRAY_METHOD_LONG_DESC_META = "([" + TX_LONG_FIELD_DESCRIPTOR + "IJ"
			+ Context.CONTEXT_DESC + ")V";
	static public String WRITE_ARRAY_METHOD_FLOAT_DESC_META = "([" + TX_FLOAT_FIELD_DESCRIPTOR + "IF"
			+ Context.CONTEXT_DESC + ")V";
	static public String WRITE_ARRAY_METHOD_DOUBLE_DESC_META = "([" + TX_DOUBLE_FIELD_DESCRIPTOR + "ID"
			+ Context.CONTEXT_DESC + ")V";
	static public String WRITE_ARRAY_METHOD_OBJ_DESC_META = "([" + TX_OBJECT_FIELD_DESCRIPTOR + "ILjava/lang/Object;"
			+ Context.CONTEXT_DESC + ")V";

	static public String READ_ARRAY_METHOD_BYTE_DESC_META = "([" + TX_BYTE_FIELD_DESCRIPTOR + "I"
			+ Context.CONTEXT_DESC + ")B";
	static public String READ_ARRAY_METHOD_CHAR_DESC_META = "([" + TX_CHAR_FIELD_DESCRIPTOR + "I"
			+ Context.CONTEXT_DESC + ")C";
	static public String READ_ARRAY_METHOD_SHORT_DESC_META = "([" + TX_SHORT_FIELD_DESCRIPTOR + "I"
			+ Context.CONTEXT_DESC + ")S";
	static public String READ_ARRAY_METHOD_INT_DESC_META = "([" + TX_INT_FIELD_DESCRIPTOR + "I" + Context.CONTEXT_DESC
			+ ")I";
	static public String READ_ARRAY_METHOD_LONG_DESC_META = "([" + TX_LONG_FIELD_DESCRIPTOR + "I"
			+ Context.CONTEXT_DESC + ")J";
	static public String READ_ARRAY_METHOD_FLOAT_DESC_META = "([" + TX_FLOAT_FIELD_DESCRIPTOR + "I"
			+ Context.CONTEXT_DESC + ")F";
	static public String READ_ARRAY_METHOD_DOUBLE_DESC_META = "([" + TX_DOUBLE_FIELD_DESCRIPTOR + "I"
			+ Context.CONTEXT_DESC + ")D";
	static public String READ_ARRAY_METHOD_OBJ_DESC_META = "([" + TX_OBJECT_FIELD_DESCRIPTOR + "I"
			+ Context.CONTEXT_DESC + ")Ljava/lang/Object;";

	private static boolean metadataInitialized = false;
	private static boolean inLocalMetadata = false;

	private static Class<?> fieldIntClass = null;
	private static Class<?> fieldShortClass = null;
	private static Class<?> fieldBooleanClass = null;
	private static Class<?> fieldByteClass = null;
	private static Class<?> fieldCharClass = null;
	private static Class<?> fieldFloatClass = null;
	private static Class<?> fieldLongClass = null;
	private static Class<?> fieldDoubleClass = null;
	private static Class<?> fieldObjectClass = null;

	private static Class<?> arrayIntClass = null;
	private static Class<?> arrayShortClass = null;
	private static Class<?> arrayByteClass = null;
	private static Class<?> arrayBooleanClass = null;
	private static Class<?> arrayCharClass = null;
	private static Class<?> arrayFloatClass = null;
	private static Class<?> arrayLongClass = null;
	private static Class<?> arrayDoubleClass = null;
	private static Class<?> arrayObjectClass = null;

	private static void metadataInitializer() {
		if (!metadataInitialized) {
			IContext c = THREAD_CONTEXT.get(); // force context loading
			InPlaceMetadata a = c.getClass().getAnnotation(InPlaceMetadata.class);
			if (a != null) {
				inLocalMetadata = true;
				try {
					fieldObjectClass = Class.forName(a.fieldObjectClass());
				} catch (ClassNotFoundException e) {
					fieldObjectClass = TxField.class;
				}
				try {
					fieldIntClass = Class.forName(a.fieldIntClass());
				} catch (ClassNotFoundException e) {
					fieldIntClass = TxField.class;
				}
				try {
					fieldShortClass = Class.forName(a.fieldShortClass());
				} catch (ClassNotFoundException e) {
					fieldShortClass = TxField.class;
				}
				try {
					fieldBooleanClass = Class.forName(a.fieldBooleanClass());
				} catch (ClassNotFoundException e) {
					fieldBooleanClass = TxField.class;
				}
				try {
					fieldByteClass = Class.forName(a.fieldByteClass());
				} catch (ClassNotFoundException e) {
					fieldByteClass = TxField.class;
				}
				try {
					fieldCharClass = Class.forName(a.fieldCharClass());
				} catch (ClassNotFoundException e) {
					fieldCharClass = TxField.class;
				}
				try {
					fieldFloatClass = Class.forName(a.fieldFloatClass());
				} catch (ClassNotFoundException e) {
					fieldFloatClass = TxField.class;
				}
				try {
					fieldLongClass = Class.forName(a.fieldLongClass());
				} catch (ClassNotFoundException e) {
					fieldLongClass = TxField.class;
				}
				try {
					fieldDoubleClass = Class.forName(a.fieldDoubleClass());
				} catch (ClassNotFoundException e) {
					fieldDoubleClass = TxField.class;
				}
				try {
					arrayIntClass = Class.forName(a.arrayIntClass());
				} catch (ClassNotFoundException e) {
					arrayIntClass = TxArrIntField.class;
				}
				try {
					arrayShortClass = Class.forName(a.arrayShortClass());
				} catch (ClassNotFoundException e) {
					arrayShortClass = TxArrShortField.class;
				}
				try {
					arrayByteClass = Class.forName(a.arrayByteClass());
				} catch (ClassNotFoundException e) {
					arrayByteClass = TxArrByteField.class;
				}
				try {
					arrayBooleanClass = Class.forName(a.arrayBooleanClass());
				} catch (ClassNotFoundException e) {
					arrayBooleanClass = TxArrBoolField.class;
				}
				try {
					arrayCharClass = Class.forName(a.arrayCharClass());
				} catch (ClassNotFoundException e) {
					arrayCharClass = TxArrCharField.class;
				}
				try {
					arrayFloatClass = Class.forName(a.arrayFloatClass());
				} catch (ClassNotFoundException e) {
					arrayFloatClass = TxArrFloatField.class;
				}
				try {
					arrayLongClass = Class.forName(a.arrayLongClass());
				} catch (ClassNotFoundException e) {
					arrayLongClass = TxArrLongField.class;
				}
				try {
					arrayDoubleClass = Class.forName(a.arrayDoubleClass());
				} catch (ClassNotFoundException e) {
					arrayDoubleClass = TxArrDoubleField.class;
				}
				try {
					arrayObjectClass = Class.forName(a.arrayObjectClass());
				} catch (ClassNotFoundException e) {
					arrayObjectClass = TxArrObjectField.class;
				}
			}

			metadataInitialized = true;
		}
	}

	public static boolean inLocalMetadata() {
		metadataInitializer();
		return inLocalMetadata;
	}

	public static Class<?> getFieldObjectClass() {
		metadataInitializer();
		return fieldObjectClass;
	}

	public static Class<?> getFieldIntClass() {
		metadataInitializer();
		return fieldIntClass;
	}

	public static Class<?> getFieldShortClass() {
		metadataInitializer();
		return fieldShortClass;
	}

	public static Class<?> getFieldCharClass() {
		metadataInitializer();
		return fieldCharClass;
	}

	public static Class<?> getFieldByteClass() {
		metadataInitializer();
		return fieldByteClass;
	}

	public static Class<?> getFieldBooleanClass() {
		metadataInitializer();
		return fieldBooleanClass;
	}

	public static Class<?> getFieldFloatClass() {
		metadataInitializer();
		return fieldFloatClass;
	}

	public static Class<?> getFieldLongClass() {
		metadataInitializer();
		return fieldLongClass;
	}

	public static Class<?> getFieldDoubleClass() {
		metadataInitializer();
		return fieldDoubleClass;
	}

	public static Class<?> getArrayIntClass() {
		metadataInitializer();
		return arrayIntClass;
	}

	public static Class<?> getArrayShortClass() {
		metadataInitializer();
		return arrayShortClass;
	}

	public static Class<?> getArrayByteClass() {
		metadataInitializer();
		return arrayByteClass;
	}

	public static Class<?> getArrayCharClass() {
		metadataInitializer();
		return arrayCharClass;
	}

	public static Class<?> getArrayBooleanClass() {
		metadataInitializer();
		return arrayBooleanClass;
	}

	public static Class<?> getArrayFloatClass() {
		metadataInitializer();
		return arrayFloatClass;
	}

	public static Class<?> getArrayLongClass() {
		metadataInitializer();
		return arrayLongClass;
	}

	public static Class<?> getArrayDoubleClass() {
		metadataInitializer();
		return arrayDoubleClass;
	}

	public static Class<?> getArrayObjectClass() {
		metadataInitializer();
		return arrayObjectClass;
	}

	final private static ContextThreadLocal THREAD_CONTEXT = new ContextThreadLocal();

	@ExcludeInternal
	private static class ContextThreadLocal extends ThreadLocal<IContext>
	{
		private Class<? extends IContext> contextClass;  

		public ContextThreadLocal(){
			String className = System.getProperty( "org.deuce.transaction.contextClass");
			if( className != null){
				try {
					this.contextClass = (Class<? extends IContext>) Class.forName(className);
					return;
				} catch (Exception e) {
					e.printStackTrace(); // TODO add logger
				}
			}
			this.contextClass = org.deuce.transaction.lsa.Context.class;
		}

		@Override
		protected synchronized IContext initialValue() {
			try {
				return this.contextClass.newInstance();
			} catch (Exception e) {
				throw new TransactionException( e);
			}
		}
	}

	public static IContext getInstance(){
		return THREAD_CONTEXT.get();
	}

	public static String getWriteMethodDesc( Type type) {
		if (inLocalMetadata()) {
			switch (type.getSort()) {
			case Type.BOOLEAN:
				return WRITE_METHOD_BOOLEAN_DESC_META;
			case Type.BYTE:
				return WRITE_METHOD_BYTE_DESC_META;
			case Type.CHAR:
				return WRITE_METHOD_CHAR_DESC_META;
			case Type.SHORT:
				return WRITE_METHOD_SHORT_DESC_META;
			case Type.INT:
				return WRITE_METHOD_INT_DESC_META;
			case Type.LONG:
				return WRITE_METHOD_LONG_DESC_META;
			case Type.FLOAT:
				return WRITE_METHOD_FLOAT_DESC_META;
			case Type.DOUBLE:
				return WRITE_METHOD_DOUBLE_DESC_META;
			default:
				return WRITE_METHOD_OBJ_DESC_META;
			}
		} else {
			switch (type.getSort()) {
			case Type.BOOLEAN:
				return WRITE_METHOD_BOOLEAN_DESC;
			case Type.BYTE:
				return WRITE_METHOD_BYTE_DESC;
			case Type.CHAR:
				return WRITE_METHOD_CHAR_DESC;
			case Type.SHORT:
				return WRITE_METHOD_SHORT_DESC;
			case Type.INT:
				return WRITE_METHOD_INT_DESC;
			case Type.LONG:
				return WRITE_METHOD_LONG_DESC;
			case Type.FLOAT:
				return WRITE_METHOD_FLOAT_DESC;
			case Type.DOUBLE:
				return WRITE_METHOD_DOUBLE_DESC;
			default:
				return WRITE_METHOD_OBJ_DESC;
			}
		}
	}

	public static String getStaticWriteMethodDesc(Type type) {
		if (inLocalMetadata()) {
			switch (type.getSort()) {
			case Type.BOOLEAN:
				return STATIC_WRITE_METHOD_BOOLEAN_DESC_META;
			case Type.BYTE:
				return STATIC_WRITE_METHOD_BYTE_DESC_META;
			case Type.CHAR:
				return STATIC_WRITE_METHOD_CHAR_DESC_META;
			case Type.SHORT:
				return STATIC_WRITE_METHOD_SHORT_DESC_META;
			case Type.INT:
				return STATIC_WRITE_METHOD_INT_DESC_META;
			case Type.LONG:
				return STATIC_WRITE_METHOD_LONG_DESC_META;
			case Type.FLOAT:
				return STATIC_WRITE_METHOD_FLOAT_DESC_META;
			case Type.DOUBLE:
				return STATIC_WRITE_METHOD_DOUBLE_DESC_META;
			default:
				return STATIC_WRITE_METHOD_OBJ_DESC_META;
			}
		} else {
			switch (type.getSort()) {
			case Type.BOOLEAN:
				return STATIC_WRITE_METHOD_BOOLEAN_DESC;
			case Type.BYTE:
				return STATIC_WRITE_METHOD_BYTE_DESC;
			case Type.CHAR:
				return STATIC_WRITE_METHOD_CHAR_DESC;
			case Type.SHORT:
				return STATIC_WRITE_METHOD_SHORT_DESC;
			case Type.INT:
				return STATIC_WRITE_METHOD_INT_DESC;
			case Type.LONG:
				return STATIC_WRITE_METHOD_LONG_DESC;
			case Type.FLOAT:
				return STATIC_WRITE_METHOD_FLOAT_DESC;
			case Type.DOUBLE:
				return STATIC_WRITE_METHOD_DOUBLE_DESC;
			default:
				return STATIC_WRITE_METHOD_OBJ_DESC;
			}
		}
	}

	public static String getReadMethodDesc(Type type) {
		if (inLocalMetadata()) {
			switch (type.getSort()) {
			case Type.BOOLEAN:
				return READ_METHOD_BOOLEAN_DESC_META;
			case Type.BYTE:
				return READ_METHOD_BYTE_DESC_META;
			case Type.CHAR:
				return READ_METHOD_CHAR_DESC_META;
			case Type.SHORT:
				return READ_METHOD_SHORT_DESC_META;
			case Type.INT:
				return READ_METHOD_INT_DESC_META;
			case Type.LONG:
				return READ_METHOD_LONG_DESC_META;
			case Type.FLOAT:
				return READ_METHOD_FLOAT_DESC_META;
			case Type.DOUBLE:
				return READ_METHOD_DOUBLE_DESC_META;
			default:
				return READ_METHOD_OBJ_DESC_META;
			}
		} else {
			switch (type.getSort()) {
			case Type.BOOLEAN:
				return READ_METHOD_BOOLEAN_DESC;
			case Type.BYTE:
				return READ_METHOD_BYTE_DESC;
			case Type.CHAR:
				return READ_METHOD_CHAR_DESC;
			case Type.SHORT:
				return READ_METHOD_SHORT_DESC;
			case Type.INT:
				return READ_METHOD_INT_DESC;
			case Type.LONG:
				return READ_METHOD_LONG_DESC;
			case Type.FLOAT:
				return READ_METHOD_FLOAT_DESC;
			case Type.DOUBLE:
				return READ_METHOD_DOUBLE_DESC;
			default:
				return READ_METHOD_OBJ_DESC;
			}
		}
	}

	public static String getReadArrayMethodDesc(Type type) {
		if (inLocalMetadata()) {
			switch (type.getSort()) {
			case Type.BOOLEAN:
				return "onArrayBoolReadAccess";
			case Type.BYTE:
				return "onArrayByteReadAccess";
			case Type.CHAR:
				return "onArrayCharReadAccess";
			case Type.SHORT:
				return "onArrayShortReadAccess";
			case Type.INT:
				return "onArrayIntReadAccess";
			case Type.LONG:
				return "onArrayLongReadAccess";
			case Type.FLOAT:
				return "onArrayFloatReadAccess";
			case Type.DOUBLE:
				return "onArrayDoubleReadAccess";
			default:
				return "onArrayObjectReadAccess";
			}
		} else {
			return "<null>";
		}
	}

	static public void beforeReadAccess( Object obj, long field, IContext context) {
		((Context) context).beforeReadAccess(obj, field);
	}

	static public Object onReadAccess( Object obj, Object value, long field, IContext context) {
		return ((Context) context).onReadAccess(obj, value, field);
	}
	static public boolean onReadAccess( Object obj, boolean value, long field, IContext context) {
		return ((Context) context).onReadAccess(obj, value, field);
	}
	static public byte onReadAccess( Object obj, byte value, long field, IContext context) {
		return ((Context) context).onReadAccess(obj, value, field);
	}
	static public char onReadAccess( Object obj, char value, long field, IContext context) {
		return ((Context) context).onReadAccess(obj, value, field);
	}
	static public short onReadAccess( Object obj, short value, long field, IContext context) {
		return ((Context) context).onReadAccess(obj, value, field);
	}
	static public int onReadAccess( Object obj, int value, long field, IContext context) {
		return ((Context) context).onReadAccess(obj, value, field);
	}
	static public long onReadAccess( Object obj, long value, long field, IContext context) {
		return ((Context) context).onReadAccess(obj, value, field);
	}
	static public float onReadAccess( Object obj, float value, long field, IContext context) {
		return ((Context) context).onReadAccess(obj, value, field);
	}
	static public double onReadAccess( Object obj, double value, long field, IContext context) {
		return ((Context) context).onReadAccess(obj, value, field);
	}

	static public void onWriteAccess( Object obj, Object value, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, boolean value, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, byte value, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, char value, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, short value, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, int value, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, long value, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, float value, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, double value, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}

	static public void addStaticWriteAccess( Object value, Object obj, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void addStaticWriteAccess( boolean value, Object obj, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void addStaticWriteAccess( byte value, Object obj, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void addStaticWriteAccess( char value, Object obj, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void addStaticWriteAccess( short value, Object obj, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void addStaticWriteAccess( int value, Object obj, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void addStaticWriteAccess( long value, Object obj, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void addStaticWriteAccess( float value, Object obj, long field, IContext context) {
		((Context) context).onWriteAccess(obj, value, field);
	}
	static public void addStaticWriteAccess( double value, Object obj, long field, IContext context) { 
		((Context) context).onWriteAccess(obj, value, field);
	}

	static public Object onArrayReadAccess( Object[] arr, int index, IContext context) {
		int address = OBJECT_ARR_BASE + OBJECT_ARR_SCALE*index;
		((Context) context).beforeReadAccess(arr, address);
		return ((Context) context).onReadAccess(arr, arr[index], address);
	}
	static public byte onArrayReadAccess( byte[] arr, int index, IContext context) {
		int address = BYTE_ARR_BASE + BYTE_ARR_SCALE*index;
		((Context) context).beforeReadAccess(arr, address);
		return ((Context) context).onReadAccess(arr, arr[index], address);
	}
	static public char onArrayReadAccess( char[] arr, int index, IContext context) {
		int address = CHAR_ARR_BASE + CHAR_ARR_SCALE*index;
		((Context) context).beforeReadAccess(arr, address);
		return ((Context) context).onReadAccess(arr, arr[index], address);
	}
	static public short onArrayReadAccess( short[] arr, int index, IContext context) {
		int address = SHORT_ARR_BASE + SHORT_ARR_SCALE*index;
		((Context) context).beforeReadAccess(arr, address);
		return ((Context) context).onReadAccess(arr, arr[index], address);
	}
	static public int onArrayReadAccess( int[] arr, int index, IContext context) {
		int address = INT_ARR_BASE + INT_ARR_SCALE*index;
		((Context) context).beforeReadAccess(arr, address);
		return ((Context) context).onReadAccess(arr, arr[index], address);
	}
	static public long onArrayReadAccess( long[] arr, int index, IContext context) {
		int address = LONG_ARR_BASE + LONG_ARR_SCALE*index;
		((Context) context).beforeReadAccess(arr, address);
		return ((Context) context).onReadAccess(arr, arr[index], address);
	}
	static public float onArrayReadAccess( float[] arr, int index, IContext context) {
		int address = FLOAT_ARR_BASE + FLOAT_ARR_SCALE*index;
		((Context) context).beforeReadAccess(arr, address);
		return ((Context) context).onReadAccess(arr, arr[index], address);
	}
	static public double onArrayReadAccess( double[] arr, int index, IContext context) {
		int address = DOUBLE_ARR_BASE + DOUBLE_ARR_SCALE*index;
		((Context) context).beforeReadAccess(arr, address);
		return ((Context) context).onReadAccess(arr, arr[index], address);
	}
	
	static public <T> void onArrayWriteAccess( T[] arr,  int index, T value, IContext context) {
		T t = arr[index]; // dummy access just to check the index in range
		((Context) context).onWriteAccess(arr, value, OBJECT_ARR_BASE + OBJECT_ARR_SCALE*index);
	}
	static public void onArrayWriteAccess( byte[] arr, int index, byte value, IContext context) {
		byte t = arr[index]; // dummy access just to check the index in range
		((Context) context).onWriteAccess(arr, value, BYTE_ARR_BASE + BYTE_ARR_SCALE*index);
	}
	static public void onArrayWriteAccess( char[] arr, int index, char value, IContext context) {
		char t = arr[index]; // dummy access just to check the index in range
		((Context) context).onWriteAccess(arr, value, CHAR_ARR_BASE + CHAR_ARR_SCALE*index);
	}
	static public void onArrayWriteAccess( short[] arr, int index, short value, IContext context) {
		short t = arr[index]; // dummy access just to check the index in range
		((Context) context).onWriteAccess(arr, value, SHORT_ARR_BASE + SHORT_ARR_SCALE*index);
	}
	static public void onArrayWriteAccess( int[] arr, int index, int value, IContext context) {
		int t = arr[index]; // dummy access just to check the index in range
		((Context) context).onWriteAccess(arr, value, INT_ARR_BASE + INT_ARR_SCALE*index);
	}
	static public void onArrayWriteAccess( long[] arr, int index, long value, IContext context) {
		long t = arr[index]; // dummy access just to check the index in range
		((Context) context).onWriteAccess(arr, value, LONG_ARR_BASE + LONG_ARR_SCALE*index);
	}
	static public void onArrayWriteAccess( float[] arr, int index, float value, IContext context) {
		float t = arr[index]; // dummy access just to check the index in range
		((Context) context).onWriteAccess(arr, value, FLOAT_ARR_BASE + FLOAT_ARR_SCALE*index);
	}
	static public void onArrayWriteAccess( double[] arr, int index, double value, IContext context) {
		double t = arr[index]; // dummy access just to check the index in range
		((Context) context).onWriteAccess(arr, value, DOUBLE_ARR_BASE + DOUBLE_ARR_SCALE*index);
	}
	
	static public void onIrrevocableAccess(IContext context) {
		context.onIrrevocableAccess();
	}
	
	static public void beforeReadAccess(TxField field, IContext context) {
		((ContextMetadata) context).beforeReadAccess(field);
	}
	
	static public Object onReadAccess(Object value, TxField field, IContext context) {
		return ((ContextMetadata) context).onReadAccess(value, field);
	}
	static public boolean onReadAccess(boolean value, TxField field, IContext context) {
		return ((ContextMetadata) context).onReadAccess(value, field);
	}
	static public byte onReadAccess(byte value, TxField field, IContext context) {
		return ((ContextMetadata) context).onReadAccess(value, field);
	}
	static public char onReadAccess(char value, TxField field, IContext context) {
		return ((ContextMetadata) context).onReadAccess(value, field);
	}
	static public short onReadAccess(short value, TxField field, IContext context) {
		return ((ContextMetadata) context).onReadAccess(value, field);
	}
	static public int onReadAccess(int value, TxField field, IContext context) {
		return ((ContextMetadata) context).onReadAccess(value, field);
	}
	static public long onReadAccess(long value, TxField field, IContext context) {
		return ((ContextMetadata) context).onReadAccess(value, field);
	}
	static public float onReadAccess(float value, TxField field, IContext context) {
		return ((ContextMetadata) context).onReadAccess(value, field);
	}
	static public double onReadAccess(double value, TxField field, IContext context) {
		return ((ContextMetadata) context).onReadAccess(value, field);
	}

	static public void onWriteAccess(Object value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void onWriteAccess(boolean value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void onWriteAccess(byte value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void onWriteAccess(char value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void onWriteAccess(short value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void onWriteAccess(int value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void onWriteAccess(long value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void onWriteAccess(float value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void onWriteAccess(double value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	
	static public void addStaticWriteAccess(Object value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void addStaticWriteAccess(boolean value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void addStaticWriteAccess(byte value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void addStaticWriteAccess(char value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void addStaticWriteAccess(short value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void addStaticWriteAccess(int value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void addStaticWriteAccess(long value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void addStaticWriteAccess(float value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}
	static public void addStaticWriteAccess(double value, TxField field, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, field);
	}

	static public Object onArrayObjectReadAccess(TxArrObjectField[] arr, int index, IContext context) {
		TxArrObjectField elem = arr[index];
		((ContextMetadata) context).beforeReadAccess(elem);
		return ((ContextMetadata) context).onReadAccess(elem.nextDim == null ? elem.array[index] : elem.nextDim, elem);
	}
	static public byte onArrayByteReadAccess(TxArrByteField[] arr, int index, IContext context) {
		TxArrByteField elem = arr[index];
		((ContextMetadata) context).beforeReadAccess(elem);
		return ((ContextMetadata) context).onReadAccess(elem.array[index], elem);
	}
	static public char onArrayCharReadAccess(TxArrCharField[] arr, int index, IContext context) {
		TxArrCharField elem = arr[index];
		((ContextMetadata) context).beforeReadAccess(elem);
		return ((ContextMetadata) context).onReadAccess(elem.array[index], elem);
	}
	static public short onArrayShortReadAccess(TxArrShortField[] arr, int index, IContext context) {
		TxArrShortField elem = arr[index];
		((ContextMetadata) context).beforeReadAccess(elem);
		return ((ContextMetadata) context).onReadAccess(elem.array[index], elem);
	}
	static public int onArrayIntReadAccess(TxArrIntField[] arr, int index, IContext context) {
		TxArrIntField elem = arr[index];
		((ContextMetadata) context).beforeReadAccess(elem);
		return ((ContextMetadata) context).onReadAccess(elem.array[index], elem);
	}
	static public long onArrayLongReadAccess(TxArrLongField[] arr, int index, IContext context) {
		TxArrLongField elem = arr[index];
		((ContextMetadata) context).beforeReadAccess(elem);
		return ((ContextMetadata) context).onReadAccess(elem.array[index], elem);
	}
	static public float onArrayFloatReadAccess(TxArrFloatField[] arr, int index, IContext context) {
		TxArrFloatField elem = arr[index];
		((ContextMetadata) context).beforeReadAccess(elem);
		return ((ContextMetadata) context).onReadAccess(elem.array[index], elem);
	}
	static public double onArrayDoubleReadAccess(TxArrDoubleField[] arr, int index, IContext context) {
		TxArrDoubleField elem = arr[index];
		((ContextMetadata) context).beforeReadAccess(elem);
		return ((ContextMetadata) context).onReadAccess(elem.array[index], elem);
	}

	static public <T> void onArrayWriteAccess(TxArrObjectField[] arr, int index, T value, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, arr[index]);
	}
	static public void onArrayWriteAccess(TxArrByteField[] arr, int index, byte value, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, arr[index]);
	}
	static public void onArrayWriteAccess(TxArrCharField[] arr, int index, char value, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, arr[index]);
	}
	static public void onArrayWriteAccess(TxArrShortField[] arr, int index, short value, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, arr[index]);
	}
	static public void onArrayWriteAccess(TxArrIntField[] arr, int index, int value, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, arr[index]);
	}
	static public void onArrayWriteAccess(TxArrLongField[] arr, int index, long value, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, arr[index]);
	}
	static public void onArrayWriteAccess(TxArrFloatField[] arr, int index, float value, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, arr[index]);
	}
	static public void onArrayWriteAccess(TxArrDoubleField[] arr, int index, double value, IContext context) {
		((ContextMetadata) context).onWriteAccess(value, arr[index]);
	}
}
