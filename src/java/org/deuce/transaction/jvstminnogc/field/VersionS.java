package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VersionS extends Version {
	public short value;

	public VersionS(int version, short value, Version next) {
		super(version, next);
		this.value = value;
	}

}
