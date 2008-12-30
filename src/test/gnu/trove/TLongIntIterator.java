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

//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////


/**
 * Iterator for maps of type long and int.
 *
 * <p>The iterator semantics for Trove's primitive maps is slightly different
 * from those defined in <tt>java.util.Iterator</tt>, but still well within
 * the scope of the pattern, as defined by Gamma, et al.</p>
 *
 * <p>This iterator does <b>not</b> implicitly advance to the next entry when
 * the value at the current position is retrieved.  Rather, you must explicitly
 * ask the iterator to <tt>advance()</tt> and then retrieve either the <tt>key()</tt>,
 * the <tt>value()</tt> or both.  This is done so that you have the option, but not
 * the obligation, to retrieve keys and/or values as your application requires, and
 * without introducing wrapper objects that would carry both.  As the iteration is
 * stateful, access to the key/value parts of the current map entry happens in
 * constant time.</p>
 *
 * <p>In practice, the iterator is akin to a "search finger" that you move from
 * position to position.  Read or write operations affect the current entry only and
 * do not assume responsibility for moving the finger.</p>
 *
 * <p>Here are some sample scenarios for this class of iterator:</p>
 *
 * <pre>
 * // accessing keys/values through an iterator:
 * for (TLongIntIterator it = map.iterator();
 *      it.hasNext();) {
 *   it.advance();
 *   if (satisfiesCondition(it.key()) {
 *     doSomethingWithValue(it.value());
 *   }
 * }
 * </pre>
 *
 * <pre>
 * // modifying values in-place through iteration:
 * for (TLongIntIterator it = map.iterator();
 *      it.hasNext();) {
 *   it.advance();
 *   if (satisfiesCondition(it.key()) {
 *     it.setValue(newValueForKey(it.key()));
 *   }
 * }
 * </pre>
 *
 * <pre>
 * // deleting entries during iteration:
 * for (TLongIntIterator it = map.iterator();
 *      it.hasNext();) {
 *   it.advance();
 *   if (satisfiesCondition(it.key()) {
 *     it.remove();
 *   }
 * }
 * </pre>
 *
 * <pre>
 * // faster iteration by avoiding hasNext():
 * TLongIntIterator iterator = map.iterator();
 * for (int i = map.size(); i-- > 0;) {
 *   iterator.advance();
 *   doSomethingWithKeyAndValue(iterator.key(), iterator.value());
 * }
 * </pre>
 *
 * @author Eric D. Friedman
 * @version $Id: P2PIterator.template,v 1.1 2006/11/10 23:28:00 robeden Exp $
 */

public class TLongIntIterator extends TPrimitiveIterator {
    /** the collection being iterated over */
    private final TLongIntHashMap _map;

    /**
     * Creates an iterator over the specified map
     */
    public TLongIntIterator(TLongIntHashMap map) {
	super(map);
	this._map = map;
    }

    /**
     * Moves the iterator forward to the next entry in the underlying map.
     *
     * @throws java.util.NoSuchElementException if the iterator is already exhausted
     */
    public void advance() {
	moveToNextIndex();
    }

    /**
     * Provides access to the key of the mapping at the iterator's position.
     * Note that you must <tt>advance()</tt> the iterator at least once
     * before invoking this method.
     *
     * @return the key of the entry at the iterator's current position.
     */
    public long key() {
	return _map._set[_index];
    }

    /**
     * Provides access to the value of the mapping at the iterator's position.
     * Note that you must <tt>advance()</tt> the iterator at least once
     * before invoking this method.
     *
     * @return the value of the entry at the iterator's current position.
     */
    public int value() {
	return _map._values[_index];
    }

    /**
     * Replace the value of the mapping at the iterator's position with the
     * specified value. Note that you must <tt>advance()</tt> the iterator at
     * least once before invoking this method.
     *
     * @param val the value to set in the current entry
     * @return the old value of the entry.
     */
    public int setValue(int val) {
	int old = value();
	_map._values[_index] = val;
	return old;
    }
}// TLongIntIterator
