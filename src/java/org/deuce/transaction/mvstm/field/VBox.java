package org.deuce.transaction.mvstm.field;

import org.deuce.transaction.mvstm.InPlaceLock;
import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public interface VBox extends InPlaceLock {
	boolean validate(Version version, int owner);
	Version get(int version);
}
