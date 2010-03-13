package org.deuce.benchmark.stmbench7.core;

import org.deuce.benchmark.stmbench7.annotations.Atomic;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Update;

/**
 * Part of the main benchmark data structure. For a default
 * implementation, see stmbench7.impl.core.AssemblyImpl.
 */
@Atomic
public interface Assembly extends DesignObj {

	@ReadOnly
	ComplexAssembly getSuperAssembly();

	@ReadOnly
	Module getModule();

	@Update
	void clearPointers();
}