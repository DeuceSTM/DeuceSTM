package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface VBoxI extends VBox {

	void commit(int value, int txNumber);

}
