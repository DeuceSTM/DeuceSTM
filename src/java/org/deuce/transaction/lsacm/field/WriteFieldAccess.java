package org.deuce.transaction.lsacm.field;

import org.deuce.transaction.lsacm.field.Field;
import org.deuce.transaction.lsacm.field.Field.Type;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class WriteFieldAccess extends ReadFieldAccess {

	private Type type;
	private Object value;
	private WriteFieldAccess next;
	private boolean write;

	public WriteFieldAccess(Object reference, long field, Type type, Object value, int hash, long lock) {
		super(reference, field, hash, lock);
		this.type = type;
		this.value = value;
		this.write = true;
		this.next = null;
	}

	public WriteFieldAccess(Object reference, long field, int hash, long lock) {
		super(reference, field, hash, lock);
		this.type = null;
		this.value = null;
		this.write = false;
		this.next = null;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setNext(WriteFieldAccess next) {
		this.next = next;
	}

	public WriteFieldAccess getNext() {
		return next;
	}

	public void update(Type type, Object value) {
		if (!write) {
			// Read entry upgrades to write mode
			this.type = type;
			write = true;
		}
		this.value = value;
	}

	public void writeField() {
		if (write)
			Field.putValue(getReference(), getField(), value, type);
	}
}