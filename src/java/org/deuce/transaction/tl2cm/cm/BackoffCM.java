package org.deuce.transaction.tl2cm.cm;

import org.deuce.transform.Exclude;

/**
 * Abstract class for all contention managers that perform back-off. It provides
 * functionality for that purpose like thread local object holding the number of
 * consecutive back-offs, etc.
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 * @since 1.2
 */
@Exclude
public abstract class BackoffCM extends AbstractContentionManager {
	
	protected final ThreadLocal<BackoffData> data = new ThreadLocal<BackoffData>();
	
	protected BackoffData getBackoffData() {
		BackoffData threadData = data.get();
		if (threadData == null) {
			threadData = new BackoffData(); 
			data.set(threadData);
		}
		return threadData;
	}

	/**
	 * This class stores all the data required to 
	 * achieve back-off for a particular thread
	 */
	protected static class BackoffData {
		
		/**
		 * The local clock value of the thread. This is used
		 * to identify whether the thread is running the same
		 * transaction or started a new one 
		 */
		int originalTimestamp;
		
		/**
		 * Counts the number of times back-off was employed
		 */
		int counter;
		
	}

}
