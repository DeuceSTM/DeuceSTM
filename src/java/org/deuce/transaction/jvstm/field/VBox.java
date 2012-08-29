package org.deuce.transaction.jvstm.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface VBox {
	boolean validate(VBoxBody body);

	VBoxBody commit(Value value, int newClock);

	VBoxBody getBody(int clock);

}
