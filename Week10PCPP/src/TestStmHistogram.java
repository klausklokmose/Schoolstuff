// For week 10
// sestoft@itu.dk * 2014-11-05

// NOT TO BE HANDED OUT, CONTAINS SOLUTIONS FOR WEEK 10

// Compile and run like this:
//   javac -cp ~/lib/multiverse-core-0.7.0.jar TestStmHistogram.java
//   java -cp ~/lib/multiverse-core-0.7.0.jar:. TestStmHistogram

// For the Multiverse library:
import static org.multiverse.api.StmUtils.atomic;

import org.multiverse.api.references.TxnInteger;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

import static org.multiverse.api.StmUtils.*;

// Multiverse locking:

class TestStmHistogram {

	public static void main(String[] args) {
		final Histogram total = new StmHistogram(30);
		countPrimeFactorsWithStmHistogram(total);
	}

	private static void countPrimeFactorsWithStmHistogram(Histogram total) {
		final Histogram histogram = new StmHistogram(30);
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
		while (stopBarrier.getNumberWaiting() != stopBarrier.getParties() - 1) {
			try {
				Thread.sleep(30);
				total.transferBins(histogram);
			} catch (InterruptedException e) {
				break;
			}
		}
		try {
			stopBarrier.await();
			System.out.println("Time to complete: "+time.check()+" ms");
		} catch (Exception exn) {
		}
//		System.out.println("\nhistogram:");
//		dump(histogram);
		System.out.println("total:");
		dump(total);
	}

	public static void dump(Histogram histogram) {
		int totalCount = 0;
		System.out.println();
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

class StmHistogram implements Histogram {
	private final TxnInteger[] counts;

	public StmHistogram(int span) {
		counts = new TxnInteger[span];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = newTxnInteger(0);
		}
	}

	public void increment(int bin) {
		atomic(new Runnable() {
			public void run() {
				// counts[bin].set(counts[bin].get() + 1);
				counts[bin].increment();
			}
		});
	}

	public int getCount(int bin) {
		return atomic(new Callable<Integer>() {
			public Integer call() throws Exception {
				return counts[bin].get();
			}
		});
	}

	// the span is considered effectively immutable as it is only written in the
	// constructor.
	public int getSpan() {
		return counts.length;
	}

	public int[] getBins() {
		int[] arr = new int[getSpan()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = getCount(i); // getCount is a transaction, so no need to
									// make a big transaction here
		}
		return arr;
	}

	public int getAndClear(int bin) {
		return atomic(new Callable<Integer>() {
			public Integer call() {
				int value = counts[bin].get();
				counts[bin].set(0);
				return value;
			}
		});
	}

	public void transferBins(final Histogram that) {
		// if(this.getSpan() > that.getSpan()){
		// throw new Exception();
		// }
		for (int i = 0; i < getSpan(); i++) {
			final int j = i;
			atomic(new Runnable() {
				public void run() {
					int value = that.getAndClear(j);
					counts[j].increment(value);
//					for (int k = 0; k < value; k++) {
//						increment(j);
//					}
				}
			});

		}
	}
}
