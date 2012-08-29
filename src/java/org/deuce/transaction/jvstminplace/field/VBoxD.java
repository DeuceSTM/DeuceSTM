/**
 * JVSTM Implementation
 * 
 * JVSTM is a multiversion STM for Java developed by Joao Cachopo 
 * from INESC-ID.
 * 
 * @author Ricardo Dias
 */
package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxDoubleField;

@ExcludeInternal
public class VBoxD extends TxDoubleField implements VBox {
	public VBoxDBody body = null;

	public VBoxD(Object ref, long address) {
		super(ref, address);
		body = new VBoxDBody(read(), 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxDBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();
		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;
		return newBody;
	}

	private static VBoxDBody makeNewBody(Value value, int version,
			VBoxDBody next) {
		return new VBoxDBody(((DoubleValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxDBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
