package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transaction.jvstminnogc.InPlaceLock;
import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface VBox extends InPlaceLock {
	boolean validate(Version version, int owner);
	Version get(int version);
}
