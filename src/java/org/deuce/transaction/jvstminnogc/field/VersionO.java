package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VersionO extends Version {
	public Object value;

	public VersionO(int version, Object value, Version next) {
		super(version, next);
		this.value = value;
	}
	
}
