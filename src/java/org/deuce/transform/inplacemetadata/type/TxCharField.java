package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;

/**
 * In-place metadata class hierarchy.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@ExcludeInternal
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
