package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface VBoxO extends VBox {

	void commit(Object value, int txNumber);

}
