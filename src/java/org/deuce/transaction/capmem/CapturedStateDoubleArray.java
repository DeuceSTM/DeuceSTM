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
public class CapturedStateDoubleArray extends CapturedStateArrayBase{
	public final double [] elements;

	public CapturedStateDoubleArray(double [] elements) {
		super();
		this.elements = elements;
	}

	public CapturedStateDoubleArray(int length, Context ctx) {
		super(ctx);
		this.elements = new double [length];
	}

	public CapturedStateDoubleArray(double [] elements, Context ctx) {
		super(ctx);
		this.elements = elements;
	}


	@Override
	public int arrayLength() {
		return elements.length;
	}

	@Override
	public void arraycopy(int srcPos, Object dest, int destPos, int length) {
		System.arraycopy(this.elements, srcPos, ((CapturedStateDoubleArray)dest).elements, destPos, length);
	}
}
