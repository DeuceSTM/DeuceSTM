package org.deuce.transaction.lsacm.cm;

import org.deuce.transaction.lsacm.ContentionManager;
import org.deuce.transaction.lsacm.Context;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class Aggressive implements ContentionManager {

	public int arbitrate(Context me, Context other, ConflictType type) {
		return KILL_OTHER;
	}
}
