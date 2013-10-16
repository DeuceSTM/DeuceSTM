package org.deuce.transaction.swisstm;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.swisstm.cm.ContentionManager;
import org.deuce.transaction.swisstm.cm.TransactionWithCM;
import org.deuce.transaction.swisstm.cm.TwoPhaseContentionManager;
import org.deuce.transaction.swisstm.field.Field;
import org.deuce.transaction.swisstm.field.Field.Type;
import org.deuce.transaction.swisstm.field.LockPair;
import org.deuce.transaction.swisstm.field.ReadLog;
import org.deuce.transaction.swisstm.field.WriteLog;
import org.deuce.transform.Exclude;

/**
 * SwissTM implementation

 * @author Daniel Pinto
 */
@Exclude
public final class Context implements org.deuce.transaction.Context, TransactionWithCM {

	// Exceptions
	private static final TransactionException WRITE_FAILURE_EXCEPTION =
			new TransactionException("Fail on write.");

	private static final TransactionException READ_FAILURE_EXCEPTION =
			new TransactionException("Fail on read.");

	// Global lock used to allow only one irrevocable transaction solely.
	private final static ReentrantReadWriteLock irrevocableAccessLock =
			new ReentrantReadWriteLock();
	private boolean irrevocableState = false;

	// Global variables
	private static final AtomicInteger transactionID = new AtomicInteger(0);
	private static final AtomicInteger commitTS = new AtomicInteger(0);
	private static final LockTable lockTable = new LockTable();

	// Transaction local variables
	private final int id;
	private int validTS;
	private final ReadLog readLog;
	private final WriteLog writeLog;
	private final ContentionManager contentionManager;

	public Context() {
		this.readLog = new ReadLog();
		this.writeLog = new WriteLog();
		this.id = transactionID.incrementAndGet();
		this.contentionManager = new TwoPhaseContentionManager(this.id, this);
	}

	@Override
	public void init(int atomicBlockId, String metainf) {
		// Lock according to the transaction irrevocable state
		if (this.irrevocableState) {
			irrevocableAccessLock.writeLock().lock();
		} else {
			irrevocableAccessLock.readLock().lock();
		}

		// Clear logs
		this.readLog.clear();
		this.writeLog.clear();

		this.validTS = commitTS.get();

		this.contentionManager.start();
	}

	private Object onReadAccess(Address address) {
		if (this.contentionManager.wasAbortSignaled()) {
			throw READ_FAILURE_EXCEPTION;
		}

		LockPair locks = lockTable.getLocks(address);
		if (locks.getWLockTransactionID() == this.id) { // Locked by me?
			return this.writeLog.get(address);
		}

		// Get a consistent reading of the value
		Object value;
		int version = locks.getRLockVersion();
		int version2;
		while (true) {
			if (version == LockPair.READ_LOCKED) {
				version = locks.getRLockVersion();
				continue;
			}
			value = Field.getValue(address.object, address.field, address.type);
			version2 = locks.getRLockVersion();
			if (version == version2) {
				break;
			}
			version = version2;
		}

		this.readLog.add(address, locks, version);
		if (version > this.validTS && !extend()) {
			throw READ_FAILURE_EXCEPTION;
		}

		return value;
	}

	private void onWriteAccess(Address address, Object value) {
		if (this.contentionManager.wasAbortSignaled()) {
			throw WRITE_FAILURE_EXCEPTION;
		}

		LockPair locks = lockTable.getLocks(address);
		if (locks.getWLockTransactionID() == this.id) { // Locked by me?
			this.writeLog.update(address, value);
			return;
		}

		while (true) {
			int attackerID = locks.getWLockTransactionID();
			if (attackerID != LockPair.WRITE_UNLOCKED) {
				if (this.contentionManager.shouldAbort(attackerID)) {
					throw WRITE_FAILURE_EXCEPTION;
				} else {
					continue;
				}
			}

			if (locks.casWLock(LockPair.WRITE_UNLOCKED, this.id)) {
				break;
			}
		}

		int version = locks.getRLockVersion();
		this.writeLog.add(address, locks, value, version);

		if (version > this.validTS && !extend()) {
			throw WRITE_FAILURE_EXCEPTION;
		}

		this.contentionManager.onWrite(this.writeLog.size());
	}

