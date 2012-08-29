package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxByteField;

@ExcludeInternal
public class VBoxB extends TxByteField implements VBox {
	public VBoxBBody body = null;

	public VBoxB(Object ref, long address) {
		super(ref, address);
		body = new VBoxBBody(read(), 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxBBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();
		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;

		return newBody;
	}

	private static VBoxBBody makeNewBody(Value value, int version,
			VBoxBBody next) {
		return new VBoxBBody(((ByteValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxBBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
