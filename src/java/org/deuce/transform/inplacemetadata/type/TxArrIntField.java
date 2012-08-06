package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class TxArrIntField extends TxField {
	final static private int ARR_BASE = AddressUtil.arrayBaseOffset(int[].class);
	final static private int ARR_SCALE = AddressUtil.arrayIndexScale(int[].class);

	public int[] array;
	public int index;

	public TxArrIntField(int[] arr, int idx) {
		super(arr, ARR_BASE + ARR_SCALE * idx);
		array = arr;
		index = idx;
	}

	public final int read() {
		return array[index];
	}

	public final void write(int value) {
		array[index] = value;
	}
}
