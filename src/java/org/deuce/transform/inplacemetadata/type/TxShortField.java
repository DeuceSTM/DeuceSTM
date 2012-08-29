package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;

/**
 * In-place metadata class hierarchy.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@ExcludeInternal
public class TxShortField extends TxField {

	public TxShortField(Object ref, long address) {
		super(ref, address);
	}

	public final short read() {
		return UnsafeHolder.getUnsafe().getShort(ref, address);
	}

	public final void write(short value) {
		UnsafeHolder.getUnsafe().putShort(ref, address, value);
	}
}
