package org.deuce.transaction.tl2cm.cm;

import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transaction.tl2cm.Context;
import org.deuce.transform.Exclude;

@Exclude
public class LockSkipper extends AbstractContentionManager {

	private ThreadLocal<Integer> skipCountThreadLocal = new ThreadLocal<Integer>();
	
	@Override
	public Action resolve(WriteFieldAccess contentionPoint, Context contending, Context other) {
		int skipCount = getSkipCount();
		if (skipCount == 5) {
			resetSkipCount();
			return Action.RESTART;
		}
		else {
			incrementSkipCount(skipCount);
			return Action.SKIP_LOCK;
		}
	}
	
	@Override
	public String getDescription() {
		return "Lock Skipper";
	}
	
	private int getSkipCount() {
		Integer sc = skipCountThreadLocal.get();
		if (sc == null) {
			sc = new Integer(1);
			skipCountThreadLocal.set(sc);
		}
		return sc;
	}
	
	private void incrementSkipCount(int skipCount) {
		skipCountThreadLocal.set(skipCount+1);
	}
	
	private void resetSkipCount() {
		skipCountThreadLocal.set(0);
	}

}
