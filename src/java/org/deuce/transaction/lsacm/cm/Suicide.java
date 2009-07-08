package org.deuce.transaction.lsacm.cm;

import org.deuce.transform.Exclude;
import org.deuce.transaction.lsacm.Context;
import org.deuce.transaction.lsacm.ContentionManager;

/**
 * @author Pascal Felber
 */
@Exclude
public class Suicide implements ContentionManager {

	public int arbitrate(Context me, Context other, ConflictType type) {
		return KILL_SELF;
	}
}
