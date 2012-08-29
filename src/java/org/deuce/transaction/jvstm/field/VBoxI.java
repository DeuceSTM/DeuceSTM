package org.deuce.transaction.jvstm.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VBoxI implements VBox {
	public VBoxIBody body = null;

	public VBoxI(int value) {
		body = new VBoxIBody(value, 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxIBody newBody = makeNewBody(newValue, txNumber, this.body);
		this.body = newBody;
		return newBody;
	}

	private static VBoxIBody makeNewBody(Value value, int version,
			VBoxIBody next) {
		return new VBoxIBody(((IntValue) value).value, version, next);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
