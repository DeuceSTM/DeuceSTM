package org.deuce.transaction.swisstm.cm;


public interface ContentionManager {
	public void start();
	public void onWrite(int writeCount);
	public boolean shouldAbort(int attackerID);
	public void onRollback();
	public void onCommit();
	public int getTS();
	public void signalAbort();
	public boolean wasAbortSignaled();
}
