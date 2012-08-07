package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

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
