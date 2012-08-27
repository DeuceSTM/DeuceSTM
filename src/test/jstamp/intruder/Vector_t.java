package jstamp.intruder;

public class Vector_t {
	int size;
	int capacity;
	Object[] elements;

	/* =============================================================================
	 * Vector_alloc
	 * -- Returns null if failed
	 * =============================================================================
	 */
	public Vector_t(int initCapacity) {
		int capacity = Math.max(initCapacity, 1);
		this.size = 0;
		this.capacity = capacity;
		this.elements = new Object[capacity];
	}

	/* =============================================================================
	 * Vector_free
	 * =============================================================================
	 */
	public void vector_free() {
		elements = null;
	}

	/* =============================================================================
	 * Vector_at
	 * -- Returns null if failed
	 * =============================================================================
	 */
	public Object vector_at(int i) {
		return (elements[i]);
	}

	/* =============================================================================
	 * Vector_pushBack
	 * -- Returns false if fail, else true
	 * =============================================================================
	 */
	public boolean vector_pushBack(Object dataPtr) {
		if (size == capacity) {
			int newCapacity = capacity * 2;
			Object[] newElements = new Object[newCapacity];

			//void** newElements = (void**)malloc(newCapacity * sizeof(void*));

			capacity = newCapacity;
			for (int i = 0; i < size; i++) {
				newElements[i] = elements[i];
			}
			elements = null;
			elements = newElements;
		}

		elements[size++] = dataPtr;

		return true;
	}

	/* =============================================================================
	 * Vector_popBack
	 * -- Returns null if fail, else returns last element
	 * =============================================================================
	 */
	public Object vector_popBack() {
		if (size < 1) {
			return null;
		}

		return (elements[--(size)]);
	}

	/* =============================================================================
	 * Vector_getSize
	 * =============================================================================
	 */
	public int vector_getSize() {
		return (size);
	}

	/* =============================================================================
	 * Vector_clear
	 * =============================================================================
	 */
	public void vector_clear() {
		size = 0;
	}

	/* =============================================================================
	 * Vector_sort
	 * =============================================================================
	 *
	public void
	  vector_sort ()
	  {
	    //qsort.sort(elements, 0, (elements.length - 1));
	    qsort.sort(elements);
	    //qsort(elements, size, 4, compare);
	  }

	* =============================================================================
	 * Vector_copy
	 * =============================================================================
	 */
	public static boolean vector_copy(Vector_t dstVectorPtr,
			Vector_t srcVectorPtr) {
		int dstCapacity = dstVectorPtr.capacity;
		int srcSize = srcVectorPtr.size;
		if (dstCapacity < srcSize) {
			int srcCapacity = srcVectorPtr.capacity;
			Object[] elements = new Object[srcCapacity];

			dstVectorPtr.elements = null;
			dstVectorPtr.elements = elements;
			dstVectorPtr.capacity = srcCapacity;
		}

		for (int i = 0; i < srcSize; i++) {
			dstVectorPtr.elements[i] = srcVectorPtr.elements[i];
		}

		dstVectorPtr.size = srcSize;

		return true;
	}
}
