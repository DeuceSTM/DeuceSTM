package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VersionL extends Version {
	public long value;

	public VersionL(int version, long value, Version next) {
		super(version, next);
		this.value = value;
	}

}
