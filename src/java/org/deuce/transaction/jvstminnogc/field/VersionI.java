package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VersionI extends Version {
	public int value;

	public VersionI(int version, int value, Version next) {
		super(version, next);
		this.value = value;
	}

}
