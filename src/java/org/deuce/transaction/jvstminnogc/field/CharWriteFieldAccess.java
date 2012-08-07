package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class CharWriteFieldAccess extends WriteFieldAccess {

	public char value;

	public void set(char value, VBox field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put(int txNumber) {
		((VBoxC)field).commit(value, txNumber);
	}

	public char getValue() {
		return value;
	}

}
