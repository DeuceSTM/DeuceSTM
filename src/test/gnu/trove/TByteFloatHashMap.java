///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package gnu.trove;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Externalizable;
import java.util.Arrays;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////


/**
 * An open addressed Map implementation for byte keys and float values.
 *
 * Created: Sun Nov  4 08:52:45 2001
 *
 * @author Eric D. Friedman
 */
public class TByteFloatHashMap extends TByteHash implements Externalizable {
	static final long serialVersionUID = 1L;

    /** the values of the map */
    protected transient float[] _values;

    /**
     * Creates a new <code>TByteFloatHashMap</code> instance with the default
     * capacity and load factor.
     */
    public TByteFloatHashMap() {
        super();
    }

    /**
     * Creates a new <code>TByteFloatHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TByteFloatHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates a new <code>TByteFloatHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     */
    public TByteFloatHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Creates a new <code>TByteFloatHashMap</code> instance with the default
     * capacity and load factor.
     * @param strategy used to compute hash codes and to compare keys.
     */
    public TByteFloatHashMap(TByteHashingStrategy strategy) {
        super(strategy);
    }

    /**
     * Creates a new <code>TByteFloatHashMap</code> instance whose capacity
     * is the next highest prime above <tt>initialCapacity + 1</tt>
     * unless that value is already prime.
     *
     * @param initialCapacity an <code>int</code> value
     * @param strategy used to compute hash codes and to compare keys.
     */
    public TByteFloatHashMap(int initialCapacity, TByteHashingStrategy strategy) {
        super(initialCapacity, strategy);
    }

    /**
     * Creates a new <code>TByteFloatHashMap</code> instance with a prime
     * value at or near the specified capacity and load factor.
     *
     * @param initialCapacity used to find a prime capacity for the table.
     * @param loadFactor used to calculate the threshold over which
     * rehashing takes place.
     * @param strategy used to compute hash codes and to compare keys.
     */
    public TByteFloatHashMap(int initialCapacity, float loadFactor, TByteHashingStrategy strategy) {
        super(initialCapacity, loadFactor, strategy);
    }

    /**
     * @return a deep clone of this collection
     */
    public Object clone() {
      TByteFloatHashMap m = (TByteFloatHashMap)super.clone();
      m._values = (float[])this._values.clone();
      return m;
    }

    /**
     * @return a TByteFloatIterator with access to this map's keys and values
     */
    public TByteFloatIterator iterator() {
        return new TByteFloatIterator(this);
    }

    /**
     * initializes the hashtable to a prime capacity which is at least
     * <tt>initialCapacity + 1</tt>.
     *
     * @param initialCapacity an <code>int</code> value
     * @return the actual capacity chosen
     */
    protected int setUp(int initialCapacity) {
        int capacity;

        capacity = super.setUp(initialCapacity);
        _values = new float[capacity];
        return capacity;
    }

    /**
     * Inserts a key/value pair into the map.
     *
     * @param key an <code>byte</code> value
     * @param value an <code>float</code> value
     * @return the previous value associated with <tt>key</tt>,
     * or (byte)0 if none was found.
     */
    public float put(byte key, float value) {
        int index = insertionIndex(key);
        return doPut(key, value, index);
    }
    
    /**
     * Inserts a key/value pair into the map if the specified key is not already
     * associated with a value.
     *
     * @param key an <code>byte</code> value
     * @param value an <code>float</code> value
     * @return the previous value associated with <tt>key</tt>,
     * or (byte)0 if none was found.
     */
    public float putIfAbsent(byte key, float value) {
        int index = insertionIndex(key);
        if (index < 0)
            return _values[-index - 1];
        return doPut(key, value, index);
    }    
    
    private float doPut(byte key, float value, int index) {
        byte previousState;
        float previous = (float)0;
        boolean isNewMapping = true;
        if (index < 0) {
            index = -index -1;
            previous = _values[index];
            isNewMapping = false;
        }
        previousState = _states[index];
        _set[index] = key;
        _states[index] = FULL;
        _values[index] = value;
        if (isNewMapping) {
            postInsertHook(previousState == FREE);
        }

        return previous;
    }

    /**
     * rehashes the map to the new capacity.
     *
     * @param newCapacity an <code>int</code> value
     */
    protected void rehash(int newCapacity) {
        int oldCapacity = _set.length;
        byte oldKeys[] = _set;
        float oldVals[] = _values;
        byte oldStates[] = _states;

        _set = new byte[newCapacity];
        _values = new float[newCapacity];
        _states = new byte[newCapacity];

        for (int i = oldCapacity; i-- > 0;) {
            if(oldStates[i] == FULL) {
                byte o = oldKeys[i];
                int index = insertionIndex(o);
                _set[index] = o;
                _values[index] = oldVals[i];
                _states[index] = FULL;
            }
        }
    }

