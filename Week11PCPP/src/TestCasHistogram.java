import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCasHistogram {

	public static void main(String[] args) {
		final Histogram total = new CasHistogram(30);
		countPrimeFactorsWithCasHistogram(total);
	}

	private static void countPrimeFactorsWithCasHistogram(Histogram total) {
		final Histogram histogram = new CasHistogram(30);
		final int range = 4_000_000;
		final int threadCount = 10, perThread = range / threadCount;
		final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1), stopBarrier = startBarrier;
		final Thread[] threads = new Thread[threadCount];
		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t, to = (t + 1 == threadCount) ? range
					: perThread * (t + 1);
			threads[t] = new Thread(new Runnable() {
				public void run() {
					try {
						startBarrier.await();
					} catch (Exception exn) {
					}
					for (int p = from; p < to; p++)
						histogram.increment(countFactors(p));
					System.out.print("*");
					try {
						stopBarrier.await();
					} catch (Exception exn) {
					}
				}
			});
			threads[t].start();
		}
		Timer time = null;
		try {
			startBarrier.await();
			time = new Timer();
		} catch (Exception exn) {
		}
		int numchecks = 0;
		while (stopBarrier.getNumberWaiting() != stopBarrier.getParties() - 1) {
			try {
				Thread.sleep(30);
				total.transferBins(histogram);
				numchecks++;
			} catch (InterruptedException e) {
				break;
			}
		}
		try {
			stopBarrier.await();
			System.out.println("Time to complete: " + time.check() + " ms");
		} catch (Exception exn) {
		}

		System.out.println("number of checks: " + numchecks);
		System.out.println("\n" + total.getClass().getName() + " ->");
		// dump(histogram);
		// System.out.println("total:");
		dump(total);
	}

	public static void dump(Histogram histogram) {
		int totalCount = 0;
		// System.out.println();
		for (int bin = 0; bin < histogram.getSpan(); bin++) {
			System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
			totalCount += histogram.getCount(bin);
		}
		System.out.printf("      %9d%n", totalCount);
	}

	public static int countFactors(int p) {
		if (p < 2)
			return 0;
		int factorCount = 1, k = 2;
		while (p >= k * k) {
			if (p % k == 0) {
				factorCount++;
				p /= k;
			} else
				k++;
		}
		return factorCount;
	}
}

interface Histogram {
	void increment(int bin);

	int getCount(int bin);

	int getSpan();

	int[] getBins();

	int getAndClear(int bin);

	void transferBins(Histogram hist);
}

class CasHistogram implements Histogram {
	AtomicInteger[] counts;

	public CasHistogram(int span) {
		counts = new AtomicInteger[span];
		for (int bin = 0; bin < counts.length; bin++) {
			counts[bin] = new AtomicInteger(0);
		}
	}

	@Override
	public void increment(int bin) {
		// TODO Whatever value the bin had before, increment it by one.
		int old;
		int newValue;
		do {
			old = counts[bin].get();
			newValue = old + 1;
		} while (!counts[bin].compareAndSet(old, newValue));
	}

	@Override
	public int getCount(int bin) {
		// TODO - just read the value
		return counts[bin].get();
	}

	@Override
	public int getSpan() {
		// TODO - just read the value (the length of the array)
		return counts.length;
	}

	@Override
	public int[] getBins() {
		// TODO - just a simple read
		int[] arr = new int[getSpan()];
		for (int bin = 0; bin < arr.length; bin++) {
			arr[bin] = getCount(bin);
		}
		return arr;
	}

	@Override
	public int getAndClear(int bin) {
		// TODO - read what is there, set the value to zero, and then return the
		// value.
		int old;
		do {
			old = counts[bin].get();
		} while (!counts[bin].compareAndSet(old, 0));

		return old;
	}

	@Override
	public void transferBins(Histogram hist) {
		// TODO - getAndClear and then add that value to whatever what there
		// before
		for (int bin = 0; bin < getSpan(); bin++) {
			int value;
			int newValue;
			int old;
			value = hist.getAndClear(bin); // is already safe
			do {
				old = counts[bin].get();
				newValue = old + value;
			} while (!counts[bin].compareAndSet(old, newValue));
		}
	}
}
