package org.deuce.benchmark.lee;

public interface ILeeRouter {

	int getGridSize();

	WorkQueue getNextTrack();

	boolean layNextTrack(WorkQueue t, int[][][] tempg);

}
