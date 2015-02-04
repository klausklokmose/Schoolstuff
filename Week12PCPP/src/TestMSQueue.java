// For week 12
// sestoft@itu.dk * 2014-11-16

// Unbounded list-based lock-free queue by Michael and Scott 1996 (who
// call it non-blocking).

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class TestMSQueue extends MyTests {
	public static void main(String[] args) {
		// try {
		// sequentialTest(new MSQueue<Integer>());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// Mark7("TestConcurrentMSQueue", new IntToDouble() { public double
		// call(int i) {
		// try {
		// return concurrentTest(new MSQueue<Integer>());
		// } catch (Exception e) {
		// e.printStackTrace();
		// return 0;
		// }
		// }
		// });
		System.out.println("MSQueue");
		for (int i = 1; i < 6; i++) {
			testModerateContention(new MSQueue<Integer>(), i);
		}
		System.out.println("MSQueueRefl");
		for (int i = 1; i < 6; i++) {
			testModerateContention(new MSQueueRefl<Integer>(), i);
		}
		System.out.println("LockBasedNonBlockingQueue");
		for (int i = 1; i < 6; i++) {
			testModerateContention(new LockBasedNonBlockingQueue<Integer>(), i);
		}

	}

	private static void testModerateContention(UnboundedQueue<Integer> bq, int N) {
		final ExecutorService pool = Executors.newCachedThreadPool();
		CyclicBarrier barrier = new CyclicBarrier(2 * N + 1);
		try {
			for (int i = 0; i < N; i++) {
				PrimeProducer pp = new PrimeProducer(bq, barrier);
				PrimeConsumer pc = new PrimeConsumer(bq, barrier);
				pool.execute(pp);
				pool.execute(pc);
			}
			Timer t = new Timer();
			barrier.await(); // wait for all threads to be ready
			barrier.await(); // wait for all threads to finish
			System.out.println("N=" + N + "\ttime lapsed: " + t.check());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		pool.shutdown();
	}

	private static void sequentialTest(MSQueue<Integer> bq) throws Exception {
		System.out.printf("%nSequential test: %s", bq.getClass());
		assertTrue(bq.isEmpty());
		// assertTrue(!bq.isFull());
		bq.enqueue(7);
		bq.enqueue(9);
		bq.enqueue(13);
		assertTrue(!bq.isEmpty());
		// assertTrue(bq.isFull());
		assertEquals(bq.dequeue(), 7);
		assertEquals(bq.dequeue(), 9);
		assertEquals(bq.dequeue(), 13);
		assertTrue(bq.isEmpty());
		// assertTrue(!bq.isFull());
		System.out.println("... passed");
	}

	private static int concurrentTest(UnboundedQueue<Integer> bq)
			throws Exception {
		// System.out.printf("%nParallel test: %s", bq.getClass());
		// System.out.println();
		final ExecutorService pool = Executors.newCachedThreadPool();
		int i = new PutTakeTest(bq, 17, 100_000).test(pool);
		pool.shutdown();
		return i;
		// System.out.println("... passed");
	}

	public static double Mark7(String msg, IntToDouble f) {
		int n = 10, count = 1, totalCount = 0;
		double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
		do {
			count *= 2;
			st = sst = 0.0;
			for (int j = 0; j < n; j++) {
				Timer t = new Timer();
				for (int i = 0; i < count; i++)
					dummy += f.call(i);
				runningTime = t.check();
				double time = runningTime * 1e9 / count; // nanoseconds
				st += time;
				sst += time * time;
				totalCount += count;
			}
		} while (runningTime < 1 && count < Integer.MAX_VALUE / 2);
		double mean = st / n, sdev = Math.sqrt(sst / n - mean * mean);
		System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev,
				count);
		return dummy / totalCount;
	}
}

class PrimeProducer implements Runnable {
	private CyclicBarrier barrier;
	private UnboundedQueue<Integer> bq;

	public PrimeProducer(UnboundedQueue<Integer> bq, CyclicBarrier barrier) {
		this.barrier = barrier;
		this.bq = bq;
	}

