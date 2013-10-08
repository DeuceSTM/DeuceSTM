package org.deuce.transaction.swisstm;

import org.deuce.transform.Exclude;

@Exclude
public final class Address {
	private final Object object;
	private final long field;

	public Address(Object object, long field) {
		this.object = object;
		this.field = field;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Address address = (Address)obj;
        return this.object == address.object && this.field == address.field;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this.object) + (int) this.field;
	}
	
}
