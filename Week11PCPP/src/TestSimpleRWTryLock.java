import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class TestSimpleRWTryLock {
	private static final String sp = " SHOULD PRINT ";

	public static void main(String[] args) {
		// testSequentialTryLock(new SimpleRWTryLock());

		// 11.2.6 Write slightly more advanced test cases that use at least two
		// threads to test basic lock functionality
		SimpleRWTryLock lock = new SimpleRWTryLock();
		testParallelTryLock(16, lock);

	}

	private static void testParallelTryLock(int n, SimpleRWTryLock lock) {
		final CyclicBarrier barrier = new CyclicBarrier(n + 1);
		final Thread[] threads = new Thread[n];
		final AtomicInteger readerTryLocks = new AtomicInteger();
		final AtomicInteger readerUnlocked = new AtomicInteger();
		final AtomicInteger writerTryLocks = new AtomicInteger();
		final AtomicInteger writerUnlocked = new AtomicInteger();

		for (int t = 0; t < threads.length; t++) {
			threads[t] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int nReaderLocksTaken = 0, nReaderLocksReleased = 0, nWriteLocksTaken = 0, nWriteLocksReleased = 0;
						ThreadLocalRandom rnd = ThreadLocalRandom.current();
						barrier.await();
						// TODO do stuff
						for (int i = 0; i < 1000; i++) {
							int index = rnd.nextInt(3);
							switch (index) {
								case 0:
									if (lock.readerTryLock()) {
										nReaderLocksTaken++;
										if(rnd.nextBoolean()){
											Thread.sleep(2);
										}
										try {
											lock.readerUnlock();
											nReaderLocksReleased++; // will only be
											// called if unlock
											// was successful
										} catch (Exception e1) {
											// e1.printStackTrace();
										}
									}
									break;
								case 1:
									if (lock.writerTryLock()) {
										nWriteLocksTaken++;
										if(rnd.nextBoolean()){
											Thread.sleep(2);
										}
										try {
											lock.writerUnlock();
											nWriteLocksReleased++; // will only be
											// called if unlock
											// was successful
										} catch (Exception e) {
											// e.printStackTrace();
										}
									}
									break;
								default:
									if(rnd.nextBoolean()){
										Thread.sleep(2);
									}
									break;
							}
									
						}
						readerTryLocks.addAndGet(nReaderLocksTaken);
						readerUnlocked.addAndGet(nReaderLocksReleased);
						writerTryLocks.addAndGet(nWriteLocksTaken);
						writerUnlocked.addAndGet(nWriteLocksReleased);

						// tell main thread that we have finished
						barrier.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						e.printStackTrace();
					}
				}
			});
			// will initialize and wait for the other threads to be ready!
			threads[t].start();
		}
		// MAIN thread
		try {
			barrier.await();
			barrier.await();
			
			System.out.println("writerTryLocks: " + writerTryLocks.get());
			System.out.println("writerUnlocked: " + writerUnlocked.get());
			System.out.println("readerTryLocks: " + readerTryLocks.get());
			System.out.println("readerUnlocked: " + readerUnlocked.get());
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	private static void testSequentialTryLock(SimpleRWTryLock lock) {
		assert lock.readerTryLock() == true;
		assert lock.writerTryLock() == false;
		try {
			// should unlock reader
			lock.readerUnlock();
		} catch (Exception e) {
			// should NOT print
			e.printStackTrace();
		}
		assert lock.writerTryLock() == true;
		assert lock.readerTryLock() == false;
		try {
			// should NOT unlock anything
			lock.readerUnlock();
		} catch (Exception e) {
			// should print
			System.out.println(sp + e.getMessage() + sp);
		}
		try {
			lock.writerUnlock();
		} catch (Exception e) {
			// should NOT print
			e.printStackTrace();
		}
		assert lock.readerTryLock() == true;
		assert lock.readerTryLock() == true;
		try {
			lock.writerUnlock();
		} catch (Exception e) {
			// should print
			System.out.println(sp + e.getMessage() + sp);
		}
		try {
			// should unlock the readerlock
			lock.readerUnlock();
			lock.readerUnlock();
		} catch (Exception e) {
			// should not print
			e.printStackTrace();
		}
		try {
			lock.readerUnlock();
		} catch (Exception e) {
			// should print
			System.out.println(sp + e.getMessage() + sp);
		}
		try {
			lock.writerUnlock();
		} catch (Exception e) {
			// should print
			System.out.println(sp + e.getMessage() + sp);
		}

	}

}
