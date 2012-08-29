package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class VersionZ extends Version {
	public boolean value;

	public VersionZ(int version, boolean value, Version next) {
		super(version, next);
		this.value = value;
	}

}
