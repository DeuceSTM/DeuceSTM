package org.deuce.transaction.capmem;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transaction.Context;
import org.deuce.transaction.ContextDelegator;
import org.deuce.transform.Exclude;
import org.deuce.transform.asm.ClassEnhancerCapturedState;
import org.deuce.transform.asm.ClassEnhancerCapturedStateArray;

/**
 * This class is similar to the ContextDelegator and defines the same event handlers.
 * Yet, it performs the following validation to elide barriers on captured memory:
 *   <code>(CapturedState)obj).owner == ((ContextFilterCapturedMem)context).trxFingerprint</code>
 *   
 * The obj parameter is the reference to the accessed object and its owner field 
 * refers to the transaction that instantiated it. On the other hand, the trxFingerprint 
 * field uniquely identifies the identity of the current transaction
 * 
 * The proper execution of this delegator admits that all transactional classes inherits 
 * from CapturedState and the context object may be an instance of the ContextFilterCapturedMem
 * class. To enable this two features the context object must be wrapped in a 
 * ContextFilterCapturedState and the transactional classes should be enhanced with the post 
 * transformation specified by ClassEnhancerCapturedState and ClassEnhancerCapturedStateArray.  
 * 
 * @author fmcarvalho <mcarvalho@cc.isel.ip.pt>
 */
@Exclude
public class ContextDelegatorCapturedState extends ContextDelegator{

	static{
		String postStr = ClassEnhancerCapturedState.class.getName();
		String capmem = System.getProperty("org.deuce.capmem");
		if(capmem != null && capmem.toUpperCase().equals("FULL"))
			postStr = postStr + "," + ClassEnhancerCapturedStateArray.class.getName();

		System.setProperty("org.deuce.transform.post", postStr);

		System.setProperty("org.deuce.transaction.filter",
				ContextFilterCapturedState.class.getName());
	}

	static public void beforeReadAccess( Object obj, long field, Context context) {
		/*
		 *  We must check if the accessed object obj is an instance of CapturedState, 
		 *  because this method is also a hook delegator for static fields accesses.
		 *  Yet, the owner class of the static fields does not inherit from CapturedState.     
		 */
		if((obj instanceof CapturedState) &&   
				(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint)) 
			return;
		context.beforeReadAccess(obj, field);
	}

	static public Object onReadAccess( Object obj, Object value, long field, Context context) {
		/*
		 *  We must check if the accessed object obj is an instance of CapturedState, 
		 *  because this method is also a hook delegator for static fields accesses.
		 *  Yet, the owner class of the static fields does not inherit from CapturedState.     
		 */	    
		if((obj instanceof CapturedState) && 
				(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint)) 
			return UnsafeHolder.getUnsafe().getObject(obj, field);

		return context.onReadAccess(obj, value, field);
	}

