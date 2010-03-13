package org.deuce.benchmark.stmbench7.correctness.invariants;

import org.deuce.benchmark.stmbench7.OperationId;
import org.deuce.benchmark.stmbench7.Setup;
import org.deuce.benchmark.stmbench7.annotations.Immutable;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.ThreadLocal;
import org.deuce.benchmark.stmbench7.core.Operation;
import org.deuce.benchmark.stmbench7.core.OperationFailedException;

/**
 * Performs the check of data structure invariants. The operation is always
 * performed in a single thread, without any concurrency, and so it does not
 * have to be thread-safe. However, it is executed by an OperationExecutor, as
 * any other operation.
 */
@Immutable
@ThreadLocal
public class CheckInvariantsOperation implements Operation {

	private final Setup setup;
	private final boolean initial;

	public CheckInvariantsOperation(Setup setup, boolean initial) {
		this.setup = setup;
		this.initial = initial;
	}

	@ReadOnly
	public int performOperation() throws OperationFailedException {
		TraversedObjects traversedObjects = new TraversedObjects();
		ModuleTest.checkInvariants(setup.getModule(), initial, traversedObjects);
		System.err.println("\nChecking indexes...");
		IndexTest.checkInvariants(setup, initial, traversedObjects);
		System.err.println("\nInvariants OK.");
		return 0;
	}

	public OperationId getOperationId() {
		return null;
	}
}
