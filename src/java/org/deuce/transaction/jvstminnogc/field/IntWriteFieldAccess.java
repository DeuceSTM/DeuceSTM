package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class IntWriteFieldAccess extends WriteFieldAccess {

	public int value;

	public void set(int value, VBox field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put(int txNumber) {
		((VBoxI)field).commit(value, txNumber);
	}

	public int getValue() {
		return value;
	}

}
