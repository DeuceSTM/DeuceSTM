package org.deuce.transaction.swisstm.cm;

import org.deuce.transform.Exclude;

@Exclude
public interface ContentionManager {
	public void start();
	public void onWrite(int writeCount);
	public boolean shouldAbort(int attackerID);
	public void onRollback();
	public void onCommit();
	public int getTS();

	/*
	 * These methods exist because rollback cannot
	 * be called directly from the contention
	 * manager. Calling rollback would cause problems
	 * because it is being called from another thread.
	 * Instead the transaction should check themselves
	 * if they should abort.
	 */
	public void signalAbort();
	public boolean wasAbortSignaled();
}
