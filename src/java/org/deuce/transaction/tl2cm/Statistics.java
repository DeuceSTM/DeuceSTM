package org.deuce.transaction.tl2cm;

import java.util.HashMap;
import java.util.Map;

import org.deuce.transform.Exclude;

/**
* @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
* @since 1.2
*/
@Exclude
public class Statistics {

	public enum AbortType {ALL, SPECULATION, COMMIT, COMMIT_READSET_VALIDATION, COMMIT_WRITESET_LOCKING, SPECULATION_READVERSION, SPECULATION_LOCATION_LOCKED} 
	
	private static final Map<Integer, Statistics> statsMap = new HashMap<Integer, Statistics>();
	
	public Statistics(int threadId) {
		// We don't want the main application thread to be included
		// in the statistics
		if (threadId != 1) {
			statsMap.put(threadId, this);
		}
	}
	
	public static int getTotalStarts() {
		int totalStarts = 0;
		for (Map.Entry<Integer, Statistics> entry : statsMap.entrySet()) {
			Statistics statistics = entry.getValue();
			totalStarts += statistics.starts;
		}
		return totalStarts;
	}

	public static int getTotalCommits() {
		int totalCommits = 0;
		for (Map.Entry<Integer, Statistics> entry : statsMap.entrySet()) {
			Statistics statistics = entry.getValue();
			totalCommits += statistics.commits;
		}
		return totalCommits;
	}

	public static int getTotalAborts() {
		int totalAborts = 0;
		for (Map.Entry<Integer, Statistics> entry : statsMap.entrySet()) {
			Statistics statistics = entry.getValue();
			totalAborts += statistics.getAborts(AbortType.ALL);
		}
		return totalAborts;
	}

	public static double getTotalAbortsPercentage(AbortType type) {
		int abortsSum = 0, totalStarts = 0;
		for (Map.Entry<Integer, Statistics> entry : statsMap.entrySet()) {
			Statistics statistics = entry.getValue();
			if (type.equals(AbortType.ALL)) {
				abortsSum += statistics.getAborts(AbortType.ALL);
			}
			else if (type.equals(AbortType.COMMIT)) {
				abortsSum += statistics.getAborts(AbortType.COMMIT);
			}
			else if (type.equals(AbortType.COMMIT_READSET_VALIDATION)) {
				abortsSum += statistics.getAborts(AbortType.COMMIT_READSET_VALIDATION);
			}
			else if (type.equals(AbortType.COMMIT_WRITESET_LOCKING)) {
				abortsSum += statistics.getAborts(AbortType.COMMIT_WRITESET_LOCKING);
			}
			else if (type.equals(AbortType.SPECULATION_READVERSION)) {
				abortsSum += statistics.getAborts(AbortType.SPECULATION_READVERSION);
			}
			else if (type.equals(AbortType.SPECULATION_LOCATION_LOCKED)) {
				abortsSum += statistics.getAborts(AbortType.SPECULATION_LOCATION_LOCKED);
			}
			else if (type.equals(AbortType.SPECULATION)) {
				abortsSum += statistics.getAborts(AbortType.SPECULATION);
			}
			totalStarts += statistics.starts;
		}
		return percentage(abortsSum, totalStarts);
	}
	
	public static double getTotalAvgReadSetSize() {
		double sumOfAverages = 0;
		for (Map.Entry<Integer, Statistics> entry : statsMap.entrySet()) {
			Statistics statistics = entry.getValue();
			sumOfAverages += statistics.getAvgReadSetSizeOnCommit();
		}
		return average(sumOfAverages, statsMap.size());
	}

	public static double getTotalAvgWriteSetSize() {
		double sumOfAverages = 0;
		for (Map.Entry<Integer, Statistics> entry : statsMap.entrySet()) {
			Statistics statistics = entry.getValue();
			sumOfAverages += statistics.getAvgWriteSetSizeOnCommit();
		}
		return average(sumOfAverages, statsMap.size());
	}

