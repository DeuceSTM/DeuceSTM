package org.deuce.benchmark.stmbench7.core;

import org.deuce.benchmark.stmbench7.annotations.Atomic;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Update;

/**
 * Root of the benchmark main data structure. For a default
 * implementation, see stmbench7.impl.core.ModuleImpl.
 */
@Atomic
public interface Module extends DesignObj {

	@Update
	void setDesignRoot(ComplexAssembly designRoot);

	@ReadOnly
	ComplexAssembly getDesignRoot();

	@ReadOnly
	Manual getManual();
}
