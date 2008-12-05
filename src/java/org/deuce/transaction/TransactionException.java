package org.deuce.transaction;

import org.deuce.objectweb.asm.Type;
import org.deuce.transform.Exclude;

@Exclude
public class TransactionException extends RuntimeException {

	final static public String TRANSACTION_EXCEPTION_INTERNAL = Type.getInternalName(TransactionException.class);
	
	public TransactionException(){}

	public TransactionException( String msg){
		super(msg);
	}

	public TransactionException( Throwable cause){
		super(cause);
	}

	@Override 
	public Throwable fillInStackTrace(){ return null;} // light exception with no stack trace
	
	@Override
	public Throwable initCause(Throwable cause) {
		throw new IllegalStateException("Can't set cause.");
	}
}
