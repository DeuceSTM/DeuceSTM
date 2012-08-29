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
import org.deuce.transform.inplacemetadata.type.TxArrCharField;

@ExcludeInternal
public class VBoxArrC extends TxArrCharField implements VBox {
	public VBoxCBody body = null;

	public VBoxArrC(char[] arr, int idx) {
		super(arr, idx);
		body = new VBoxCBody(read(), 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxCBody newBody = makeNewBody(newValue, txNumber, this.body);

//		this.body.value = read();
		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;

		return newBody;
	}

	private static VBoxCBody makeNewBody(Value value, int version,
			VBoxCBody next) {
		return new VBoxCBody(((CharValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxCBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
