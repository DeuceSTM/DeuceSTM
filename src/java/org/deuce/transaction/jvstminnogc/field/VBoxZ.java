package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface VBoxZ extends VBox {

	void commit(boolean value, int txNumber);

}
