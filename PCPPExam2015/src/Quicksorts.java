// For PCPP exam January 2015
// sestoft@itu.dk * 2015-01-03

// Several versions of sequential and parallel quicksort: 
// A: sequential recursive
// B: sequential using work a deque as a stack

// To do by students:
// C: single-queue multi-threaded with shared lock-based queue
// D: multi-queue multi-threaded with thread-local lock-based queues and stealing
// E: as D but with thread-local lock-free queues and stealing

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import javax.annotation.concurrent.GuardedBy;

public class Quicksorts extends Tests {
	final static int size = 20_000_000; // Number of integers to sort
	final static int testArrSize = 15; // Number of integers to sort in small
										// test

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// SystemInfo();
		// sequentialRecursive();
		// singleQueueSingleThread();
		// System.out.println("#threads:\t\tTime (sec):");
		// for (int i = 1; i < 9; i++)
		// singleQueueMultiThread(i);
		// simpleDequeTestSequential(new SimpleDeque<Integer>(size));
		// new PutTakeTest(new SimpleDeque<Integer>(size), 4, 200_000).test();
		// for (int i = 1; i < 9; i++)
		// multiQueueMultiThread(i);
		// simpleDequeTestSequential(new ChaseLevDeque(100_000));
		// new PutTakeTest(new ChaseLevDeque<Integer>(100_000), 8, 10_000_000)
		// .test2();
		 for (int i = 1; i < 9; i++)
			 multiQueueMultiThreadCL(i);
		// multiQueueMultiThreadCL(8);
	}

	public static void SystemInfo() {
		System.out
				.printf("# OS:   %s; %s; %s%n", System.getProperty("os.name"),
						System.getProperty("os.version"),
						System.getProperty("os.arch"));
		System.out.printf("# JVM:  %s; %s%n",
				System.getProperty("java.vendor"),
				System.getProperty("java.version"));
		// This line works only on MS Windows:
		System.out
				.printf("# CPU:  %s%n", System.getenv("PROCESSOR_IDENTIFIER"));
		java.util.Date now = new java.util.Date();
		System.out.printf("# Date: %s%n", new java.text.SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssZ").format(now));
	}

	private static void simpleDequeTestSequential(Deque<Integer> queue)
			throws Exception {
		System.out.printf("%nSequential test: %s", queue.getClass());
		System.out
				.println("\n-----------------------------------------------------");
		assertQueueIsEmpty(queue);
		queue.push(42);
		assertEquals(queue.pop(), 42);

		queue.push(42);
		queue.push(43);
		queue.push(44);
		assertEquals(queue.pop(), 44);
		assertEquals(queue.pop(), 43);
		assertEquals(queue.pop(), 42);
		assertQueueIsEmpty(queue);
		queue.push(12);
		queue.push(13);
		assertEquals(queue.steal(), 12);
		assertEquals(queue.steal(), 13);
		System.out.println("finished");
	}

	// ----------------------------------------------------------------------
	// Version A: Standard sequential quicksort using recursion

	private static void sequentialRecursive() {
		int[] arr = IntArrayUtil.randomIntArray(size);
		qsort(arr, 0, arr.length - 1);
		System.out.println(IntArrayUtil.isSorted(arr));
	}

	// Sort arr[a..b] endpoints inclusive
	private static void qsort(int[] arr, int a, int b) {
		if (a < b) {
			int i = a, j = b;
			int x = arr[(i + j) / 2];
			do {
				while (arr[i] < x)
					i++;
				while (arr[j] > x)
					j--;
				if (i <= j) {
					swap(arr, i, j);
					i++;
					j--;
				}
			} while (i <= j);
			qsort(arr, a, j);
			qsort(arr, i, b);
		}
	}

	// Swap arr[s] and arr[t]
	private static void swap(int[] arr, int s, int t) {
		int tmp = arr[s];
		arr[s] = arr[t];
		arr[t] = tmp;
	}

	// ----------------------------------------------------------------------
	// Version B: Single-queue single-thread setup; sequential quicksort using
	// queue

	private static void singleQueueSingleThread() {
		SimpleDeque<SortTask> queue = new SimpleDeque<SortTask>(100000);
		int[] arr = IntArrayUtil.randomIntArray(size);
		queue.push(new SortTask(arr, 0, arr.length - 1));
		sqstWorker(queue);
		System.out.println(IntArrayUtil.isSorted(arr));
	}

	private static void sqstWorker(Deque<SortTask> queue) {
		SortTask task;
		while (null != (task = queue.pop())) {
			final int[] arr = task.arr;
			final int a = task.a, b = task.b;
			if (a < b) {
				int i = a, j = b;
				int x = arr[(i + j) / 2];
				do {
					while (arr[i] < x)
						i++;
					while (arr[j] > x)
						j--;
					if (i <= j) {
						swap(arr, i, j);
						i++;
						j--;
					}
				} while (i <= j);
				queue.push(new SortTask(arr, a, j));
				queue.push(new SortTask(arr, i, b));
			}
		}
	}

	// ----------------------------------------------------------------------
	// Version C: Single-queue multi-thread setup

	private static void singleQueueMultiThread(final int threadCount) {
		int[] arr = IntArrayUtil.randomIntArray(size);
		// IntArrayUtil.printout(arr, arr.length);
		final Deque<SortTask> queue = new SimpleDeque<SortTask>(100_000);
		queue.push(new SortTask(arr, 0, arr.length - 1));
		sqmtWorkers(queue, threadCount);
		System.out.println(IntArrayUtil.isSorted(arr));
		// IntArrayUtil.printout(arr, arr.length);
	}

	private static void sqmtWorkers(Deque<SortTask> queue, int threadCount) {
		final Thread[] threads = new Thread[threadCount];
		final LongAdder ongoing = new LongAdder();

		final CyclicBarrier barrier = new CyclicBarrier(threadCount + 1);

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						barrier.await(); // ready to start
					} catch (InterruptedException | BrokenBarrierException e) {
						e.printStackTrace();
					}
					SortTask task;
					while (null != (task = getTask(queue, ongoing))) {
						final int[] arr = task.arr;
						final int a = task.a, b = task.b;
						if (a < b) {
							int i = a, j = b;
							int x = arr[(i + j) / 2];
							do {
								while (arr[i] < x)
									i++;
								while (arr[j] > x)
									j--;
								if (i <= j) {
									swap(arr, i, j);
									i++;
									j--;
								}
							} while (i <= j);
							queue.push(new SortTask(arr, a, j));
							ongoing.increment();
							queue.push(new SortTask(arr, i, b));
							ongoing.increment();
						}
						ongoing.decrement();
					}
					try {
						barrier.await(); // finished
					} catch (InterruptedException | BrokenBarrierException e) {
						e.printStackTrace();
					}
				}
			});
		}
		ongoing.increment(); // initial task
		// start all the worker threads
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		Timer time = null;
		try {
			barrier.await(); // on main thread
			time = new Timer();

			barrier.await(); // on main thread
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		System.out.println(threadCount + "\t\t\t" + time.check());
		// wait for all threads to finish
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Tries to get a sorting task. If task queue is empty but some
	// tasks are not yet processed, yield and then try again.

	private static SortTask getTask(final Deque<SortTask> queue,
			LongAdder ongoing) {
		SortTask task;
		while (null == (task = queue.pop())) {
			if (ongoing.longValue() > 0)
				Thread.yield();
			else
				return null;
		}
		return task;
	}

	// ----------------------------------------------------------------------
	// Version D: Multi-queue multi-thread setup, thread-local queues

	private static void multiQueueMultiThread(final int threadCount) {
		// int[] arr = IntArrayUtil.randomIntArray(size);
		int[] arr = IntArrayUtil.randomIntArray(15);
		IntArrayUtil.printout(arr, arr.length);
		SimpleDeque<SortTask>[] queues = new SimpleDeque[threadCount];
		for (int i = 0; i < queues.length; i++) {
			queues[i] = new SimpleDeque(100_000);
		}
		queues[0].push(new SortTask(arr, 0, arr.length - 1));
		mqmtWorkers(queues, threadCount);

		System.out.println(IntArrayUtil.isSorted(arr));
		IntArrayUtil.printout(arr, arr.length);
	}

	// Version E: Multi-queue multi-thread setup, thread-local queues
	private static void multiQueueMultiThreadCL(final int threadCount) {
		int[] arr = IntArrayUtil.randomIntArray(size);
//		IntArrayUtil.printout(arr, arr.length);
		ChaseLevDeque<SortTask>[] queues = new ChaseLevDeque[threadCount];
		for (int i = 0; i < queues.length; i++) {
			queues[i] = new ChaseLevDeque(100_000);
		}
		queues[0].push(new SortTask(arr, 0, arr.length - 1));
		mqmtWorkers(queues, threadCount);

//		System.out.println(IntArrayUtil.isSorted(arr));
//		IntArrayUtil.printout(arr, arr.length);
	}

	private static void mqmtWorkers(Deque<SortTask>[] queues, int threadCount) {
		// To do: ... create and start threads and so on ...
		Thread[] threads = new Thread[threadCount];
		final LongAdder ongoing = new LongAdder();

		final CyclicBarrier barrier = new CyclicBarrier(threadCount + 1);

		for (int i = 0; i < threads.length; i++) {
			final int t = i;
			threads[t] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						barrier.await(); // ready to start
					} catch (InterruptedException | BrokenBarrierException e) {
						e.printStackTrace();
					}
					SortTask task;
					while (null != (task = getTask(t, queues, ongoing))) {
						final int[] arr = task.arr;
						final int a = task.a, b = task.b;
						if (a < b) {
							int i = a, j = b;
							int x = arr[(i + j) / 2];
							do {
								while (arr[i] < x)
									i++;
								while (arr[j] > x)
									j--;
								if (i <= j) {
									swap(arr, i, j);
									i++;
									j--;
								}
							} while (i <= j);
							queues[t].push(new SortTask(arr, a, j));
							ongoing.increment();
							queues[t].push(new SortTask(arr, i, b));
							ongoing.increment();
						}
						ongoing.decrement();
					}
					try {
						barrier.await(); // finished
					} catch (InterruptedException | BrokenBarrierException e) {
						e.printStackTrace();
					}
				}
			});
		}
		ongoing.increment(); // initial task
		// start all the worker threads
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		Timer time = null;
		try {
			barrier.await(); // on main thread
			time = new Timer();

			barrier.await(); // on main thread
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		System.out.println(threadCount + "\t" + time.check());
		// wait for all threads to finish
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Tries to get a sorting task. If task queue is empty, repeatedly
	// try to steal, cyclically, from other threads and if that fails,
	// yield and then try again, while some sort tasks are not processed.

	private static SortTask getTask(final int myNumber,
			final Deque<SortTask>[] queues, LongAdder ongoing) {
		final int threadCount = queues.length;
		SortTask task = queues[myNumber].pop();
		if (null != task)
			return task;
		else {
			do {
				// To do here: ... try to steal from other tasks' queues ...
				for (int i = 0; i < queues.length; i++) {
					task = queues[i].steal();
					if (task != null) {
						return task;
					}
				}
				Thread.yield();
			} while (ongoing.longValue() > 0);
			return null;
		}
	}

}

