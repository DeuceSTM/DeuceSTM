package org.deuce.transaction.lsacm;

import org.deuce.transform.ExcludeInternal;

/**
 * @author Pascal Felber
 */
@ExcludeInternal
public interface ContentionManager {

	@ExcludeInternal
	static public enum ConflictType {
		RR, RW, WR, WW
	}

	final public static int KILL_SELF = 0x00;
	final public static int KILL_OTHER = 0x01;
	final public static int DELAY_RESTART = 0x04;

	public int arbitrate(Context me, Context other, ConflictType type);
}
