package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxArrBoolField;

@ExcludeInternal
public class VBoxArrZ extends TxArrBoolField implements VBox {
	public VBoxZBody body = null;

	public VBoxArrZ(boolean[] arr, int idx) {
		super(arr, idx);
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
