package org.deuce.transaction.jvstm.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VBoxB implements VBox {
	public VBoxBBody body = null;

	public VBoxB(byte value) {
		body = new VBoxBBody(value, 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxBBody newBody = makeNewBody(newValue, txNumber, this.body);
		this.body = newBody;
		return newBody;
	}

	private static VBoxBBody makeNewBody(Value value, int version,
			VBoxBBody next) {
		return new VBoxBBody(((ByteValue) value).value, version, next);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

}
