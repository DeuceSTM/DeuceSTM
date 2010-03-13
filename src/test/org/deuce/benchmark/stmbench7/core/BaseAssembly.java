package org.deuce.benchmark.stmbench7.core;

import org.deuce.benchmark.stmbench7.annotations.Atomic;
import org.deuce.benchmark.stmbench7.annotations.ReadOnly;
import org.deuce.benchmark.stmbench7.annotations.Update;
import org.deuce.benchmark.stmbench7.backend.ImmutableCollection;

/**
 * Part of the main benchmark data structure. For a default
 * implementation, see stmbench7.impl.core.BaseAssemblyImpl.
 */
@Atomic
public interface BaseAssembly extends Assembly {

	@Update
	void addComponent(CompositePart component);

	@Update
	boolean removeComponent(CompositePart component);

	@ReadOnly
	ImmutableCollection<CompositePart> getComponents();
}