package org.deuce.transaction.mvstm.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class VersionD extends Version {
	public double value;

	public VersionD(int version, double value, Version next) {
		super(version, next);
		this.value = value;
	}
	
}
