package org.deuce.transaction.jvstm;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface TxQueueListener {
    public void noteOldestTransaction(int newOldest);
}