	static public boolean onReadAccess( Object obj, boolean value, long field, Context context) {
		/*
		 *  We must check if the accessed object obj is an instance of CapturedState, 
		 *  because this method is also a hook delegator for static fields accesses.
		 *  Yet, the owner class of the static fields does not inherit from CapturedState.     
		 */     
		if((obj instanceof CapturedState) && 
				(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint))
			return UnsafeHolder.getUnsafe().getBoolean(obj, field);
		else
			return context.onReadAccess(obj, value, field);
	}
	static public byte onReadAccess( Object obj, byte value, long field, Context context) {
		/*
		 *  We must check if the accessed object obj is an instance of CapturedState, 
		 *  because this method is also a hook delegator for static fields accesses.
		 *  Yet, the owner class of the static fields does not inherit from CapturedState.     
		 */     
		if((obj instanceof CapturedState) && 
				(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint))
			return UnsafeHolder.getUnsafe().getByte(obj, field);
		else
			return context.onReadAccess(obj, value, field);
	}
	static public char onReadAccess( Object obj, char value, long field, Context context) {
		/*
		 *  We must check if the accessed object obj is an instance of CapturedState, 
		 *  because this method is also a hook delegator for static fields accesses.
		 *  Yet, the owner class of the static fields does not inherit from CapturedState.     
		 */     
		if((obj instanceof CapturedState) && 
				(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint)) 
			return UnsafeHolder.getUnsafe().getChar(obj, field);
		else
			return context.onReadAccess(obj, value, field);
	}
	static public short onReadAccess( Object obj, short value, long field, Context context) {
		/*
		 *  We must check if the accessed object obj is an instance of CapturedState, 
		 *  because this method is also a hook delegator for static fields accesses.
		 *  Yet, the owner class of the static fields does not inherit from CapturedState.     
		 */     
		if((obj instanceof CapturedState) && 
				(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint)) 
			return UnsafeHolder.getUnsafe().getShort(obj, field);
		else
			return context.onReadAccess(obj, value, field);
	}
	static public int onReadAccess( Object obj, int value, long field, Context context) {
		/*
		 *  We must check if the accessed object obj is an instance of CapturedState, 
		 *  because this method is also a hook delegator for static fields accesses.
		 *  Yet, the owner class of the static fields does not inherit from CapturedState.     
		 */     
		if((obj instanceof CapturedState) && 
				(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint))
			return UnsafeHolder.getUnsafe().getInt(obj, field);
		else
			return context.onReadAccess(obj, value, field);
	}
	static public long onReadAccess( Object obj, long value, long field, Context context) {
		/*
		 *  We must check if the accessed object obj is an instance of CapturedState, 
		 *  because this method is also a hook delegator for static fields accesses.
		 *  Yet, the owner class of the static fields does not inherit from CapturedState.     
		 */     
		if((obj instanceof CapturedState) && 
				(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint)) 
			return UnsafeHolder.getUnsafe().getLong(obj, field);
		else
			return context.onReadAccess(obj, value, field);
	}
	static public float onReadAccess( Object obj, float value, long field, Context context) {
		/*
		 *  We must check if the accessed object obj is an instance of CapturedState, 
		 *  because this method is also a hook delegator for static fields accesses.
		 *  Yet, the owner class of the static fields does not inherit from CapturedState.     
		 */     
		if((obj instanceof CapturedState) && 
				(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint)) 
			return UnsafeHolder.getUnsafe().getFloat(obj, field);
		else
			return context.onReadAccess(obj, value, field);
	}
	static public double onReadAccess( Object obj, double value, long field, Context context) {
		/*
		 *  We must check if the accessed object obj is an instance of CapturedState, 
		 *  because this method is also a hook delegator for static fields accesses.
		 *  Yet, the owner class of the static fields does not inherit from CapturedState.     
		 */     
		if((obj instanceof CapturedState) && 
				(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint)) 
			return UnsafeHolder.getUnsafe().getDouble(obj, field);
		else
			return context.onReadAccess(obj, value, field);
	}

	static public void onWriteAccess( Object obj, Object value, long field, Context context) {
		if(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint) 
			UnsafeHolder.getUnsafe().putObject(obj, field, value);
		else
			context.onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, boolean value, long field, Context context) {
		if(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint)
			UnsafeHolder.getUnsafe().putBoolean(obj, field, value);
		else
			context.onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, byte value, long field, Context context) {
		if(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint) 
			UnsafeHolder.getUnsafe().putByte(obj, field, value);
		else
			context.onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, char value, long field, Context context) {
		if(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint) 
			UnsafeHolder.getUnsafe().putChar(obj, field, value);
		else
			context.onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, short value, long field, Context context) {
		if(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint) 
			UnsafeHolder.getUnsafe().putShort(obj, field, value);
		else
			context.onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, int value, long field, Context context) {
		if(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint) 
			UnsafeHolder.getUnsafe().putInt(obj, field, value);
		else
			context.onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, long value, long field, Context context) {
		if(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint) 
			UnsafeHolder.getUnsafe().putLong(obj, field, value);
		else
			context.onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, float value, long field, Context context) {
		if(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint) 
			UnsafeHolder.getUnsafe().putFloat(obj, field, value);
		else
			context.onWriteAccess(obj, value, field);
	}
	static public void onWriteAccess( Object obj, double value, long field, Context context) {
		if(((CapturedState)obj).owner == ((ContextFilterCapturedState)context).trxFingerprint) 
			UnsafeHolder.getUnsafe().putDouble(obj, field, value);
		else
			context.onWriteAccess(obj, value, field);
	}
	/*===================================================================================
	 ************************  arrays barriers for captured mem       ******************
	  ===================================================================================*/

