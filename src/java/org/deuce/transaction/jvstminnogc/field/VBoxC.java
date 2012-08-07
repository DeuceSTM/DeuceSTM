package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface VBoxC extends VBox {

	void commit(char value, int txNumber);

}
