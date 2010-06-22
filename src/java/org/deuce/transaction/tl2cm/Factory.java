package org.deuce.transaction.tl2cm;

import org.deuce.transaction.tl2cm.cm.Aggressive;
import org.deuce.transaction.tl2cm.cm.ContentionManager;
import org.deuce.transaction.tl2cm.cm.Karma;
import org.deuce.transaction.tl2cm.cm.KarmaLockStealer;
import org.deuce.transaction.tl2cm.cm.KillPrioLockStealer;
import org.deuce.transaction.tl2cm.cm.LockStealer;
import org.deuce.transaction.tl2cm.cm.Polite;
import org.deuce.transaction.tl2cm.cm.Polka;
import org.deuce.transaction.tl2cm.cm.Suicide;
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
			cm = new KarmaLockStealer();
		}
		else if ("Polka".equals(cmId)) {
			cm = new Polka(10);
		}
		else if ("LockStealer".equals(cmId)) {
			cm = new LockStealer();
		}
		else if ("KillPrioLockStealer".equals(cmId)) {
			cm = new KillPrioLockStealer();
		}
		else {
			cm = new KillPrioLockStealer();	// This is the default CM
		}
		return cm;
	}

}
