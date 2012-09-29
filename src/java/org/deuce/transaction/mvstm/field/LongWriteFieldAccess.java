package org.deuce.transaction.mvstm.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class LongWriteFieldAccess extends WriteFieldAccess {

	public long value;

	public void set(long value, VBox field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put(int txNumber) {
		((VBoxL)field).commit(value, txNumber);
	}

	public long getValue() {
		return value;
	}

}
