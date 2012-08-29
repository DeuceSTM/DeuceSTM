package org.deuce.transaction.tl2inplace.field;

import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxField;

/**
 * Represents a base class for field write access.
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class ReadFieldAccess {
	public TxField field;

	public ReadFieldAccess() {
	}

	public ReadFieldAccess(TxField field) {
		init(field);
	}

	public void init(TxField field) {
		this.field = field;
	}

	@Override
	public boolean equals(Object obj) {
		ReadFieldAccess other = (ReadFieldAccess) obj;
		return field == other.field;
	}

	@Override
	final public int hashCode() {
		return field.hashCode();
	}

	public void clear() {
		field = null;
	}
}