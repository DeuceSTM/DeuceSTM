package org.deuce.benchmark.stmbench7.correctness.invariants;

import java.util.HashSet;

import org.deuce.benchmark.stmbench7.annotations.Immutable;
import org.deuce.benchmark.stmbench7.annotations.ThreadLocal;
import org.deuce.benchmark.stmbench7.core.AtomicPart;
import org.deuce.benchmark.stmbench7.core.BaseAssembly;
import org.deuce.benchmark.stmbench7.core.ComplexAssembly;
import org.deuce.benchmark.stmbench7.core.CompositePart;
import org.deuce.benchmark.stmbench7.core.Document;

/**
 * Stores the sets of objects traversed during an invariants
 * check performed by the CheckInvariantsOperation class.
 */
@Immutable
@ThreadLocal
public class TraversedObjects {

	public final HashSet<ComplexAssembly> complexAssemblies;
	public final HashSet<BaseAssembly> baseAssemblies;
	public final HashSet<CompositePart> components;
	public final HashSet<Document> documents;
	public final HashSet<AtomicPart> atomicParts;
	
	public TraversedObjects() {
		complexAssemblies = new HashSet<ComplexAssembly>();
		baseAssemblies = new HashSet<BaseAssembly>();
		components = new HashSet<CompositePart>();
		documents = new HashSet<Document>();
		atomicParts = new HashSet<AtomicPart>();
	}
}
