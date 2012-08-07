package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VersionF extends Version {
	public float value;

	public VersionF(int version, float value, Version next) {
		super(version, next);
		this.value = value;
	}

}