	static public Object onArrayReadAccess( CapturedStateObjectArray ref, int index, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			return ref.elements[index];
		else
			return ContextDelegator.onArrayReadAccess(ref.elements, index, context);
	}
	static public byte onArrayReadAccess(CapturedStateByteArray ref, int index, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			return ref.elements[index];
		else
			return ContextDelegator.onArrayReadAccess(ref.elements, index, context);
	}
	static public char onArrayReadAccess(CapturedStateCharArray ref, int index, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			return ref.elements[index];
		else
			return ContextDelegator.onArrayReadAccess(ref.elements, index, context);
	}
	static public short onArrayReadAccess(CapturedStateShortArray ref, int index, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			return ref.elements[index];
		else
			return ContextDelegator.onArrayReadAccess(ref.elements, index, context);
	}
	static public int onArrayReadAccess(CapturedStateIntArray ref, int index, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			return ref.elements[index];
		else
			return ContextDelegator.onArrayReadAccess(ref.elements, index, context);
	}
	static public long onArrayReadAccess(CapturedStateLongArray ref, int index, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			return ref.elements[index];
		else
			return ContextDelegator.onArrayReadAccess(ref.elements, index, context);
	}
	static public float onArrayReadAccess(CapturedStateFloatArray ref, int index, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			return ref.elements[index];
		else
			return ContextDelegator.onArrayReadAccess(ref.elements, index, context);
	}
	static public double onArrayReadAccess(CapturedStateDoubleArray ref, int index, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			return ref.elements[index];
		else
			return ContextDelegator.onArrayReadAccess(ref.elements, index, context);
	}

	static public <T> void onArrayWriteAccess(CapturedStateObjectArray ref,  int index, T value, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			ref.elements[index] = value;
		else
			ContextDelegator.onArrayWriteAccess(ref.elements, index, value, context);
	}
	static public void onArrayWriteAccess( CapturedStateByteArray ref,  int index, byte value, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			ref.elements[index] = value;
		else
			ContextDelegator.onArrayWriteAccess(ref.elements, index, value, context);
	}
	static public void onArrayWriteAccess(CapturedStateCharArray ref,  int index, char value, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			ref.elements[index] = value;
		else
			ContextDelegator.onArrayWriteAccess(ref.elements, index, value, context);
	}
	static public void onArrayWriteAccess(CapturedStateShortArray ref,  int index, short value, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			ref.elements[index] = value;
		else
			ContextDelegator.onArrayWriteAccess(ref.elements, index, value, context);
	}
	static public void onArrayWriteAccess(CapturedStateIntArray ref,  int index, int value, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			ref.elements[index] = value;
		else
			ContextDelegator.onArrayWriteAccess(ref.elements, index, value, context);
	}
	static public void onArrayWriteAccess(CapturedStateLongArray ref,  int index, long value, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			ref.elements[index] = value;
		else
			ContextDelegator.onArrayWriteAccess(ref.elements, index, value, context);
	}
	static public void onArrayWriteAccess(CapturedStateFloatArray ref,  int index, float value, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			ref.elements[index] = value;
		else
			ContextDelegator.onArrayWriteAccess(ref.elements, index, value, context);
	}
	static public void onArrayWriteAccess(CapturedStateDoubleArray ref,  int index, double value, Context context) {
		if(ref.owner == ((ContextFilterCapturedState)context).trxFingerprint)
			ref.elements[index] = value;
		else
			ContextDelegator.onArrayWriteAccess(ref.elements, index, value, context);
	}	

	/*===================================================================================
	 ************************     barriers for static fields          ******************
	  ===================================================================================*/

	  /*
	   * Inherited from the base class ContextDelegator.
	   */

	   /*===================================================================================
	    ************************  *************************************  ******************
	  ===================================================================================*/

	static public void onIrrevocableAccess(Context context) {
		context.onIrrevocableAccess();
	}
}