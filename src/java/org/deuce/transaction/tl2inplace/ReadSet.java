package org.deuce.transaction.tl2inplace;

import org.deuce.transaction.tl2inplace.field.ReadFieldAccess;
import org.deuce.transform.ExcludeInternal;

/**
 * Represents the transaction read set. And acts as a recycle pool of the
 * {@link ReadFieldAccess}.
 * 
 * @author Guy Korland, Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class ReadSet {

	private static final int DEFAULT_CAPACITY = 1024;
	private ReadFieldAccess[] readSet = new ReadFieldAccess[DEFAULT_CAPACITY];
	private int nextAvaliable = 0;

	public ReadSet() {
		fillArray(0);
	}

	public void clear() {
		nextAvaliable = 0;
	}

	private void fillArray(int offset) {
		for (int i = offset; i < readSet.length; ++i) {
			readSet[i] = new ReadFieldAccess();
		}
	}

	public ReadFieldAccess getNext() {
		if (nextAvaliable >= readSet.length) {
			int orignLength = readSet.length;
			ReadFieldAccess[] tmpReadSet = new ReadFieldAccess[2 * orignLength];
			System.arraycopy(readSet, 0, tmpReadSet, 0, orignLength);
			readSet = tmpReadSet;
			fillArray(orignLength);
		}
		return readSet[nextAvaliable++];
	}

	public void checkClock(int clock, Context lockChecker) {
		for (int i = 0; i < nextAvaliable; i++) {
			((InPlaceLock) readSet[i].field).checkLock(clock, lockChecker);
			readSet[i].clear();
		}
	}

	public interface ReadSetListener {
		void execute(ReadFieldAccess read);
	}

	public int size() {
		return nextAvaliable;
	}
}