// ----------------------------------------------------------------------
// SortTask class, Deque<T> interface, SimpleDeque<T>

// Represents the task of sorting arr[a..b]
class SortTask {
	public final int[] arr;
	public final int a, b;

	public SortTask(int[] arr, int a, int b) {
		this.arr = arr;
		this.a = a;
		this.b = b;
	}
}

interface Deque<T> {
	void push(T item); // at bottom

	T pop(); // from bottom

	T steal(); // from top
}

class SimpleDeque<T> implements Deque<T> {
	// The queue's items are in items[top%S...(bottom-1)%S], where S ==
	// items.length; items[bottom%S] is where the next push will happen;
	// items[(bottom-1)%S] is where the next pop will happen;
	// items[top%S] is where the next steal will happen; the queue is
	// empty if top == bottom, non-empty if top < bottom, and full if
	// bottom - top == items.length. The top field can only increase.
	@GuardedBy("this")
	private long bottom = 0, top = 0;
	@GuardedBy("this")
	private final T[] items;

	public SimpleDeque(int size) {
		this.items = makeArray(size);
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] makeArray(int size) {
		// Java's @$#@?!! type system requires this unsafe cast
		return (T[]) new Object[size];
	}

	private static int index(long i, int n) {
		return (int) (i % (long) n);
	}

