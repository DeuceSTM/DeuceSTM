package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface VBoxB extends VBox {

	void commit(byte value, int txNumber);

}