	public static double getTotalAvgIndexInReadSetValidationFailure() {
		int sumOfAverages = 0;
		for (Map.Entry<Integer, Statistics> entry : statsMap.entrySet()) {
			Statistics statistics = entry.getValue();
			sumOfAverages += statistics.getAvgReadSetValidationFailureIndex();
		}
		return average(sumOfAverages, statsMap.size());
	}

	public static double getTotalAvgIndexInWriteSetValidationFailure() {
		double sumOfAverages = 0;
		for (Map.Entry<Integer, Statistics> entry : statsMap.entrySet()) {
			Statistics statistics = entry.getValue();
			sumOfAverages += statistics.getAvgWriteSetValidationFailureIndex();
		}
		return average(sumOfAverages, statsMap.size());
	}

	public static double getTotalAvgCommitingTxTime() {
		int sum = 0;
		for (Map.Entry<Integer, Statistics> entry : statsMap.entrySet()) {
			Statistics statistics = entry.getValue();
			sum += statistics.txDurationSum;
		}
		double avgTimeInMS = average(sum, getTotalCommits());
		return 1000 * avgTimeInMS;	// return value in micro-seconds
	}

	public static String getDetailedStatistics() {
		StringBuilder sb = new StringBuilder("\n\n");
		sb.append("TL2CM Statistics:\n");
		sb.append("=================\n");
		addStat("Starts                      ", getTotalStarts(), sb);
		addStat("Commits                     ", getTotalCommits(), sb);
		addStat("Aborts                      ", getTotalAborts(), sb);
		addStat("Aborts (%)                  ", getTotalAbortsPercentage(AbortType.ALL), sb);
		addStat(" - During speculation   (%) ", getTotalAbortsPercentage(AbortType.SPECULATION), sb);
		addStat("   - Newer Read Version (%) ", getTotalAbortsPercentage(AbortType.SPECULATION_READVERSION), sb);
		addStat("   - Location locked    (%) ", getTotalAbortsPercentage(AbortType.SPECULATION_LOCATION_LOCKED), sb);
		addStat(" - During commit        (%) ", getTotalAbortsPercentage(AbortType.COMMIT), sb);
		addStat("   - Writeset Locking   (%) ", getTotalAbortsPercentage(AbortType.COMMIT_WRITESET_LOCKING), sb);
		addStat("   - Readset Validation (%) ", getTotalAbortsPercentage(AbortType.COMMIT_READSET_VALIDATION), sb);
		addStat("Avg. read set size          ", getTotalAvgReadSetSize(), sb);
		addStat("Avg. fail index in rs valid.", getTotalAvgIndexInReadSetValidationFailure(), sb);
		addStat("Avg. write set size         ", getTotalAvgWriteSetSize(), sb);
		addStat("Avg. fail index in ws lock. ", getTotalAvgIndexInWriteSetValidationFailure(), sb);
		addStat("Avg. commiting TX time (us) ", getTotalAvgCommitingTxTime(), sb);
		return sb.toString();
	}

	private static void addStat(String title, Object value, StringBuilder sb) {
		sb.append("  ");
		sb.append(title);
		sb.append(": ");
		sb.append(value);
		sb.append("\n");
	}
	
	private int starts = 0;
	private int abortsDuringCommitWritesetLocking;
	private int abortsDuringCommitReadSetValidation;
	private int abortsDuringSpeculationNewerReadVersion = 0;
	private int abortsDuringSpeculationLocationLocked = 0;
	private int commits = 0;
	
	private long startTime;
	private long txDurationSum = 0;
	
	private int readSetValidationFailureSum = 0;
	private int readSetValidationFailureCounter = 0;
	
	private int writeSetValidationFailureSum = 0;
	private int writeSetValidationFailureCounter = 0;
	
	private int readSetSizeOnCommitSum = 0;
	private int readSetSizeOnCommitCounter = 0;
	private int writeSetSizeOnCommitSum = 0;
	private int writeSetSizeOnCommitCounter = 0;
	
	public void reportTxStart() {
		this.starts++;
		this.startTime = System.currentTimeMillis();
	}
	
