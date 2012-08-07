package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface VBoxD extends VBox {

	void commit(double value, int txNumber);

}
