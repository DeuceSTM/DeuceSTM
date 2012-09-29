package org.deuce.transaction.mvstm.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class VersionL extends Version {
	public long value;

	public VersionL(int version, long value, Version next) {
		super(version, next);
		this.value = value;
	}

}
