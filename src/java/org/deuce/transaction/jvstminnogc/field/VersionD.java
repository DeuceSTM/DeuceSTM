package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VersionD extends Version {
	public double value;

	public VersionD(int version, double value, Version next) {
		super(version, next);
		this.value = value;
	}
	
}
