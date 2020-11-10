package org.deuce.transaction.capmem;

import org.deuce.transaction.Context;
import org.deuce.transform.Exclude;


/**
 * The instances of this class encapsulate an array 
 * and keep track of its captured state.  
 *  
 * @author fmcarvalho <mcarvalho@cc.isel.ip.pt>
 */
@Exclude
public class CapturedStateShortArray extends CapturedStateArrayBase{
	public final short [] elements;

	public CapturedStateShortArray(short [] elements) {
		super();
		this.elements = elements;
	}

	public CapturedStateShortArray(int length, Context ctx) {
		super(ctx);
		this.elements = new short [length];
	}

	public CapturedStateShortArray(short [] elements, Context ctx) {
		super(ctx);
		this.elements = elements;
	}

	@Override
	public int arrayLength() {
		return elements.length;
	}

	@Override
	public void arraycopy(int srcPos, Object dest, int destPos, int length) {
		System.arraycopy(this.elements, srcPos, ((CapturedStateShortArray)dest).elements, destPos, length);
	}
}