	@Override
	public boolean commit() {
		try {
			if (isReadOnly()) {
				return true;
			}

			if (this.contentionManager.wasAbortSignaled()) {
				return false;
			}

			// Lock r-locks of written addresses
			this.writeLog.lockReadLocks();
			Collection<Address> readLockedAddresses = this.writeLog.getAddresses();

			int ts = commitTS.incrementAndGet();

			// Ignore the addresses read-locked by commit (ie, the written addresses)
			// during validation
			if (ts > this.validTS + 1 && !this.readLog.validate(readLockedAddresses)) {
				// Restore r-locks to previous values
				this.writeLog.restoreReadLocks();
				return false;
			}

			// Write values and unlock locks
			this.writeLog.commitValues(ts);

			this.contentionManager.onCommit();
			return true;
		} finally {
			if (this.irrevocableState){
				this.irrevocableState = false;
				irrevocableAccessLock.writeLock().unlock();
			} else {
				irrevocableAccessLock.readLock().unlock();
			}
		}
	}

	@Override
	public void rollback() {
		try {
			this.writeLog.unlockWriteLocks();
			this.contentionManager.onRollback();
		} finally {
			irrevocableAccessLock.readLock().unlock();
		}
	}

	private boolean extend() {
		int ts = commitTS.get();
		if (this.readLog.validate()) {
			this.validTS = ts;
			return true;
		}
		return false;
	}

	private boolean isReadOnly() {
		return this.writeLog.size() == 0;
	}

	@Override
	public ContentionManager getContentionManager() {
		return this.contentionManager;
	}

	@Override
	public Object onReadAccess(Object obj, Object value, long field) {
		return onReadAccess(new Address(obj, field, Type.OBJECT));
	}

	@Override
	public boolean onReadAccess(Object obj, boolean value, long field) {
		return (Boolean) onReadAccess(new Address(obj, field, Type.BOOLEAN));
	}

	@Override
	public byte onReadAccess(Object obj, byte value, long field) {
		return ((Number) onReadAccess(new Address(obj, field, Type.BYTE))).byteValue();
	}

	@Override
	public char onReadAccess(Object obj, char value, long field) {
		return (Character) onReadAccess(new Address(obj, field, Type.CHAR));
	}

	@Override
	public short onReadAccess(Object obj, short value, long field) {
		return ((Number) onReadAccess(new Address(obj, field, Type.SHORT))).shortValue();
	}

	@Override
	public int onReadAccess(Object obj, int value, long field) {
		return ((Number) onReadAccess(new Address(obj, field, Type.INT))).intValue();
	}

	@Override
	public long onReadAccess(Object obj, long value, long field) {
		return ((Number) onReadAccess(new Address(obj, field, Type.LONG))).longValue();
	}

	@Override
	public float onReadAccess(Object obj, float value, long field) {
		return ((Number) onReadAccess(new Address(obj, field, Type.FLOAT))).floatValue();
	}

	@Override
	public double onReadAccess(Object obj, double value, long field) {
		return ((Number) onReadAccess(new Address(obj, field, Type.DOUBLE))).doubleValue();
	}

	@Override
	public void onWriteAccess(Object obj, Object value, long field) {
		onWriteAccess(new Address(obj, field, Type.OBJECT), value);
	}

	@Override
	public void onWriteAccess(Object obj, boolean value, long field) {
		onWriteAccess(new Address(obj, field, Type.BOOLEAN), value);
	}

	@Override
	public void onWriteAccess(Object obj, byte value, long field) {
		onWriteAccess(new Address(obj, field, Type.BYTE), value);
	}

	@Override
	public void onWriteAccess(Object obj, char value, long field) {
		onWriteAccess(new Address(obj, field, Type.CHAR), value);
	}

	@Override
	public void onWriteAccess(Object obj, short value, long field) {
		onWriteAccess(new Address(obj, field, Type.SHORT), value);
	}

	@Override
	public void onWriteAccess(Object obj, int value, long field) {
		onWriteAccess(new Address(obj, field, Type.INT), value);
	}

	@Override
	public void onWriteAccess(Object obj, long value, long field) {
		onWriteAccess(new Address(obj, field, Type.LONG), value);
	}

	@Override
	public void onWriteAccess(Object obj, float value, long field) {
		onWriteAccess(new Address(obj, field, Type.FLOAT), value);
	}

	@Override
	public void onWriteAccess(Object obj, double value, long field) {
		onWriteAccess(new Address(obj, field, Type.DOUBLE), value);
	}

	@Override
	public void beforeReadAccess(Object obj, long field) {
		// Useless for SwissTM
	}

	@Override
	public void onIrrevocableAccess() {
		if (this.irrevocableState) { // Already in irrevocable state so no need to restart transaction
			return;
		}

		this.irrevocableState = true;
		throw TransactionException.STATIC_TRANSACTION;
	}
}
