package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class TxArrDoubleField extends TxField {
	final static private int ARR_BASE = AddressUtil.arrayBaseOffset(double[].class);
	final static private int ARR_SCALE = AddressUtil.arrayIndexScale(double[].class);

	public double[] array;
	public int index;

	public TxArrDoubleField(double[] arr, int idx) {
		super(arr, ARR_BASE + ARR_SCALE * idx);
		array = arr;
		index = idx;
	}

	public final double read() {
		return array[index];
	}

	public final void write(double value) {
		array[index] = value;
	}
}
