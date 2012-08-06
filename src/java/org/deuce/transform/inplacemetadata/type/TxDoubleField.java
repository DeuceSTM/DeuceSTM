package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;

public class TxDoubleField extends TxField {

	public TxDoubleField(Object ref, long address) {
		super(ref, address);
	}
	
	public final double read() {
		return UnsafeHolder.getUnsafe().getDouble(ref, address);
	}

	public final void write(double value) {
		UnsafeHolder.getUnsafe().putDouble(ref, address, value);
	}
}
