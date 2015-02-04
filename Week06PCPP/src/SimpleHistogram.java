import net.jcip.annotations.GuardedBy;

// For week 3
// sestoft@itu.dk * 2014-09-04

class SimpleHistogram {
	// public static void main(String[] args) {
	// final Histogram histogram = new Histogram1(30);
	// histogram.increment(7);
	// histogram.increment(13);
	// histogram.increment(7);
	// dump(histogram);
	// }

	public static void dump(Histogram histogram) {
		int totalCount = 0;
		for (int item = 0; item < histogram.getSpan(); item++) {
			System.out.printf("%4d: %9d%n", item, histogram.getCount(item));
			totalCount += histogram.getCount(item);
		}
		System.out.printf("      %9d%n", totalCount);
	}

}

interface Histogram {
	public void increment(int item);

	public int getCount(int item);

	public int getSpan();

	void addAll(Histogram hist);
}

// class Histogram1 implements Histogram {
// private int[] counts;
// public Histogram1(int span) {
// this.counts = new int[span];
// }
// public void increment(int item) {
// counts[item] = counts[item] + 1;
// }
// public int getCount(int item) {
// return counts[item];
// }
// public int getSpan() {
// return counts.length;
// }
// }

class Histogram2 implements Histogram {
	// Doesn't need final, because it is not accessible from anywhere
	// (encapsulated)
	@GuardedBy("this")
	private final int[] counts;

	public Histogram2(int span) {
		this.counts = new int[span];
	}

	// Needs to be synchronized because if potential race condition
	public synchronized void increment(int item) {
		counts[item] = counts[item] + 1;
	}

	// Needs to be synchronized because if potential race condition
	public synchronized int getCount(int item) {
		return counts[item];
	}

	// does not need to be synchronized, because it is considered effectively
	// immutable
	public synchronized int getSpan() {
		return counts.length;
	}

	@Override
	public void addAll(Histogram hist) {
		synchronized (this) {
			if (this.getSpan() != hist.getSpan()) {
				throw new RuntimeException();
			}
			for (int i = 0; i < counts.length; i++) {
				this.counts[i] += hist.getCount(i);
			}
		}

	}

//	public void addAllMort(Histogram2 hist) {
//		synchronized (this) {
//			if (this.getSpan() != hist.getSpan()) {
//				throw new RuntimeException();
//			}
//			for (int i = 0; i < counts.length; i++) {
//				this.counts[i] += hist.counts[i];
//			}
//		}
//	}
}

class TestCountFactors {
	private static int count;

	public static void main(String[] args) {
		final int range = 5_000_000;
		final Histogram2 histogram = new Histogram2(range);

		for (int p = 0; p < range; p++)
			count += countFactors(p);

		System.out.printf("Total number of factors is %9d%n", count);
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