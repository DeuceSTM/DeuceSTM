package org.deuce.benchmark.stmbench7.locking;

import org.deuce.benchmark.stmbench7.core.ComplexAssembly;
import org.deuce.benchmark.stmbench7.core.Module;
import org.deuce.benchmark.stmbench7.impl.core.DesignObjFactoryImpl;

/**
 * Implementation of the DesignObjFactory used in the medium-grained
 * locking synchronization method.
 */
public class MGLockingDesignObjFactory extends DesignObjFactoryImpl {

	public MGLockingDesignObjFactory() {
		super();
	}

	@Override
	public ComplexAssembly createComplexAssembly(int id, String type, int buildDate, 
			Module module, ComplexAssembly superAssembly) {
		return new MGLockingComplexAssemblyImpl(id, type, buildDate, module, superAssembly);
	}
}