	public synchronized void push(T item) { // at bottom
		final long size = bottom - top;
		if (size == items.length)
			throw new RuntimeException("queue overflow");
		items[index(bottom++, items.length)] = item;
	}

	public synchronized T pop() { // from bottom
		final long afterSize = bottom - 1 - top;
		if (afterSize < 0)
			return null;
		else
			return items[index(--bottom, items.length)];
	}

	public synchronized T steal() { // from top
		final long size = bottom - top;
		if (size <= 0)
			return null;
		else
			return items[index(top++, items.length)];
	}
}

// ----------------------------------------------------------------------

// A lock-free queue simplified from Chase and Lev: Dynamic circular
// work-stealing queue, SPAA 2005. We simplify it by not reallocating
// the array; hence this queue may overflow. This is close in spirit
// to the original ABP work-stealing queue (Arora, Blumofe, Plaxton:
// Thread scheduling for multiprogrammed multiprocessors, 2000,
// section 3) but in that paper an "age" tag needs to be added to the
// top pointer to avoid the ABA problem (see ABP section 3.3). This
// is not necessary in the Chase-Lev dequeue design, where the top
// index never assumes the same value twice.

// PSEUDOCODE for ChaseLevDeque class:

class ChaseLevDeque<T> implements Deque<T> {
	volatile long bottom = 0;
	final AtomicLong top = new AtomicLong(0);
	final T[] items;

