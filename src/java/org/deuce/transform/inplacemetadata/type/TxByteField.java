package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;

/**
 * In-place metadata class hierarchy.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@ExcludeInternal
public class TxByteField extends TxField {

	public TxByteField(Object ref, long address) {
		super(ref, address);
	}

	public final byte read() {
		return UnsafeHolder.getUnsafe().getByte(ref, address);
	}

	public final void write(byte value) {
		UnsafeHolder.getUnsafe().putByte(ref, address, value);
	}
}
