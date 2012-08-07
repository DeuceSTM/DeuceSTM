package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface VBox {
	boolean validate(VBoxBody body);

	VBoxBody commit(Value value, int newClock);

	void write(VBoxBody body);

	VBoxBody getBody(int clock);

	public VBoxBody getTop();
}
