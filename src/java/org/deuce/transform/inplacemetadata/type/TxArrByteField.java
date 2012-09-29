package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.ExcludeInternal;

/**
 * In-place metadata class hierarchy.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@ExcludeInternal
public class TxArrByteField extends TxField {
	final static private int ARR_BASE = AddressUtil.arrayBaseOffset(byte[].class);
	final static private int ARR_SCALE = AddressUtil.arrayIndexScale(byte[].class);

	public byte[] array;
	public int index;

	public TxArrByteField(byte[] arr, int idx) {
		super(arr, ARR_BASE + ARR_SCALE * idx);
		array = arr;
		index = idx;
	}

	public final byte read() {
		return array[index];
	}

	public final void write(byte value) {
		array[index] = value;
	}
}
