package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxShortField;

@ExcludeInternal
public class VBoxS extends TxShortField implements VBox {
	public VBoxSBody body = null;

	public VBoxS(Object ref, long address) {
		super(ref, address);
		body = new VBoxSBody(read(), 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxSBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();
		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;
		return newBody;
	}

	public static VBoxSBody makeNewBody(Value value, int version, VBoxSBody next) {
		return new VBoxSBody(((ShortValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxSBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
