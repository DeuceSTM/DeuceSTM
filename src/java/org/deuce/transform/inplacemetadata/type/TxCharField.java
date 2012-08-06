package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;

public class TxCharField extends TxField {

	public TxCharField(Object ref, long address) {
		super(ref, address);
	}

	public final char read() {
		return UnsafeHolder.getUnsafe().getChar(ref, address);
	}

	public final void write(char value) {
		UnsafeHolder.getUnsafe().putChar(ref, address, value);
	}
}
