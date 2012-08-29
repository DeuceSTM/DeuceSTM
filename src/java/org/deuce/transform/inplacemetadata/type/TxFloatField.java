package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;

/**
 * In-place metadata class hierarchy.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@ExcludeInternal
public class TxFloatField extends TxField {

	public TxFloatField(Object ref, long address) {
		super(ref, address);
	}

	public final float read() {
		return UnsafeHolder.getUnsafe().getFloat(ref, address);
	}

	public final void write(float value) {
		UnsafeHolder.getUnsafe().putFloat(ref, address, value);
	}
}
