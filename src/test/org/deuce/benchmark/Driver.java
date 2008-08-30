package org.deuce.benchmark;

import org.deuce.transform.Exclude;

@Exclude
public class Driver {

	public static void main(String[] args) {
		int nb_threads = 8;
		int duration = 10000;
		Long seed = null;
		String benchmark = null;
		boolean error = false;
		int arg;

		for (arg = 0; arg < args.length && !error; arg++) {
			if (args[arg].equals("-n")) {
				if (++arg < args.length)
					nb_threads = Integer.parseInt(args[arg]);
				else
					error = true;
			} else if (args[arg].equals("-d")) {
				if (++arg < args.length)
					duration = Integer.parseInt(args[arg]);
				else
					error = true;
			} else if (args[arg].equals("-s")) {
				if (++arg < args.length)
					seed = new Long(args[arg]);
				else
					error = true;
			} else
				break;
		}
		if (arg < args.length) {
			benchmark = args[arg++];
			String[] s = new String[args.length - arg];
			System.arraycopy(args, arg, s, 0, s.length);
			args = s;
		} else
			error = true;

		if (error) {
			System.out.println("Usage: java Driver [-n nb-threads] [-d duration-ms] [-s seed] benchmark [args...]");
			System.exit(1);
		}

		if (seed != null)
			BenchmarkThread.initPRNG(seed.longValue());

		Benchmark b = null;
		try {
			Class<?> c = Class.forName(benchmark);
			b = (Benchmark)c.newInstance();
		} catch(Exception e) {
			System.err.println("Unexpected exception: " + e.getMessage());
			System.exit(1);
		}

		b.init(args);

		BenchmarkThread[] bt = new BenchmarkThread[nb_threads];
		for (int i = 0; i < bt.length; i++)
			bt[i] = b.createThread(i);

		Thread[] t = new Thread[bt.length];
		for (int i = 0; i < t.length; i++)
			t[i] = new Thread(bt[i]);

		System.out.print("Starting threads...");
		for (int i = 0; i < t.length; i++) {
			System.out.print(" " + i);
			t[i].start();
		}
		System.out.println();

		long start = System.currentTimeMillis();
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
		}
		long end = System.currentTimeMillis();

		System.out.print("Stopping threads...");
		for (int i = 0; i < bt.length; i++) {
			System.out.print(" " + i);
			bt[i].end();
		}
		System.out.println();

		System.out.println("Waiting for threads to finish...");
		for (int i = 0; i < t.length; i++) {
			try {
				t[i].join();
			} catch (InterruptedException e) {
			}
		}
		System.out.println("All threads returned successfully");

		int steps = 0;
		for (int i = 0; i < bt.length; i++)
			steps += bt[i].getSteps();

		System.out.println("RESULTS:\n");
		System.out.println("  Test duration (ms) = " + (end - start));
		System.out.println("  Nb iterations      = " + steps);
		System.out.println("  Stats              = " + b.stats(bt));
		for (int i = 0; i < bt.length; i++)
			System.out.println("    " + i + " : " + bt[i].getSteps() +
					" (" + bt[i].stats() + ")");
	}
}
