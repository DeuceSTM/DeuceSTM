package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;

/**
 * In-place metadata class hierarchy.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@ExcludeInternal
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
