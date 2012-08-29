package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxFloatField;

@ExcludeInternal
public class VBoxF extends TxFloatField implements VBox {
	public VBoxFBody body = null;

	public VBoxF(Object ref, long address) {
		super(ref, address);
		body = new VBoxFBody(read(), 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxFBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();
		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;
		return newBody;
	}

	private static VBoxFBody makeNewBody(Value value, int version,
			VBoxFBody next) {
		return new VBoxFBody(((FloatValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxFBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
