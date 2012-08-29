package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class VersionF extends Version {
	public float value;

	public VersionF(int version, float value, Version next) {
		super(version, next);
		this.value = value;
	}

}