package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class ByteWriteFieldAccess extends WriteFieldAccess {

	public byte value;

	public void set(byte value, VBox field) {
		super.init(field);
		this.value = value;
	}

	@Override
	public void put(int txNumber) {
		((VBoxB)field).commit(value, txNumber);
	}

	public byte getValue() {
		return value;
	}

}
