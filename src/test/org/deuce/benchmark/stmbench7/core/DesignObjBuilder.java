package org.deuce.benchmark.stmbench7.core;

import org.deuce.benchmark.stmbench7.Parameters;
import org.deuce.benchmark.stmbench7.ThreadRandom;
import org.deuce.benchmark.stmbench7.annotations.Immutable;

/**
 * Methods for constructing common attributes of objects implementing parts of
 * the data structure.
 */
@Immutable
public abstract class DesignObjBuilder {

	protected final DesignObjFactory designObjFactory = DesignObjFactory.instance;

	protected String createType() {
		String type = "type #" + ThreadRandom.nextInt(Parameters.NumTypes);
		return type;
	}

	protected int createBuildDate(int minBuildDate, int maxBuildDate) {
		return minBuildDate
				+ ThreadRandom.nextInt(maxBuildDate - minBuildDate + 1);
	}

	protected String createText(int textSize, String textPattern) {
		int patternSize = textPattern.length();
		int size = 0;
		StringBuilder stringBuilder = new StringBuilder(textSize);

		while (size + patternSize <= textSize) {
			stringBuilder.append(textPattern);
			size += patternSize;
		}

		return stringBuilder.toString();
	}
}
