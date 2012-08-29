package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.ExcludeInternal;

/**
 * In-place metadata class hierarchy.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@ExcludeInternal
public class TxArrShortField extends TxField {
	final static private int ARR_BASE = AddressUtil.arrayBaseOffset(short[].class);
	final static private int ARR_SCALE = AddressUtil.arrayIndexScale(short[].class);

	public short[] array;
	public int index;

	public TxArrShortField(short[] arr, int idx) {
		super(arr, ARR_BASE + ARR_SCALE * idx);
		array = arr;
		index = idx;
	}

	public final short read() {
		return array[index];
	}

	public final void write(short value) {
		array[index] = value;
	}
}