	public ChaseLevDeque(int size) {
		this.items = makeArray(size);
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] makeArray(int size) {
		// Java's @$#@?!! type system requires this unsafe cast
		return (T[]) new Object[size];
	}

	private static int index(long i, int n) {
		return (int) (i % (long) n);
	}

	public void push(T item) { // at bottom
		final long b = bottom, t = top.get(), size = b - t;
		if (size == items.length)
			throw new RuntimeException("queue overflow");
		items[index(b, items.length)] = item;
		bottom = b + 1;
	}

	public T pop() { // from bottom
		final long b = bottom - 1;
		bottom = b;
		final long t = top.get(), afterSize = b - t;
		if (afterSize < 0) { // empty before call
			bottom = t;
			return null;
		} else {
			T result = items[index(b, items.length)];
			if (afterSize > 0) // non-empty after call
				return result;
			else { // became empty, update both top and bottom
				if (!CAS(top, t, t + 1)) // somebody stole result
					result = null;
				bottom = t + 1;
				return result;
			}
		}
	}

	public T steal() { // from top
		final long t = top.get(), b = bottom, size = b - t;
		if (size <= 0)
			return null;
		else {
			T result = items[index(t, items.length)];
			if (CAS(top, t, t + 1))
				return result;
			else
				return null;
		}
	}

	private boolean CAS(AtomicLong top, long expect, long update) {
		return top.compareAndSet(expect, update);
	}
}

// ----------------------------------------------------------------------

class IntArrayUtil {
	public static int[] randomIntArray(final int n) {
		int[] arr = new int[n];
		for (int i = 0; i < n; i++)
			arr[i] = (int) (Math.random() * n * 2);
		return arr;
	}

	public static void printout(final int[] arr, final int n) {
		for (int i = 0; i < n; i++)
			System.out.print(arr[i] + " ");
		System.out.println("");
	}

	public static boolean isSorted(final int[] arr) {
		for (int i = 1; i < arr.length; i++)
			if (arr[i - 1] > arr[i])
				return false;
		return true;
	}
}

class Tests {
	public static <T> void assertEquals(T x, T y) throws Exception {
		if (!x.equals(y))
			throw new Exception(String.format("ERROR: %d not equal to %d%n", x,
					y));
	}

	public static void assertTrue(boolean b) throws Exception {
		if (!b)
			throw new Exception(String.format("ERROR: assertTrue"));
	}

	public static <T> void assertQueueIsEmpty(Deque<T> queue)
			throws AssertionError {
		try {
			// try to make it fail because queue.pop() should return null
			queue.pop().getClass();
			throw new AssertionError("FAIL - queue not empty");
		} catch (Exception e) {
		}
	}
}

