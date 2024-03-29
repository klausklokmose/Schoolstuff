// For week 10
// sestoft@itu.dk * 2014-11-05

// Compile and run like this:
//   javac -cp ~/lib/multiverse-core-0.7.0.jar TestStmQueues.java 
//   java -cp ~/lib/multiverse-core-0.7.0.jar:. TestStmQueues

// The Multiverse library:
import static org.multiverse.api.StmUtils.atomic;
import static org.multiverse.api.StmUtils.newTxnInteger;
import static org.multiverse.api.StmUtils.retry;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.multiverse.api.StmUtils;
import org.multiverse.api.references.TxnInteger;
import org.multiverse.api.references.TxnRef;



public class TestStmQueues extends Tests {
	private static CyclicBarrier barrier;

	public static void main(String[] args) throws Exception {
		StmBoundedQueue<Integer> bq1 = new StmBoundedQueue<Integer>(3), bq2 = new StmBoundedQueue<Integer>(
				3);
		// System.out.println(bq1.take()); // The retry() is NOT a busy wait!
		bq2.put(7);
		bq2.put(9);
		bq2.put(13);
		transferFromTo(bq2, bq1);
		System.out.println(bq1.take());
		bq1.put(17);
		System.out.println(takeOne(bq1, bq2));
//		sequentialTest(new StmBoundedQueue<Integer>(3));
		timeSequentialTest(new StmBoundedQueue(3));
	}

	private static void timeSequentialTest(BoundedQueue<Integer> bq){
		barrier = new CyclicBarrier(2);
//		final ExecutorService pool = Executors.newCachedThreadPool();
		try {
			barrier.await();
			Timer t = new Timer();
			sequentialTest(bq);
			barrier.await();
			System.out.println(t.check()+" ns");
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void sequentialTest(BoundedQueue<Integer> bq)
			throws Exception {
		barrier.await();
		System.out.printf("%nSequential test: %s", bq.getClass());
		assertTrue(bq.isEmpty());
		assertTrue(!bq.isFull());
		bq.put(7);
		bq.put(9);
		bq.put(13);
		assertTrue(!bq.isEmpty());
		assertTrue(bq.isFull());
		assertEquals(bq.take(), 7);
		assertEquals(bq.take(), 9);
		assertEquals(bq.take(), 13);
		assertTrue(bq.isEmpty());
		assertTrue(!bq.isFull());
		barrier.await();
		System.out.println("... passed");
	}

	// Herlihy & Shavit Fig 18.7
	private static <T> void transferFromTo(BoundedQueue<T> from,
			BoundedQueue<T> to) {
		atomic(new Runnable() {
			public void run() {
				T item = from.take();
				to.put(item);
			}
		});
	}

	// Herlihy & Shavit Fig 18.8, using a home-made orElse construct,
	// but basically as defined in Multiverse class GammaOrElseBlock
	// (which I cannot get to work, though):
	private static <T> T takeOne(BoundedQueue<T> bq1, BoundedQueue<T> bq2)
			throws Exception {
		return myOrElse(new Callable<T>() {
			public T call() {
				return bq1.take();
			}
		}, new Callable<T>() {
			public T call() {
				return bq2.take();
			}
		});
	}

	public static <T> T myOrElse(Callable<T> either, Callable<T> orelse)
			throws Exception {
		return atomic(new Callable<T>() {
			public T call() throws Exception {
				try {
					return either.call();
				} catch (org.multiverse.api.exceptions.RetryError retry) {
					return orelse.call();
				}
			}
		});
	}
}

class Tests {
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

interface BoundedQueue<E> {
	boolean isEmpty();

	boolean isFull();

	void put(E item);

	E take();
}

class StmBoundedQueue<T> implements BoundedQueue<T> {
	private final TxnInteger availableItems, availableSpaces;
	private final TxnRef<T>[] items;
	private final TxnInteger head, tail;

	public StmBoundedQueue(int capacity) {
		this.availableItems = newTxnInteger(0);
		this.availableSpaces = newTxnInteger(capacity);
		this.items = makeArray(capacity);
		for (int i = 0; i < capacity; i++)
			this.items[i] = StmUtils.<T> newTxnRef();
		this.head = newTxnInteger(0);
		this.tail = newTxnInteger(0);
	}

	@SuppressWarnings("unchecked")
	private static <T> TxnRef<T>[] makeArray(int capacity) {
		// Java's @$#@?!! type system requires this unsafe cast
		return (TxnRef<T>[]) new TxnRef[capacity];
	}

	public boolean isEmpty() {
		return atomic(new Callable<Boolean>() {
			public Boolean call() {
				return availableItems.get() == 0;
			}
		});
	}

	public boolean isFull() {
		return atomic(new Callable<Boolean>() {
			public Boolean call() {
				return availableSpaces.get() == 0;
			}
		});
	}

	public void put(T item) { // at tail
		atomic(new Runnable() {
			public void run() {
				if (availableSpaces.get() == 0)
					retry();
				else {
					availableSpaces.decrement();
					items[tail.get()].set(item);
					tail.set((tail.get() + 1) % items.length);
					availableItems.increment();
				}
			}
		});
	}

	public T take() { // from head
		return atomic(new Callable<T>() {
			public T call() {
				if (availableItems.get() == 0) {
					retry();
					return null; // unreachable
				} else {
					availableItems.decrement();
					T item = items[head.get()].get();
					items[head.get()].set(null);
					head.set((head.get() + 1) % items.length);
					availableSpaces.increment();
					return item;
				}
			}
		});
	}
}
