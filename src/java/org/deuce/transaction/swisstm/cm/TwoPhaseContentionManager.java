package org.deuce.transaction.swisstm.cm;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.transform.Exclude;

@Exclude
public class TwoPhaseContentionManager implements ContentionManager {

	/*
	 * SwissTM uses cycles instead of ns. Java has no way of
	 * getting the CPU cycles. SwissTM uses 8000 cycles as
	 * a multiplicator. At 2GHz, 1 ns is 2 cycles, so I
	 * halved their value to convert to ns.
	 */
	private static final int WAIT_NS_MULTIPLICATOR = 4000;
	private static final int GREEDY_PHASE_THRESHOLD = 10;

	// Global variables
	private static final AtomicInteger greedyTS = new AtomicInteger(0);
	private static final ConcurrentMap<Integer, TransactionWithCM> threadMap =
			new ConcurrentHashMap<Integer, TransactionWithCM>();
	private static final Random RANDOM = new Random();

	// Transaction local variables
	private int cmTS;
	private AtomicBoolean abortSignaled = new AtomicBoolean(false);
	private int successiveAbortCount;
	private boolean wasRestarted = false;

	public TwoPhaseContentionManager(int threadID, TransactionWithCM thread) {
		threadMap.put(threadID, thread);
	}

	@Override
	public void start() {
		if (!this.wasRestarted) {
			this.cmTS = Integer.MAX_VALUE;
			this.abortSignaled.set(false);
			this.successiveAbortCount = 0;
		}
	}

	@Override
	public void onWrite(int writeCount) {
		if (inInitialPhase() && writeCount >= GREEDY_PHASE_THRESHOLD) {
			// Change to greedy phase
			this.cmTS = greedyTS.incrementAndGet();
		}
	}

	@Override
	public boolean shouldAbort(int attackerID) {
		if (inInitialPhase()) {
			return true;
		}

		if (this.abortSignaled.get()) {
			return true;
		}

		TransactionWithCM lockOwner = threadMap.get(attackerID);
		if (lockOwner.getContentionManager().getTS() < this.cmTS) {
			return true;
		} else {
			lockOwner.getContentionManager().signalAbort();
			return false;
		}
	}

	@Override
	public void onRollback() {
		this.wasRestarted = true;
		this.successiveAbortCount++;
		int waitTime = RANDOM.nextInt(this.successiveAbortCount * WAIT_NS_MULTIPLICATOR);
		try {
			Thread.sleep(0, waitTime);
		} catch (InterruptedException e) {
			// Do nothing
		}
	}

	@Override
	public void onCommit() {
		this.wasRestarted = false;
	}

	@Override
	public int getTS() {
		return this.cmTS;
	}

	@Override
	public void signalAbort() {
		this.abortSignaled.set(true);
	}

	@Override
	public boolean wasAbortSignaled() {
		return this.abortSignaled.get();
	}

	private boolean inInitialPhase() {
		return this.cmTS == Integer.MAX_VALUE;
	}
}
