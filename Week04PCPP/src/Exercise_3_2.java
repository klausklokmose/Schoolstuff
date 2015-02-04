public class Exercise_3_2 {

	public static void main(String[] args) {
		
		Factorizer f = new Factorizer();
		exerciseFactorizer(new Memoizer1<Long, long[]>(f));
		System.out.println(f.getCount());
	}

	static double exerciseFactorizer(final Computable<Long, long[]> f) {
		final int threadCount = 16;
		final long start = 10_000_000_000L, range = 2_000L;
//		System.out.println(f.getClass());

		Thread[] threads = new Thread[threadCount];

		for (int i = 0; i < threadCount; i++) {
			final int c = i;
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					long endRange = start + range;
					for (long j = start; j < endRange; j++) {
						try {
							f.compute(j);
							f.compute(j + endRange + (c * 500));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
		for (Thread thread : threads) {
			thread.run();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return f.hashCode();
	}

}