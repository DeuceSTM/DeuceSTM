package org.deuce.transaction.mvstm.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class DoubleWriteFieldAccess extends WriteFieldAccess {

	public double value;

	public void set(double value, VBox field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put(int txNumber) {
		((VBoxD)field).commit(value, txNumber);
	}

	public double getValue() {
		return value;
	}
}
