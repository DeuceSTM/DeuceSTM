package org.deuce.transaction.jvstm.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VBoxZ implements VBox {
	public VBoxZBody body = null;

	public VBoxZ(boolean value) {
		body = new VBoxZBody(value, 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxZBody newBody = makeNewBody(newValue, txNumber, this.body);
		this.body = newBody;
		return newBody;
	}

	private static VBoxZBody makeNewBody(Value value, int version,
			VBoxZBody next) {
		return new VBoxZBody(((BoolValue) value).value, version, next);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
