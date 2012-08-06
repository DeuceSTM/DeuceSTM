package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;

public class TxBooleanField extends TxField {

	public TxBooleanField(Object ref, long address) {
		super(ref, address);
	}

	public final boolean read() {
		return UnsafeHolder.getUnsafe().getBoolean(ref, address);
	}

	public final void write(boolean value) {
		UnsafeHolder.getUnsafe().putBoolean(ref, address, value);
	}
}