    /**
     * retrieves the value for <tt>key</tt>
     *
     * @param key an <code>byte</code> value
     * @return the value of <tt>key</tt> or (byte)0 if no such mapping exists.
     */
    public float get(byte key) {
        int index = index(key);
        return index < 0 ? (float)0 : _values[index];
    }

    /**
     * Empties the map.
     *
     */
    public void clear() {
        super.clear();
        byte[] keys = _set;
        float[] vals = _values;
        byte[] states = _states;

        Arrays.fill(_set, 0, _set.length, (byte) 0);
        Arrays.fill(_values, 0, _values.length, (float) 0);
        Arrays.fill(_states, 0, _states.length, FREE);
    }

    /**
     * Deletes a key/value pair from the map.
     *
     * @param key an <code>byte</code> value
     * @return an <code>float</code> value, or (byte)0 if no mapping for key exists
     */
    public float remove(byte key) {
        float prev = (float)0;
        int index = index(key);
        if (index >= 0) {
            prev = _values[index];
            removeAt(index);    // clear key,state; adjust size
        }
        return prev;
    }

    /**
     * Compares this map with another map for equality of their stored
     * entries.
     *
     * @param other an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean equals(Object other) {
        if (! (other instanceof TByteFloatHashMap)) {
            return false;
        }
        TByteFloatHashMap that = (TByteFloatHashMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        return forEachEntry(new EqProcedure(that));
    }

    public int hashCode() {
        HashProcedure p = new HashProcedure();
        forEachEntry(p);
        return p.getHashCode();
    }

    private final class HashProcedure implements TByteFloatProcedure {
        private int h = 0;

        public int getHashCode() {
            return h;
        }

        public final boolean execute(byte key, float value) {
            h += (_hashingStrategy.computeHashCode(key) ^ HashFunctions.hash(value));
            return true;
        }
    }

    private static final class EqProcedure implements TByteFloatProcedure {
        private final TByteFloatHashMap _otherMap;

        EqProcedure(TByteFloatHashMap otherMap) {
            _otherMap = otherMap;
        }

        public final boolean execute(byte key, float value) {
            int index = _otherMap.index(key);
            if (index >= 0 && eq(value, _otherMap.get(key))) {
                return true;
            }
            return false;
        }

        /**
         * Compare two floats for equality.
         */
        private final boolean eq(float v1, float v2) {
            return v1 == v2;
        }

    }

    /**
     * removes the mapping at <tt>index</tt> from the map.
     *
     * @param index an <code>int</code> value
     */
    protected void removeAt(int index) {
        _values[index] = (float)0;
        super.removeAt(index);  // clear key, state; adjust size
    }

    /**
     * Returns the values of the map.
     *
     * @return a <code>Collection</code> value
     */
    public float[] getValues() {
        float[] vals = new float[size()];
        float[] v = _values;
        byte[] states = _states;

        for (int i = v.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            vals[j++] = v[i];
          }
        }
        return vals;
    }

    /**
     * returns the keys of the map.
     *
     * @return a <code>Set</code> value
     */
    public byte[] keys() {
        byte[] keys = new byte[size()];
        byte[] k = _set;
        byte[] states = _states;

        for (int i = k.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            keys[j++] = k[i];
          }
        }
        return keys;
    }

    /**
     * returns the keys of the map.
     *
     * @param a the array into which the elements of the list are to
     *        be stored, if it is big enough; otherwise, a new array of the
     *         same type is allocated for this purpose.
     * @return a <code>Set</code> value
     */
    public byte[] keys(byte[] a) {
        int size = size();
        if (a.length < size) {
            a = (byte[]) java.lang.reflect.Array.newInstance(
                a.getClass().getComponentType(), size);
        }

        byte[] k = (byte[]) _set;
        byte[] states = _states;

        for (int i = k.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            a[j++] = k[i];
          }
        }
        return a;
    }

    /**
     * checks for the presence of <tt>val</tt> in the values of the map.
     *
     * @param val an <code>float</code> value
     * @return a <code>boolean</code> value
     */
    public boolean containsValue(float val) {
        byte[] states = _states;
        float[] vals = _values;

        for (int i = vals.length; i-- > 0;) {
            if (states[i] == FULL && val == vals[i]) {
                return true;
            }
        }
        return false;
    }


    /**
     * checks for the present of <tt>key</tt> in the keys of the map.
     *
     * @param key an <code>byte</code> value
     * @return a <code>boolean</code> value
     */
    public boolean containsKey(byte key) {
        return contains(key);
    }

    /**
     * Executes <tt>procedure</tt> for each key in the map.
     *
     * @param procedure a <code>TByteProcedure</code> value
     * @return false if the loop over the keys terminated because
     * the procedure returned false for some key.
     */
    public boolean forEachKey(TByteProcedure procedure) {
        return forEach(procedure);
    }

    /**
     * Executes <tt>procedure</tt> for each value in the map.
     *
     * @param procedure a <code>TFloatProcedure</code> value
     * @return false if the loop over the values terminated because
     * the procedure returned false for some value.
     */
    public boolean forEachValue(TFloatProcedure procedure) {
        byte[] states = _states;
        float[] values = _values;
        for (int i = values.length; i-- > 0;) {
            if (states[i] == FULL && ! procedure.execute(values[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Executes <tt>procedure</tt> for each key/value entry in the
     * map.
     *
     * @param procedure a <code>TOByteFloatProcedure</code> value
     * @return false if the loop over the entries terminated because
     * the procedure returned false for some entry.
     */
    public boolean forEachEntry(TByteFloatProcedure procedure) {
        byte[] states = _states;
        byte[] keys = _set;
        float[] values = _values;
        for (int i = keys.length; i-- > 0;) {
            if (states[i] == FULL && ! procedure.execute(keys[i],values[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retains only those entries in the map for which the procedure
     * returns a true value.
     *
     * @param procedure determines which entries to keep
     * @return true if the map was modified.
     */
    public boolean retainEntries(TByteFloatProcedure procedure) {
        boolean modified = false;
        byte[] states = _states;
        byte[] keys = _set;
        float[] values = _values;


        // Temporarily disable compaction. This is a fix for bug #1738760
        tempDisableAutoCompaction();
        try {
            for (int i = keys.length; i-- > 0;) {
                if (states[i] == FULL && ! procedure.execute(keys[i],values[i])) {
                    removeAt(i);
                    modified = true;
                }
            }
        }
        finally {
            reenableAutoCompaction(true);
        }
        
        return modified;
    }

    /**
     * Transform the values in this map using <tt>function</tt>.
     *
     * @param function a <code>TFloatFunction</code> value
     */
    public void transformValues(TFloatFunction function) {
        byte[] states = _states;
        float[] values = _values;
        for (int i = values.length; i-- > 0;) {
            if (states[i] == FULL) {
                values[i] = function.execute(values[i]);
            }
        }
    }

    /**
     * Increments the primitive value mapped to key by 1
     *
     * @param key the key of the value to increment
     * @return true if a mapping was found and modified.
     */
    public boolean increment(byte key) {
        return adjustValue(key, (float)1);
    }

    /**
     * Adjusts the primitive value mapped to key.
     *
     * @param key the key of the value to increment
     * @param amount the amount to adjust the value by.
     * @return true if a mapping was found and modified.
     */
    public boolean adjustValue(byte key, float amount) {
        int index = index(key);
        if (index < 0) {
            return false;
        } else {
            _values[index] += amount;
            return true;
        }
    }

    /**
     * Adjusts the primitive value mapped to the key if the key is present in the map.
     * Otherwise, the <tt>initial_value</tt> is put in the map.
     *
     * @param key the key of the value to increment
     * @param adjust_amount the amount to adjust the value by
     * @param put_amount the value put into the map if the key is not initial present
     *
     * @return the value present in the map after the adjustment or put operation
     *
     * @since 2.0b1
     */
    public float adjustOrPutValue(final byte key, final float adjust_amount, final float put_amount ) {
        int index = insertionIndex(key);
        final boolean isNewMapping;
        final float newValue;
        if (index < 0) {
            index = -index -1;
            newValue = ( _values[index] += adjust_amount );
            isNewMapping = false;
        } else {
            newValue = ( _values[index] = put_amount );
            isNewMapping = true;
        }

        byte previousState = _states[index];
        _set[index] = key;
        _states[index] = FULL;

        if ( isNewMapping ) {
            postInsertHook(previousState == FREE);
        }

        return newValue;
    }


    public void writeExternal( ObjectOutput out ) throws IOException {
    	// VERSION
    	out.writeByte( 0 );

    	// NUMBER OF ENTRIES
    	out.writeInt( _size );

    	// ENTRIES
        SerializationProcedure writeProcedure = new SerializationProcedure( out );
        if (! forEachEntry(writeProcedure)) {
            throw writeProcedure.exception;
        }
    }

    public void readExternal( ObjectInput in )
    	throws IOException, ClassNotFoundException {

    	// VERSION
    	in.readByte();

    	// NUMBER OF ENTRIES
    	int size = in.readInt();
    	setUp( size );

    	// ENTRIES
        while (size-- > 0) {
            byte key = in.readByte();
            float val = in.readFloat();
            put(key, val);
        }
    }
} // TByteFloatHashMap
