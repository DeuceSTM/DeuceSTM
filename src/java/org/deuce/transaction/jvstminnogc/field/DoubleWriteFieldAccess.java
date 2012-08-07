package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

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
