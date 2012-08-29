package org.deuce.transaction.jvstminplace;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface TxQueueListener {
    public void noteOldestTransaction(int newOldest);
}
