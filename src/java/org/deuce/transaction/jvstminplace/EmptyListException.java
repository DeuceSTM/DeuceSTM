package org.deuce.transaction.jvstminplace;

import org.deuce.transform.ExcludeInternal;


@ExcludeInternal
public class EmptyListException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EmptyListException() {
	super();
    }

    public EmptyListException(String msg) {
	super(msg);
    }
}
