package org.deuce.benchmark.bank;

/**
 * @author Pascal Felber
 * @since 0.1
 */
public class OverdraftException extends Exception {

	public OverdraftException() {
		super();
	}

	public OverdraftException(String reason) {
		super(reason);
	}
}
