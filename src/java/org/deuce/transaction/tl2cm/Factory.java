package org.deuce.transaction.tl2cm;

import org.deuce.transaction.tl2cm.cm.Aggressive;
import org.deuce.transaction.tl2cm.cm.ContentionManager;
import org.deuce.transaction.tl2cm.cm.Karma;
import org.deuce.transaction.tl2cm.cm.Polite;
import org.deuce.transaction.tl2cm.cm.Polka;
import org.deuce.transaction.tl2cm.cm.Suicide;
import org.deuce.transaction.tl2cm.contexts.ArrayContextsMap;
import org.deuce.transaction.tl2cm.contexts.CHMContextsMap;
import org.deuce.transaction.tl2cm.contexts.COWALContextsMap;
import org.deuce.transaction.tl2cm.contexts.ContextsMap;
import org.deuce.transaction.tl2cm.contexts.HashMapContextsMap;
import org.deuce.transaction.tl2cm.contexts.RWLockContextsMap;
import org.deuce.transform.Exclude;

/**
 * Factory for creating contention mangers
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 */
@Exclude
public class Factory {

	private static final String TL2CM_CONTENTIONMANAGER = "org.deuce.transaction.tl2cm.ContentionManager";
	private static final String TL2CM_CONTEXTSMAP = "org.deuce.transaction.tl2cm.ContextsMap";
	
	public static ContentionManager createContentionManager() {
		String cmId = System.getProperty(TL2CM_CONTENTIONMANAGER);
		ContentionManager cm = null;
		int constant = getConstant();
		if ("Suicide".equals(cmId)) {
			cm = new Suicide();
		}
		else if ("Aggressive".equals(cmId)) {
			cm = new Aggressive();
		}
		if ("Polite".equals(cmId)) {
			cm = new Polite(2);
		}
		else if ("Karma".equals(cmId)) {
			cm = new Karma(4);
		}
		else if ("Polka".equals(cmId)) {
			cm = new Polka(4);
		} 
		else {
			cm = new Suicide();	// This is the default CM
		}
		return cm;
	}
	
	public static ContextsMap createContextsMap() {
		String contextsMapStr = System.getProperty(TL2CM_CONTEXTSMAP);
		if ("CHM".equals(contextsMapStr)) {
			return new CHMContextsMap();
		}
		else if ("COWAL".equals(contextsMapStr)) {
			return new COWALContextsMap();
		}
		else if ("RWL".equals(contextsMapStr)) {
			return new RWLockContextsMap();
		}
		else if ("ARR".equals(contextsMapStr)) {
			return new ArrayContextsMap();
		}
		else if ("HM".equals(contextsMapStr)) {
			return new HashMapContextsMap();
		}
		else {
			return new HashMapContextsMap();
		}
	}

	private static int getConstant() {
		String c = System.getProperty("constant");
		if (c != null) {
			int constant = Integer.valueOf(c);
			return constant;
		}
		else {
			return 1;
		}
	}

}
