package org.deuce.benchmark.stmbench7.correctness.invariants;

import org.deuce.benchmark.stmbench7.Parameters;
import org.deuce.benchmark.stmbench7.annotations.Immutable;
import org.deuce.benchmark.stmbench7.annotations.ThreadLocal;
import org.deuce.benchmark.stmbench7.core.Assembly;
import org.deuce.benchmark.stmbench7.core.ComplexAssembly;
import org.deuce.benchmark.stmbench7.core.Module;

/**
 * Test of invariants of an assembly.
 */
@Immutable
@ThreadLocal
public class AssemblyTest extends InvariantTest {

	public static void checkInvariants(Assembly assembly, boolean initial, int maxId,
			ComplexAssembly parentAssembly, Module module) {

		DesignObjTest.checkInvariants(assembly, initial, maxId, Parameters.MinAssmDate, Parameters.MaxAssmDate);
		
		int id = assembly.getId();
		
		if(assembly.getSuperAssembly() != parentAssembly)
			reportError(assembly, id, "invalid reference to the parent ComplexAssembly");
		
		if(assembly.getModule() != module)
			reportError(assembly, id, "invalid reference to the parent Module");
	}

}
