package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;

public class TxIntField extends TxField {

	public TxIntField(Object ref, long address) {
		super(ref, address);
	}
	
	public final int read() {
		return UnsafeHolder.getUnsafe().getInt(ref, address);
	}

	public final void write(int value) {
		UnsafeHolder.getUnsafe().putInt(ref, address, value);
	}
}
