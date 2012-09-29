package org.deuce.transaction.tl2inplace.field;

import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxField;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class LongWriteFieldAccess extends WriteFieldAccess {

	private long value;

	public void set(long value, TxField field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put() {
		UnsafeHolder.getUnsafe().putLong(field.ref, field.address, value);
		// clear();
	}

	public long getValue() {
		return value;
	}

}
