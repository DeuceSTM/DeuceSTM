package org.deuce.transaction.swisstm.cm;

import org.deuce.transform.Exclude;

@Exclude
public interface TransactionWithCM {
	public ContentionManager getContentionManager();
}
