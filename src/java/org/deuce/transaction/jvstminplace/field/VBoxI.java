package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxIntField;

@ExcludeInternal
public class VBoxI extends TxIntField implements VBox {
	public VBoxIBody body = null;

	public VBoxI(Object ref, long address) {
		super(ref, address);
		body = new VBoxIBody(read(), 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxIBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();
		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;
		return newBody;
	}

	private static VBoxIBody makeNewBody(Value value, int version,
			VBoxIBody next) {
		return new VBoxIBody(((IntValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxIBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
