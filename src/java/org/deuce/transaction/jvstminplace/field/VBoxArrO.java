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
import org.deuce.transform.inplacemetadata.type.TxArrObjectField;

@ExcludeInternal
public class VBoxArrO extends TxArrObjectField implements VBox {
	public VBoxOBody body = null;

	public VBoxArrO(Object[] arr, int idx) {
		super(arr, idx);
		body = new VBoxOBody(read(), 0, null);
	}
	
	public VBoxArrO(Object[] arr, int idx, Object dummy) {
          super(arr, idx, dummy);
          body = new VBoxOBody(read(), 0, null);
        }

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxOBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();

		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;

		return newBody;
	}

	public static VBoxOBody makeNewBody(Value value, int version, VBoxOBody next) {
		return new VBoxOBody(((ObjectValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxOBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
