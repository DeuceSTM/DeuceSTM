package org.deuce.transaction.mvstm.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class VersionO extends Version {
	public Object value;

	public VersionO(int version, Object value, Version next) {
		super(version, next);
		this.value = value;
	}
	
}
