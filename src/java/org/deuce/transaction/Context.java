/*
 * @(#)Context.java   05/01/2008
 *
 * Copyright 2008 GigaSpaces Technologies Inc.
 */

package org.deuce.transaction;

import org.deuce.objectweb.asm.Type;
import org.deuce.transform.Exclude;

/**
 * All the STM implementations should implement this interface.
 * Using the -Dorg.deuce.transaction.contextClass property one can
 * switch between the different implementations. 
 *
 * @author	Guy Korland
 * @since	1.0
 */
@Exclude
public interface Context
{
	final static public Type CONTEXT_TYPE = Type.getType( Context.class);
	final static public String CONTEXT_INTERNAL = Type.getInternalName(Context.class);
	final static public String CONTEXT_DESC = Type.getDescriptor(Context.class);

	/**
	 * Called before the transaction was started
	 */
	void init();

	/**
	 * Called on commit
	 * @return <code>true</code> on success 
	 */
	boolean commit();

	/**
	 * Called on rollback, rollback might be called more than once in a row.
	 * But, can't be called after {@link #commit()} without an {@link #init()} call in between. 
	 */
	void rollback();

	/* Methods called on Read/Write event */
	void beforeReadAccess( Object obj, long field);
	<T> T addReadAccess( Object obj, T value, long field);
	boolean addReadAccess( Object obj, boolean value, long field);
	byte addReadAccess( Object obj, byte value, long field);
	char addReadAccess( Object obj, char value, long field);
	short addReadAccess( Object obj, short value, long field);
	int addReadAccess( Object obj, int value, long field);
	long addReadAccess( Object obj, long value, long field);
	float addReadAccess( Object obj, float value, long field);
	double addReadAccess( Object obj, double value, long field);

	void addWriteAccess( Object obj, Object value, long field);
	void addWriteAccess( Object obj, boolean value, long field);
	void addWriteAccess( Object obj, byte value, long field);
	void addWriteAccess( Object obj, char value, long field);
	void addWriteAccess( Object obj, short value, long field);
	void addWriteAccess( Object obj, int value, long field);
	void addWriteAccess( Object obj, long value, long field);
	void addWriteAccess( Object obj, float value, long field);
	void addWriteAccess( Object obj, double value, long field);
}