	public void run() {
		try {
			barrier.await();
			int i = getAPrimeNumberInRange(100_000, 200_000);
			if (i != -1) {
				bq.enqueue(i);
			}
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	private static int getAPrimeNumberInRange(int from, int to) {
		for (int i = from; i < to; i++)
			if (isPrime(i))
				return i;
		return -1;
	}

	private static boolean isPrime(int n) {
		int k = 2;
		while (k * k <= n && n % k != 0)
			k++;
		return n >= 2 && k * k > n;
	}
}

class PrimeConsumer implements Runnable {
	private CyclicBarrier barrier;
	private UnboundedQueue<Integer> bq;

	public PrimeConsumer(UnboundedQueue<Integer> bq, CyclicBarrier barrier) {
		this.barrier = barrier;
		this.bq = bq;
	}

	public void run() {
		try {
			barrier.await();

			while (true) {
				Integer k = bq.dequeue();
				if (k != null && isPrime(k))
					break;
			}
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	private static boolean isPrime(int n) {
		int k = 2;
		while (k * k <= n && n % k != 0)
			k++;
		return n >= 2 && k * k > n;
	}
}

/**
 * Producer-consumer test program for BoundedQueue
 *
 * @author Brian Goetz and Tim Peierls; some modifications by sestoft@itu.dk
 */

class PutTakeTest extends MyTests {
	protected CyclicBarrier barrier;
	protected final UnboundedQueue<Integer> bq;
	protected final int nTrials, nPairs;
	protected final AtomicInteger putSum = new AtomicInteger(0);
	protected final AtomicInteger takeSum = new AtomicInteger(0);

	public PutTakeTest(UnboundedQueue<Integer> bq2, int npairs, int ntrials) {
		this.bq = bq2;
		this.nTrials = ntrials;
		this.nPairs = npairs;
		this.barrier = new CyclicBarrier(npairs * 2 + 1);
	}

	int test(ExecutorService pool) {
		try {
			for (int i = 0; i < nPairs; i++) {
				pool.execute(new Producer());
				pool.execute(new Consumer());
			}
			barrier.await(); // wait for all threads to be ready
			barrier.await(); // wait for all threads to finish
			assertTrue(bq.isEmpty());
			assertEquals(putSum.get(), takeSum.get());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return putSum.get();
	}

	class Producer implements Runnable {
		public void run() {
			try {
				Random random = new Random();
				int sum = 0;
				barrier.await();
				for (int i = nTrials; i > 0; --i) {
					int item = random.nextInt();
					bq.enqueue(item);
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
				int succes = 0;
				while (succes != nTrials) {
					Integer s = bq.dequeue();
					if (s != null) {
						sum += s;
						succes++;
					}
				}
				takeSum.getAndAdd(sum);
				barrier.await();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}

class MyTests {
	public static void assertEquals(int x, int y) throws Exception {
		if (x != y)
			throw new Exception(String.format("ERROR: %d not equal to %d%n", x,
					y));
	}

	public static void assertTrue(boolean b) throws Exception {
		if (!b)
			throw new Exception(String.format("ERROR: assertTrue"));
	}
}

class LockBasedNonBlockingQueue<T> implements UnboundedQueue<T> {
	private final AtomicReference<Node<T>> head, tail;
	private final AtomicReferenceFieldUpdater<Node<T>, Node<T>> nextUpdater = AtomicReferenceFieldUpdater
			.newUpdater((Class<Node<T>>) (Class<?>) (Node.class),
					(Class<Node<T>>) (Class<?>) (Node.class), "next");

	public LockBasedNonBlockingQueue() {
		Node<T> dummy = new Node<T>(null, null);
		head = new AtomicReference<Node<T>>(dummy);
		tail = new AtomicReference<Node<T>>(dummy);
	}

	public boolean isEmpty() {
		return head.get().equals(tail.get());
	}

	public synchronized void enqueue(T item) { // at tail
		Node<T> node = new Node<T>(item, null);
		while (true) {
			Node<T> last = tail.get(), next = last.next;
			if (last == tail.get()) { // E7
				if (next == null) {
					// In quiescent state, try inserting new node
					if (nextUpdater.compareAndSet(last, next, node)) { // E9
						// Insertion succeeded, try advancing tail
						tail.compareAndSet(last, node);
						return;
					}
				} else
					// Queue in intermediate state, advance tail
					tail.compareAndSet(last, next);
			}
		}
	}

	public synchronized T dequeue() { // from head
		while (true) {
			Node<T> first = head.get(), last = tail.get(), next = first.next; // D3
			if (first == head.get()) { // D5
				if (first == last) {
					if (next == null)
						return null;
					else
						tail.compareAndSet(last, next);
				} else {
					T result = next.item;
					if (head.compareAndSet(first, next)) // D13
						return result;
				}
			}
		}
	}

	private static class Node<T> {
		final T item;
		volatile Node<T> next;

		public Node(T item, Node<T> next) {
			this.item = item;
			this.next = next;
		}
	}
}

class MSQueue<T> implements UnboundedQueue<T> {
	private final AtomicReference<Node<T>> head, tail;

	public MSQueue() {
		Node<T> dummy = new Node<T>(null, null);
		head = new AtomicReference<Node<T>>(dummy);
		tail = new AtomicReference<Node<T>>(dummy);
	}

	public boolean isEmpty() {
		return head.get().equals(tail.get());
	}

	public void enqueue(T item) { // at tail
		Node<T> node = new Node<T>(item, null);
		while (true) {
			Node<T> last = tail.get(), next = last.next.get();
//			if (last == tail.get()) { // E7
				if (next == null) {
					// In quiescent state, try inserting new node
					if (last.next.compareAndSet(next, node)) { // E9
						// Insertion succeeded, try advancing tail
						tail.compareAndSet(last, node);
						return;
					}
				} else
					// Queue in intermediate state, advance tail
					tail.compareAndSet(last, next);
//			}
		}
	}

	public T dequeue() { // from head
		while (true) {
			Node<T> first = head.get(), last = tail.get(), next = first.next
					.get(); // D3
//			if (first == head.get()) { // D5
				if (first == last) {
					if (next == null)
						return null;
					else
						tail.compareAndSet(last, next);
				} else {
					T result = next.item;
					if (head.compareAndSet(first, next)) // D13
						return result;
				}
//			}
		}
	}

	private static class Node<T> {
		final T item;
		final AtomicReference<Node<T>> next;

		public Node(T item, Node<T> next) {
			this.item = item;
			this.next = new AtomicReference<Node<T>>(next);
		}
	}
}

class MSQueueRefl<T> implements UnboundedQueue<T> {
	private final AtomicReference<Node<T>> head, tail;
	private final AtomicReferenceFieldUpdater<Node<T>, Node<T>> nextUpdater = AtomicReferenceFieldUpdater
			.newUpdater((Class<Node<T>>) (Class<?>) (Node.class),
					(Class<Node<T>>) (Class<?>) (Node.class), "next");

	public MSQueueRefl() {
		Node<T> dummy = new Node<T>(null, null);
		head = new AtomicReference<Node<T>>(dummy);
		tail = new AtomicReference<Node<T>>(dummy);
	}

	public boolean isEmpty() {
		return head.get().equals(tail.get());
	}

	public void enqueue(T item) { // at tail
		Node<T> node = new Node<T>(item, null);
		while (true) {
			Node<T> last = tail.get(), next = last.next;
			if (last == tail.get()) { // E7
				if (next == null) {
					// In quiescent state, try inserting new node
					if (nextUpdater.compareAndSet(last, next, node)) { // E9
						// Insertion succeeded, try advancing tail
						tail.compareAndSet(last, node);
						return;
					}
				} else
					// Queue in intermediate state, advance tail
					tail.compareAndSet(last, next);
			}
		}
	}

	public T dequeue() { // from head
		while (true) {
			Node<T> first = head.get(), last = tail.get(), next = first.next; // D3
			if (first == head.get()) { // D5
				if (first == last) {
					if (next == null)
						return null;
					else
						tail.compareAndSet(last, next);
				} else {
					T result = next.item;
					if (head.compareAndSet(first, next)) // D13
						return result;
				}
			}
		}
	}

	private static class Node<T> {
		final T item;
		volatile Node<T> next;

		public Node(T item, Node<T> next) {
			this.item = item;
			this.next = next;
		}
	}

	// ------------------------------------------------------------
	// Unbounded lock-free queue (non-blocking in M&S terminology),
	// using CAS and AtomicReference

	// This creates one AtomicReference object for each Node object. The
	// next MSQueueRefl class further below uses one-time reflection to
	// create an AtomicReferenceFieldUpdater, thereby avoiding this extra
	// object. In practice the overhead of the extra object apparently
	// does not matter much.

}

interface UnboundedQueue<T> {
	void enqueue(T item);

	boolean isEmpty();

	T dequeue();

	// ------------------------------------------------------------
	// Unbounded lock-based queue with sentinel (dummy) node

	class LockingQueue<T> implements UnboundedQueue<T> {
		// Invariants:
		// The node referred by tail is reachable from head.
		// If non-empty then head != tail,
		// and tail points to last item, and head.next to first item.
		// If empty then head == tail.

		private static class Node<T> {
			final T item;
			Node<T> next;

			public Node(T item, Node<T> next) {
				this.item = item;
				this.next = next;
			}
		}

		private Node<T> head, tail;

		public LockingQueue() {
			head = tail = new Node<T>(null, null);
		}

		public synchronized void enqueue(T item) { // at tail
			Node<T> node = new Node<T>(item, null);
			tail.next = node;
			tail = node;
		}

		public synchronized T dequeue() { // from head
			if (head.next == null)
				return null;
			Node<T> first = head;
			head = first.next;
			return head.item;
		}

		@Override
		public boolean isEmpty() {
			throw new RuntimeException(
					"These aren't the droids we're looking for");
		}
	}
}
