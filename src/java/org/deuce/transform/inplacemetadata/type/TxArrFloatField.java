package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class TxArrFloatField extends TxField {
	final static private int ARR_BASE = AddressUtil.arrayBaseOffset(float[].class);
	final static private int ARR_SCALE = AddressUtil.arrayIndexScale(float[].class);

	public float[] array;
	public int index;

	public TxArrFloatField(float[] arr, int idx) {
		super(arr, ARR_BASE + ARR_SCALE * idx);
		array = arr;
		index = idx;
	}

	public final float read() {
		return array[index];
	}

	public final void write(float value) {
		array[index] = value;
	}
}
