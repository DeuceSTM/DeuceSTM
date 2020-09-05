package org.deuce.transform.inplacemetadata.type;

import org.deuce.transform.ExcludeInternal;

/**
 * In-place metadata class hierarchy.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt>
 */
@ExcludeInternal
public class TxField {
	public Object ref;
	public final long address;

	public TxField(Object ref, long address) {
		this.ref = ref;
		this.address = address;
	}

}
