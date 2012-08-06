package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;

public class TxFloatField extends TxField {

	public TxFloatField(Object ref, long address) {
		super(ref, address);
	}
	
	public final float read() {
		return UnsafeHolder.getUnsafe().getFloat(ref, address);
	}

	public final void write(float value) {
		UnsafeHolder.getUnsafe().putFloat(ref, address, value);
	}
}
