package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.ExcludeInternal;

/**
 * In-place metadata class hierarchy.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@ExcludeInternal
public class TxArrCharField extends TxField {
	final static private int ARR_BASE = AddressUtil.arrayBaseOffset(char[].class);
	final static private int ARR_SCALE = AddressUtil.arrayIndexScale(char[].class);

	public char[] array;
	public int index;

	public TxArrCharField(char[] arr, int idx) {
		super(arr, ARR_BASE + ARR_SCALE * idx);
		array = arr;
		index = idx;
	}

	public final char read() {
		return array[index];
	}

	public final void write(char value) {
		array[index] = value;
	}
}
