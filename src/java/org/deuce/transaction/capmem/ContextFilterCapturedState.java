package org.deuce.transaction.capmem;

import org.deuce.transaction.Context;
import org.deuce.transform.Exclude;

/**
 * This class acts like a context Decorator wrapping another Context object.
 * This context adds the field trxFingerprint to the Context object, which 
 * is used to initialize the owner field of the new transactional objects 
 * with this trxFingerprint.
 * Later in all STM barriers we can compare the current owner of a 
 * transactional object with the trxFingerprint of this Context, to detect 
 * if that object is local to this transaction.
 * 
 * @author fmcarvalho <mcarvalho@cc.isel.ip.pt>
 */
@Exclude
public class ContextFilterCapturedState implements Context{
	protected final Context ctx;
	protected Object trxFingerprint;

	public ContextFilterCapturedState(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public void init(int atomicBlockId, String metainf) {
		trxFingerprint = new Object();
		ctx.init(atomicBlockId, metainf);
	}

	@Override
	public boolean commit() {
		return ctx.commit();
	}

	@Override
	public void rollback() {
		ctx.rollback();
	}

	@Override
	public void beforeReadAccess(Object obj, long field) {
		ctx.beforeReadAccess(obj, field);
	}

	@Override
	public Object onReadAccess(Object obj, Object value, long field) {
		return ctx.onReadAccess(obj, value, field);
	}

	@Override
	public boolean onReadAccess(Object obj, boolean value, long field) {
		return ctx.onReadAccess(obj, value, field);
	}

	@Override
	public byte onReadAccess(Object obj, byte value, long field) {
		return ctx.onReadAccess(obj, value, field);
	}

	@Override
	public char onReadAccess(Object obj, char value, long field) {
		return ctx.onReadAccess(obj, value, field);
	}

	@Override
	public short onReadAccess(Object obj, short value, long field) {
		return ctx.onReadAccess(obj, value, field);
	}

	@Override
	public int onReadAccess(Object obj, int value, long field) {
		return ctx.onReadAccess(obj, value, field);
	}

	@Override
	public long onReadAccess(Object obj, long value, long field) {
		return ctx.onReadAccess(obj, value, field);
	}

	@Override
	public float onReadAccess(Object obj, float value, long field) {
		return ctx.onReadAccess(obj, value, field);
	}

	@Override
	public double onReadAccess(Object obj, double value, long field) {
		return ctx.onReadAccess(obj, value, field);
	}

	@Override
	public void onWriteAccess(Object obj, Object value, long field) {
		ctx.onWriteAccess(obj, value, field);
	}

	@Override
	public void onWriteAccess(Object obj, boolean value, long field) {
		ctx.onWriteAccess(obj, value, field);
	}

	@Override
	public void onWriteAccess(Object obj, byte value, long field) {
		ctx.onWriteAccess(obj, value, field);
	}

	@Override
	public void onWriteAccess(Object obj, char value, long field) {
		ctx.onWriteAccess(obj, value, field);
	}

	@Override
	public void onWriteAccess(Object obj, short value, long field) {
		ctx.onWriteAccess(obj, value, field);
	}

	@Override
	public void onWriteAccess(Object obj, int value, long field) {
		ctx.onWriteAccess(obj, value, field);
	}

	@Override
	public void onWriteAccess(Object obj, long value, long field) {
		ctx.onWriteAccess(obj, value, field);
	}

	@Override
	public void onWriteAccess(Object obj, float value, long field) {
		ctx.onWriteAccess(obj, value, field);
	}

	@Override
	public void onWriteAccess(Object obj, double value, long field) {
		ctx.onWriteAccess(obj, value, field);
	}

	@Override
	public void onIrrevocableAccess() {
		ctx.onIrrevocableAccess();
	}

}
