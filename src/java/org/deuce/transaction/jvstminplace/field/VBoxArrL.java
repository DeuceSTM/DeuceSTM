package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxArrLongField;

@ExcludeInternal
public class VBoxArrL extends TxArrLongField implements VBox {
	public VBoxLBody body = null;

	public VBoxArrL(long[] arr, int idx) {
		super(arr, idx);
		body = new VBoxLBody(read(), 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxLBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();
		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;
		return newBody;
	}

	private static VBoxLBody makeNewBody(Value value, int version,
			VBoxLBody next) {
		return new VBoxLBody(((LongValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxLBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
