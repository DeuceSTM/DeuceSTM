package org.deuce.transaction.mvstm.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class VersionC extends Version {
	public char value;

	public VersionC(int version, char value, Version next) {
		super(version, next);
		this.value = value;
	}
	
}
