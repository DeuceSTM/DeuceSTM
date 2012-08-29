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
import org.deuce.transform.inplacemetadata.type.TxBooleanField;

@ExcludeInternal
public class VBoxZ extends TxBooleanField implements VBox {
	public VBoxZBody body = null;

	public VBoxZ(Object ref, long address) {
		super(ref, address);
		body = new VBoxZBody(read(), 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxZBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();
		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;
		return newBody;
	}

	private static VBoxZBody makeNewBody(Value value, int version,
			VBoxZBody next) {
		return new VBoxZBody(((BoolValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxZBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
