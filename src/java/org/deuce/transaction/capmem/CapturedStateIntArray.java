package org.deuce.transaction.capmem;

import org.deuce.transaction.Context;
import org.deuce.transform.Exclude;


/**
 * The instances of this class encapsulate an array 
 * and keep track its captured state.  
 *  
 * @author fmcarvalho <mcarvalho@cc.isel.ip.pt>
 */
@Exclude
public class CapturedStateIntArray extends CapturedStateArrayBase{
	public final int [] elements;

	public CapturedStateIntArray(int [] elements) {
		super();
		this.elements = elements;
	}

	public CapturedStateIntArray(int length, Context ctx) {
		super(ctx);
		this.elements = new int [length];
	}

	public CapturedStateIntArray(int [] elements, Context ctx) {
		super(ctx);
		this.elements = elements;
	}

	@Override
	public int arrayLength() {
		return elements.length;
	}

	@Override
	public void arraycopy(int srcPos, Object dest, int destPos, int length) {
		System.arraycopy(this.elements, srcPos, ((CapturedStateIntArray)dest).elements, destPos, length);
	}
}
