package org.deuce.benchmark.stmbench7.core;

import org.deuce.benchmark.stmbench7.annotations.Atomic;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.backend.ImmutableCollection;

/**
 * Part of the main benchmark data structure. For a default
 * implementation, see stmbench7.impl.core.ComplexAssemblyImpl.
 */
@Atomic
public interface ComplexAssembly extends Assembly {

	@Update
	boolean addSubAssembly(Assembly assembly);

	@Update
	boolean removeSubAssembly(Assembly assembly);

	@ReadOnly
	ImmutableCollection<Assembly> getSubAssemblies();

	@ReadOnly
	short getLevel();
}