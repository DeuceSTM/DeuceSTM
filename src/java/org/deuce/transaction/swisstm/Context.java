package org.deuce.transaction.swisstm;

import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.transaction.lsa.field.Field.Type;


public class Context implements org.deuce.transaction.Context {

	private static final AtomicInteger threadID = new AtomicInteger(0);
	private static final AtomicInteger commitTS = new AtomicInteger(0);	
	
	private int id;
	private int validTS;	

	private Object readValue;
	
	@Override
	public void init(int atomicBlockId, String metainf) {
		this.id = threadID.incrementAndGet();
		this.validTS = commitTS.get();
	}

	@Override
	public boolean commit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeReadAccess(Object obj, long field) {
		// TODO Auto-generated method stub
		
	}

	private boolean onReadAccess(Object obj, long field, Type type) {
		return false;
	}
	
	private void onWriteAccess(Object obj, long field, Object value, Type type) {
		
	}
	
	@Override
	public Object onReadAccess(Object obj, Object value, long field) {
		return (onReadAccess(obj, field, Type.OBJECT) ? this.readValue : value);
	}

	@Override
	public boolean onReadAccess(Object obj, boolean value, long field) {
		return (onReadAccess(obj, field, Type.BOOLEAN) ? (Boolean) this.readValue : value);
	}

	@Override
	public byte onReadAccess(Object obj, byte value, long field) {
		return (onReadAccess(obj, field, Type.BYTE) ? ((Number) this.readValue).byteValue() : value);
	}

	@Override
	public char onReadAccess(Object obj, char value, long field) {
		return (onReadAccess(obj, field, Type.CHAR) ? (Character) this.readValue : value);
	}

	@Override
	public short onReadAccess(Object obj, short value, long field) {
		return (onReadAccess(obj, field, Type.SHORT) ? ((Number) this.readValue).shortValue() : value);
	}

	@Override
	public int onReadAccess(Object obj, int value, long field) {
		return (onReadAccess(obj, field, Type.INT) ? ((Number) this.readValue).intValue() : value);
	}

	@Override
	public long onReadAccess(Object obj, long value, long field) {
		return (onReadAccess(obj, field, Type.LONG) ? ((Number) this.readValue).longValue() : value);
	}

	@Override
	public float onReadAccess(Object obj, float value, long field) {
		return (onReadAccess(obj, field, Type.FLOAT) ? ((Number) this.readValue).floatValue() : value);
	}

	@Override
	public double onReadAccess(Object obj, double value, long field) {
		return (onReadAccess(obj, field, Type.DOUBLE) ? ((Number) this.readValue).doubleValue() : value);
	}

	@Override
	public void onWriteAccess(Object obj, Object value, long field) {
		onWriteAccess(obj, field, value, Type.OBJECT);
	}

	@Override
	public void onWriteAccess(Object obj, boolean value, long field) {
		onWriteAccess(obj, field, value, Type.BOOLEAN);
	}

	@Override
	public void onWriteAccess(Object obj, byte value, long field) {
		onWriteAccess(obj, field, value, Type.BYTE);
	}

	@Override
	public void onWriteAccess(Object obj, char value, long field) {
		onWriteAccess(obj, field, value, Type.CHAR);
	}

	@Override
	public void onWriteAccess(Object obj, short value, long field) {
		onWriteAccess(obj, field, value, Type.SHORT);
	}

	@Override
	public void onWriteAccess(Object obj, int value, long field) {
		onWriteAccess(obj, field, value, Type.INT);
	}

	@Override
	public void onWriteAccess(Object obj, long value, long field) {
		onWriteAccess(obj, field, value, Type.LONG);
	}

	@Override
	public void onWriteAccess(Object obj, float value, long field) {
		onWriteAccess(obj, field, value, Type.FLOAT);
	}

	@Override
	public void onWriteAccess(Object obj, double value, long field) {
		onWriteAccess(obj, field, value, Type.DOUBLE);
	}

	@Override
	public void onIrrevocableAccess() {
		// TODO Auto-generated method stub
		
	}	
}
