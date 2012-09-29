package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;

/**
 * In-place metadata class hierarchy.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@ExcludeInternal
public class TxLongField extends TxField {

	public TxLongField(Object ref, long address) {
		super(ref, address);
	}

	public final long read() {
		return UnsafeHolder.getUnsafe().getLong(ref, address);
	}

	public final void write(long value) {
		UnsafeHolder.getUnsafe().putLong(ref, address, value);
	}
}
