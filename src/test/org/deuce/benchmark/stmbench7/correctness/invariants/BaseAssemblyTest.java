package org.deuce.benchmark.stmbench7.correctness.invariants;

import org.deuce.benchmark.stmbench7.Parameters;
import org.deuce.benchmark.stmbench7.annotations.Immutable;
import org.deuce.benchmark.stmbench7.annotations.ThreadLocal;
import org.deuce.benchmark.stmbench7.core.BaseAssembly;
import org.deuce.benchmark.stmbench7.core.ComplexAssembly;
import org.deuce.benchmark.stmbench7.core.CompositePart;
import org.deuce.benchmark.stmbench7.core.Module;

/**
 * Test of invariants of a base assembly.
 */
@Immutable
@ThreadLocal
public class BaseAssemblyTest extends InvariantTest {

	public static void checkInvariants(BaseAssembly assembly, boolean initial,
			ComplexAssembly parentAssembly, Module module, TraversedObjects traversedObjects) {

		traversedObjects.baseAssemblies.add(assembly);
		
		AssemblyTest.checkInvariants(assembly, initial, Parameters.MaxBaseAssemblies, parentAssembly, module);
		
		for(CompositePart component : assembly.getComponents())
			CompositePartTest.checkInvariants(component, initial, assembly, traversedObjects);
	}
}
