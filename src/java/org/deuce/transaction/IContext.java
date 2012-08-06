package org.deuce.transaction;

import org.deuce.objectweb.asm.Type;

public interface IContext {

	final static public Type CONTEXT_TYPE = Type.getType(IContext.class);
	final static public String CONTEXT_INTERNAL = Type.getInternalName(IContext.class);
	final static public String CONTEXT_DESC = Type.getDescriptor(IContext.class);
	
	/**
	 * Called before the transaction was started
	 * @param atomicBlockId a unique id for atomic block
	 * @param metainf a meta information on the current atomic block.
	 */
	public abstract void init(int atomicBlockId, String metainf);

	/**
	 * Called on commit
	 * @return <code>true</code> on success 
	 */
	public abstract boolean commit();

	/**
	 * Called on rollback, rollback might be called more than once in a row.
	 * But, can't be called after {@link #commit()} without an {@link #init(int, String)} call in between. 
	 */
	public abstract void rollback();
	
	/** Called before entering an irrevocable block*/
	void onIrrevocableAccess();

}