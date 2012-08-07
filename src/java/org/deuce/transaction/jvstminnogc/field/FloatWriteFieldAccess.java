package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class FloatWriteFieldAccess extends WriteFieldAccess {

	public float value;

	public void set(float value, VBox field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put(int txNumber) {
		((VBoxF)field).commit(value, txNumber);
	}

	public float getValue() {
		return value;
	}

}
