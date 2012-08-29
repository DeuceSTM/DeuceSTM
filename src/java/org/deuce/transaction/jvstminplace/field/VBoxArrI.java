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
import org.deuce.transform.inplacemetadata.type.TxArrIntField;

@ExcludeInternal
public class VBoxArrI extends TxArrIntField implements VBox {
	public VBoxIBody body = null;

	public VBoxArrI(int[] arr, int idx) {
		super(arr, idx);
		body = new VBoxIBody(read(), 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxIBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();
		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;
		return newBody;
	}

	private static VBoxIBody makeNewBody(Value value, int version,
			VBoxIBody next) {
		return new VBoxIBody(((IntValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxIBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
