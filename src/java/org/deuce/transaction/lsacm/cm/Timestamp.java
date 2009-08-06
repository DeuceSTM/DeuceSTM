package org.deuce.transaction.lsacm.cm;

import org.deuce.transform.Exclude;
import org.deuce.transaction.lsacm.Context;
import org.deuce.transaction.lsacm.ContentionManager;

/**
 * @author Pascal Felber
 */
@Exclude
public class Timestamp implements ContentionManager {

	public int arbitrate(Context me, Context other, ConflictType type) {
		long myTime = me.getStartTime();
		long otherTime = other.getStartTime();
		if (!other.isActive() || (otherTime > myTime || (otherTime == myTime && other.getId() > me.getId()))) {
			// I am older (or other is not active, so I can proceed)
			return KILL_OTHER;
		}
		return KILL_SELF;
	}
}
