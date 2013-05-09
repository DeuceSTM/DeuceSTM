package org.deuce.transaction.capmem;

import org.deuce.transaction.Context;


/**
 * To enable the capture analysis to arrays we must wrap them inside 
 * a captured state object defined by this class CapturedStateArrayBase.
 *    
 * @author fmcarvalho <mcarvalho@cc.isel.ip.pt>
 */
public abstract class CapturedStateArrayBase extends CapturedState{
	public CapturedStateArrayBase(Context ctx) {
		super(ctx);
	}

	public CapturedStateArrayBase() {
	}

	public abstract int arrayLength();

	public abstract void arraycopy(int srcPos, Object dest, int destPos, int length);
}