	public void reportAbort(AbortType type) {
		startTime = -1;		// noting not to profile this transaction's duration 
		if (type.equals(AbortType.COMMIT_READSET_VALIDATION)) {
			this.abortsDuringCommitReadSetValidation++;
		}
		else if (type.equals(AbortType.COMMIT_WRITESET_LOCKING)) {
			this.abortsDuringCommitWritesetLocking++;
		}
		else if (type.equals(AbortType.SPECULATION_READVERSION)) {
			this.abortsDuringSpeculationNewerReadVersion++;
		}
		else if (type.equals(AbortType.SPECULATION_LOCATION_LOCKED)) {
			this.abortsDuringSpeculationLocationLocked++;
		}
	}
	
	public void reportCommit() {
		this.commits++;
		if (startTime != -1) {
			long txDuration = System.currentTimeMillis() - startTime;
			txDurationSum += txDuration;
		}
	}
	
	public int getAborts(AbortType type) {
		if (type.equals(AbortType.ALL)) {
			return starts - commits;
		}
		else if (type.equals(AbortType.COMMIT)) {
			return abortsDuringCommitReadSetValidation + abortsDuringCommitWritesetLocking;
		}
		else if (type.equals(AbortType.COMMIT_READSET_VALIDATION)) {
			return abortsDuringCommitReadSetValidation;
		}
		else if (type.equals(AbortType.COMMIT_WRITESET_LOCKING)) {
			return abortsDuringCommitWritesetLocking;
		}
		else if (type.equals(AbortType.SPECULATION_READVERSION)) {
			return abortsDuringSpeculationNewerReadVersion;
		} 
		else if (type.equals(AbortType.SPECULATION_LOCATION_LOCKED)) {
			return abortsDuringSpeculationLocationLocked;
		}
		else if (type.equals(AbortType.SPECULATION)) {
			return getAborts(AbortType.ALL) - getAborts(AbortType.COMMIT);
		}
		throw new IllegalArgumentException("AbortType unrecognized " + type);
	}

	public void reportReadSetValidationFailureDuringCommit(int failedAtIndex) {
		readSetValidationFailureCounter++;
		readSetValidationFailureSum += failedAtIndex;
	}
	
	public double getAvgReadSetValidationFailureIndex() {
		return average(readSetValidationFailureSum, readSetValidationFailureCounter); 
	}
	
	public void reportWriteSetValidationFailureDuringCommit(int failedAtIndex) {
		writeSetValidationFailureCounter++;
		writeSetValidationFailureSum += failedAtIndex;
	}
	
	public double getAvgWriteSetValidationFailureIndex() {
		return average(writeSetValidationFailureSum, writeSetValidationFailureCounter);
	}

	public void reportOnCommit(int rsSize, int wsSize) {
		readSetSizeOnCommitSum += rsSize;
		readSetSizeOnCommitCounter++;
		if (wsSize > 0) {
			// We only consider writing transactions
			// when we calculate average write-set size
			writeSetSizeOnCommitSum += wsSize;
			writeSetSizeOnCommitCounter++;
		}
	}
	
	public double getAvgReadSetSizeOnCommit() {
		return average(readSetSizeOnCommitSum, readSetSizeOnCommitCounter);
	}

	public double getAvgWriteSetSizeOnCommit() {
		return average(writeSetSizeOnCommitSum, writeSetSizeOnCommitCounter);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Starts: ");
		sb.append(starts);
		sb.append(" Commits: ");
		sb.append(commits);
		sb.append(" Aborts: ");
		int aborts = starts-commits;
		sb.append(aborts);
		double restartRate = (double)starts / commits;
		sb.append(" Restart Rate: ");
		sb.append(restartRate);
		double abortsPercentage = (double) aborts / starts;
		sb.append(" Aborts Percentage: ");
		sb.append(abortsPercentage);
		return sb.toString();
	}

	private static double percentage(int p, int w) {
		return (double)p / w * 100;
	}

	private static double average(double sum, double n) {
		return sum / n;
	}
	
}
