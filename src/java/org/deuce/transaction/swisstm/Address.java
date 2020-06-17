package org.deuce.transaction.swisstm;

import org.deuce.transaction.swisstm.field.Field.Type;
import org.deuce.transform.Exclude;

@Exclude
public final class Address {
	public final Object object;
	public final long field;
	public final Type type;
	private final int hash;

	public Address(Object object, long field, Type type) {
		this.object = object;
		this.field = field;
		this.type = type;
		this.hash = System.identityHashCode(this.object) + (int) this.field;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		Address address = (Address) obj;

		// type depends on the address and field, no need to check it
		return this.object == address.object && this.field == address.field;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}
}
