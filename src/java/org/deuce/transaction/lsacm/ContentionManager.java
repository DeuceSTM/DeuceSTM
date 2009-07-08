package org.deuce.transaction.lsacm;

import org.deuce.transaction.lsacm.Context;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public interface ContentionManager {

	@Exclude
	static public enum ConflictType {
		RR, RW, WR, WW
	}

	final public static int KILL_SELF = 0x00;
	final public static int KILL_OTHER = 0x01;
	final public static int DELAY_RESTART = 0x04;

	public int arbitrate(Context me, Context other, ConflictType type);
}
