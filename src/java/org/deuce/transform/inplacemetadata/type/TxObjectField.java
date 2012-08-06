package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;

public class TxObjectField extends TxField {

	public TxObjectField(Object ref, long address) {
		super(ref, address);
	}
	
	public final Object read() {
		return UnsafeHolder.getUnsafe().getObject(ref, address);
	}

	public final void write(Object value) {
		UnsafeHolder.getUnsafe().putObject(ref, address, value);
	}
}
