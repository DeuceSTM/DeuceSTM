package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class ObjectWriteFieldAccess extends WriteFieldAccess {

	public Object value;

	public void set(Object value, VBox field) {
		super.init(field);
		this.value = value;
	}
	
	@Override
	public void put(int txNumber) {
		((VBoxO)field).commit(value, txNumber);
	}

	public Object getValue() {
		return value;
	}
}
