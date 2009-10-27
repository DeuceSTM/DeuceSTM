package org.deuce.transaction.tl2cm;

import org.deuce.transform.Exclude;

/**
* @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
* @since 1.2
*/
@Exclude
public class Statistics {

	public int starts = 0;
	public int commits = 0;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Starts: ");
		sb.append(starts);
		sb.append(" Commits: ");
		sb.append(commits);
		sb.append(" Aborts: ");
		sb.append(starts-commits);
		double ratio = (double)starts / commits;
		sb.append(" Ratio: ");
		sb.append(ratio);
		return sb.toString();
	}
	
}
