package org.deuce.transaction.jvstminnogc;

public interface InPlaceLock {
	
	boolean lock(int owner);
	void unLock();
	
}