/**
 * Producer-consumer test program for BoundedQueue
 *
 * @author Brian Goetz and Tim Peierls; some modifications by sestoft@itu.dk;
 *         further modifications by kklo@itu.dk
 */

class PutTakeTest extends Tests {
	protected CyclicBarrier barrier;
	protected final Deque<Integer> queue;
	protected int nTrials;
	protected final int nPairs;
	protected final AtomicInteger putSum = new AtomicInteger(0);
	protected final AtomicInteger takeSum = new AtomicInteger(0);
	protected final AtomicInteger stealSum = new AtomicInteger(0);

	public PutTakeTest(Deque<Integer> queue, int npairs, int ntrials) {
		this.queue = queue;
		this.nTrials = ntrials;
		this.nPairs = npairs;
		// this.barrier = new CyclicBarrier((npairs * 2) + 1);
		this.barrier = new CyclicBarrier((npairs) + 1);
		while ((nTrials % nPairs) != 0) {
			System.out.println("add " + 1);
			nTrials += 1;
		}
	}

	void test() {
		System.out.printf("%nConcurrent test: %s\n", queue.getClass());
		Thread[] threads = new Thread[(nPairs * 2)];
		try {
			// create threads with producers and consumers
			for (int i = 0; i < nPairs; i++) {
				threads[i] = new Thread(new Producer());
				threads[i].start();
				threads[i + nPairs] = new Thread(new Consumer());
				threads[i + nPairs].start();
			}
			barrier.await(); // start work
			barrier.await(); // wait to finish

			assertEquals(putSum.get(), takeSum.get());
			System.out.println("put: " + putSum.get() + "\ntake:"
					+ takeSum.get());
			assertQueueIsEmpty(queue);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void test2() {
		System.out.printf("%nConcurrent test: %s\n", queue.getClass());
		Thread[] threads = new Thread[nPairs];
		try {
			// create threads with producers and consumers
			threads[0] = new Thread(new PutPopper());
			threads[0].start();
			for (int i = 1; i < nPairs; i++) {
				threads[i] = new Thread(new StealConsumer());
				threads[i].start();
			}
			barrier.await(); // start work
			barrier.await(); // wait to finish

			assertEquals(putSum.get(), takeSum.get() + stealSum.get());
			System.out.println("put:\t\t" + putSum.get() + "\ntakeANDsteal:\t"
					+ (takeSum.get() + stealSum.get()));
			assertQueueIsEmpty(queue);
			System.out
					.println("-----------------------------------------------------");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	class Producer implements Runnable {
		public void run() {
			try {
				ThreadLocalRandom random = ThreadLocalRandom.current();
				int sum = 0;
				barrier.await();
				for (int i = nTrials; i > 0; --i) {
					int item = random.nextInt();
					queue.push(item);
					sum += item;
				}
				putSum.getAndAdd(sum);
				barrier.await();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	class Consumer implements Runnable {
		public void run() {
			try {
				barrier.await();
				int sum = 0;
				int nPops = 0;
				boolean steal = false;
				Integer r;
				while (nPops != nTrials) { // A1
					r = steal ? queue.steal() : queue.pop();
					if (r != null) {
						sum += r;
						nPops++;
						steal = !steal;
					}
				}
				takeSum.getAndAdd(sum);
				barrier.await();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	class PutPopper implements Runnable {
		public void run() {
			try {
				ThreadLocalRandom random = ThreadLocalRandom.current();
				int popSum = 0;
				int pushSum = 0;
				int nPops = 0;
				barrier.await();
				for (int i = 0; i < nTrials; i++) {
					// push
					int item = random.nextInt();
					queue.push(item);
					pushSum += item;

					// pop
					if (nPops != (nTrials / nPairs)) {
						Integer r = queue.pop();
						if (r != null) {
							popSum += r;
							nPops++;
						}
					}
				}
				putSum.getAndAdd(pushSum);
				takeSum.getAndAdd(popSum);
				barrier.await();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	class StealConsumer implements Runnable {
		public void run() {
			try {
				int sum = 0;
				int nSteals = 0;
				barrier.await();
				while (nSteals != (nTrials / nPairs)) {
					Integer r = queue.steal();
					if (r != null) {
						sum += r;
						nSteals++;
					}
				}
				stealSum.getAndAdd(sum);
				barrier.await();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
