package org.deuce.transaction.jvstm.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VBoxD implements VBox {
	public VBoxDBody body = null;

	public VBoxD(double value) {
		body = new VBoxDBody(value, 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxDBody newBody = makeNewBody(newValue, txNumber, this.body);
		this.body = newBody;
		return newBody;
	}

	private static VBoxDBody makeNewBody(Value value, int version,
			VBoxDBody next) {
		return new VBoxDBody(((DoubleValue) value).value, version, next);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
