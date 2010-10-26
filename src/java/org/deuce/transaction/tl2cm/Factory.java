package org.deuce.transaction.tl2cm;

import org.deuce.transaction.tl2cm.cm.Aggressive;
import org.deuce.transaction.tl2cm.cm.AggressiveLS;
import org.deuce.transaction.tl2cm.cm.ContentionManager;
import org.deuce.transaction.tl2cm.cm.Karma;
import org.deuce.transaction.tl2cm.cm.KarmaLS;
import org.deuce.transaction.tl2cm.cm.KillPrioLS;
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
		else if ("KarmaLS".equals(cmId)) {
			cm = new KarmaLS();
		}
		else if ("Polka".equals(cmId)) {
			cm = new Polka(10);
		}
		else if ("AggressiveLS".equals(cmId)) {
			cm = new AggressiveLS();
		}
		else if ("KillPrioLS".equals(cmId)) {
			cm = new KillPrioLS();
		}
		else {
			cm = new KillPrioLS();	// This is the default CM
		}
		return cm;
	}

}
