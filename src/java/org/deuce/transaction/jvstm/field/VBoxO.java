package org.deuce.transaction.jvstm.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VBoxO implements VBox {
	public VBoxOBody body = null;

	public VBoxO(Object value) {
		body = new VBoxOBody(value, 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxOBody newBody = makeNewBody(newValue, txNumber, this.body);
		this.body = newBody;

		return newBody;
	}

	public static VBoxOBody makeNewBody(Value value, int version, VBoxOBody next) {
		return new VBoxOBody(((ObjectValue) value).value, version, next);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
