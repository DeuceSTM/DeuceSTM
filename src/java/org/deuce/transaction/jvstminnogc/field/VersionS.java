package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class VersionS extends Version {
	public short value;

	public VersionS(int version, short value, Version next) {
		super(version, next);
		this.value = value;
	}

}
