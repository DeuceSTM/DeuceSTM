package org.deuce.transaction.tl2cm;

import org.deuce.transaction.tl2cm.cm.Aggressive;
import org.deuce.transaction.tl2cm.cm.ContentionManager;
import org.deuce.transaction.tl2cm.cm.Karma;
import org.deuce.transaction.tl2cm.cm.KarmaLockStealer;
import org.deuce.transaction.tl2cm.cm.LockSkipper;
import org.deuce.transaction.tl2cm.cm.Polite;
import org.deuce.transaction.tl2cm.cm.Polka;
import org.deuce.transaction.tl2cm.cm.Suicide;
import org.deuce.transaction.tl2cm.cm.Timestamp;
import org.deuce.transform.Exclude;

/**
 * Factory for creating contention mangers
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 */
@Exclude 
public class Factory {

	private static final String TL2CM_CONTENTIONMANAGER = "org.deuce.transaction.tl2cm.ContentionManager";
	
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
		else if ("Polite".equals(cmId)) {
			cm = new Polite(2);
		}
		else if ("Karma".equals(cmId)) {
			cm = new Karma(4); 
		}
		else if ("KarmaLockStealer".equals(cmId)) {
			cm = new KarmaLockStealer(4);
		}
		else if ("Polka".equals(cmId)) {
			cm = new Polka(10);
		}
		else if ("Timestamp".equals(cmId)) {
			cm = new Timestamp(4);
		}
		else if ("LockSkipper".equals(cmId)) {
			cm = new LockSkipper();
		}
		else {
			cm = new Suicide();	// This is the default CM
		}
		return cm;
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
