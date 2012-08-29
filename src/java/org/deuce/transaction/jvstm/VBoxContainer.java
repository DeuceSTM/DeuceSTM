/*
 * JVSTM: a Java library for Software Transactional Memory
 * Copyright (C) 2005 INESC-ID Software Engineering Group
 * http://www.esw.inesc-id.pt
 *
 * Author's contact:
 * INESC-ID Software Engineering Group
 * Rua Alves Redol 9
 * 1000 - 029 Lisboa
 * Portugal
 */
package org.deuce.transaction.jvstm;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import org.deuce.transaction.jvstm.field.VBox;
import org.deuce.transaction.jvstm.field.VBoxB;
import org.deuce.transaction.jvstm.field.VBoxC;
import org.deuce.transaction.jvstm.field.VBoxD;
import org.deuce.transaction.jvstm.field.VBoxF;
import org.deuce.transaction.jvstm.field.VBoxI;
import org.deuce.transaction.jvstm.field.VBoxL;
import org.deuce.transaction.jvstm.field.VBoxO;
import org.deuce.transaction.jvstm.field.VBoxS;
import org.deuce.transaction.jvstm.field.VBoxZ;
import org.deuce.transform.ExcludeInternal;


/*
 * Some parts of this class are taken from the implementation of the JVSTM for Deuce 
 * done by Ivo Anjo.
 */
@ExcludeInternal
public class VBoxContainer {

	public static class WeakHashedReference<T> extends WeakReference<T> {
		private final int hashCode;

		WeakHashedReference(T referent, int hashCode, ReferenceQueue<? super T> q) {
			super(referent, q);
			this.hashCode = hashCode;
		}

		final int referentHashCode() {
			return hashCode;
		}
	}
	
	public static class Key {
		public WeakHashedReference<Object> obj;
		public long field;
		public int hashCode;

		public Key(Object obj, long field) {
			this.hashCode = System.identityHashCode(obj) + (123 * (int) field);
			this.obj = new WeakHashedReference<Object>(obj, this.hashCode, VBoxContainer.refQueue);
			this.field = field;
		}

		public Key(WeakHashedReference<Object> obj) {
			this.obj = obj;
			this.field = 0xF000DEAD; // _offset should never be accessed on a MapKey
									// for GC'ing, but just in case
			// we set it to a stupid value that would easily be spotted while
			// debugging
			this.hashCode = obj.referentHashCode();
		}
		
		public int hashCode() {
			return hashCode;
		}

		public boolean equals(Object o) {
			if (o == null || !(o instanceof Key)) {
				return false;
			}
			Key k = (Key) o;
			if (this.obj == k.obj) {
				return true;
			}
			Object ti = this.obj.get();
			Object to = k.obj.get();
			return (ti != null) && (ti == to) && (this.field == k.field);
		}

	}

	public static final ReferenceQueue<Object> refQueue = new ReferenceQueue<Object>();
	public static final ConcurrentHashMap<Key, VBox> vboxes = new ConcurrentHashMap<VBoxContainer.Key, VBox>();
	
	static {
		(new Thread("org.deuce.transaction.jvstm2.VBoxContainer GC Thread") {
			{
				setDaemon(true);
			}

			@Override
			@SuppressWarnings("unchecked")
			public void run() {
				while (true)
					try {
						WeakHashedReference<Object> ref = (WeakHashedReference<Object>) refQueue
								.remove();
						Key key = new Key(ref);
//						while (vboxes.remove(key) != null) {
//						}
						vboxes.remove(key);
					} catch (InterruptedException e) {
						throw new Error(e);
					}
			}
		}).start();
	}

	public static <T> VBox getVBoxO(Object obj, long field, T initValue) {
		Key key = new Key(obj, field);
		if (!vboxes.containsKey(key)) {
			VBox res = new VBoxO(initValue);
			VBox prev = vboxes.putIfAbsent(key, res);
			return prev == null ? res : prev;
		} else {
			return (VBox) vboxes.get(key);
		}
	}

	public static <T> VBox getVBoxZ(Object obj, long field, boolean initValue) {
		Key key = new Key(obj, field);
		if (!vboxes.containsKey(key)) {
			VBox res = new VBoxZ(initValue);
			VBox prev = vboxes.putIfAbsent(key, res);
			return prev == null ? res : prev;
		} else {
			return (VBox) vboxes.get(key);
		}
	}

	public static <T> VBox getVBoxB(Object obj, long field, byte initValue) {
		Key key = new Key(obj, field);
		if (!vboxes.containsKey(key)) {
			VBox res = new VBoxB(initValue);
			VBox prev = vboxes.putIfAbsent(key, res);
			return prev == null ? res : prev;
		} else {
			return (VBox) vboxes.get(key);
		}
	}

	public static <T> VBox getVBoxC(Object obj, long field, char initValue) {
		Key key = new Key(obj, field);
		if (!vboxes.containsKey(key)) {
			VBox res = new VBoxC(initValue);
			VBox prev = vboxes.putIfAbsent(key, res);
			return prev == null ? res : prev;
		} else {
			return (VBox) vboxes.get(key);
		}
	}

	public static <T> VBox getVBoxS(Object obj, long field, short initValue) {
		Key key = new Key(obj, field);
		if (!vboxes.containsKey(key)) {
			VBox res = new VBoxS(initValue);
			VBox prev = vboxes.putIfAbsent(key, res);
			return prev == null ? res : prev;
		} else {
			return (VBox) vboxes.get(key);
		}
	}

	public static <T> VBox getVBoxF(Object obj, long field, float initValue) {
		Key key = new Key(obj, field);
		if (!vboxes.containsKey(key)) {
			VBox res = new VBoxF(initValue);
			VBox prev = vboxes.putIfAbsent(key, res);
			return prev == null ? res : prev;
		} else {
			return (VBox) vboxes.get(key);
		}
	}

	public static <T> VBox getVBoxD(Object obj, long field, double initValue) {
		Key key = new Key(obj, field);
		if (!vboxes.containsKey(key)) {
			VBox res = new VBoxD(initValue);
			VBox prev = vboxes.putIfAbsent(key, res);
			return prev == null ? res : prev;
		} else {
			return (VBox) vboxes.get(key);
		}
	}

	public static <T> VBox getVBoxI(Object obj, long field, int initValue) {
		Key key = new Key(obj, field);
		if (!vboxes.containsKey(key)) {
			VBox res = new VBoxI(initValue);
			VBox prev = vboxes.putIfAbsent(key, res);
			return prev == null ? res : prev;
		} else {
			return (VBox) vboxes.get(key);
		}
	}

	public static <T> VBox getVBoxL(Object obj, long field, long initValue) {
		Key key = new Key(obj, field);
		if (!vboxes.containsKey(key)) {
			VBox res = new VBoxL(initValue);
			VBox prev = vboxes.putIfAbsent(key, res);
			return prev == null ? res : prev;
		} else {
			return (VBox) vboxes.get(key);
		}
	}

}
