// For week 10 -- this version includes solutions to exercises
// sestoft@itu.dk * 2014-11-05

// Compile and run like this:
//   javac -cp ~/lib/multiverse-core-0.7.0.jar IntToDouble.java Timer.java TestStmMap.java
//   java -cp ~/lib/multiverse-core-0.7.0.jar:. TestStmMap

// For the Multiverse library:
import static org.multiverse.api.StmUtils.atomic;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.multiverse.api.StmUtils;
import org.multiverse.api.references.TxnInteger;
import org.multiverse.api.references.TxnRef;

public class TestStmMap {
	public static void main(String[] args) {
		SystemInfo();
		testAllMaps();
		exerciseAllMaps();
	}

	private static double exerciseMap(int threadCount, int perThread,
			int range, final OurMap<Integer, String> map) {
		Thread[] threads = new Thread[threadCount];
		for (int t = 0; t < threadCount; t++) {
			final int myThread = t;
			threads[t] = new Thread(new Runnable() {
				public void run() {
					Random random = new Random(37 * myThread + 78);
					for (int i = 0; i < perThread; i++) {
						Integer key = random.nextInt(range);
						if (!map.containsKey(key)) {
							// Add key with probability 60%
							if (random.nextDouble() < 0.60)
								map.put(key, Integer.toString(key));
						} else // Remove key with probability 2% and reinsert
						if (random.nextDouble() < 0.02) {
							map.remove(key);
							map.putIfAbsent(key, Integer.toString(key));
						}
					}
					final AtomicInteger ai = new AtomicInteger();
					map.forEach(new Consumer<Integer, String>() {
						public void accept(Integer k, String v) {
							ai.getAndIncrement();
						}
					});
					// System.out.println(ai.intValue() + " " + map.size());
				}
			});
		}
		for (int t = 0; t < threadCount; t++)
			threads[t].start();
		// map.reallocateBuckets();
		try {
			for (int t = 0; t < threadCount; t++)
				threads[t].join();
		} catch (InterruptedException exn) {
		}
		return map.size();
	}

	private static void exerciseAllMaps() {
		final int bucketCount = 100_000, threadCount = 16;
		final int iterations = 1_600_000, perThread = iterations / threadCount;
		final int range = 100_000;
		System.out.println(Mark7(
				String.format("%-21s %d", "StmMap", threadCount),
				new IntToDouble() {
					public double call(int i) {
						return exerciseMap(threadCount, perThread, range,
								new StmMap<Integer, String>(bucketCount));
					}
				}));
	}

	// Very basic sequential functional test of a hash map. You must
	// run with assertions enabled for this to work, as in
	// java -ea TestStmMap
	private static void testMap(final OurMap<Integer, String> map) {
		System.out.printf("%n%s%n", map.getClass());
		assert map.size() == 0;
		assert !map.containsKey(117);
		assert !map.containsKey(-2);
		assert map.get(117) == null;
		assert map.put(117, "A") == null;
		assert map.containsKey(117);
		assert map.get(117).equals("A");
		assert map.put(17, "B") == null;
		assert map.size() == 2;
		assert map.containsKey(17);
		assert map.get(117).equals("A");
		assert map.get(17).equals("B");
		assert map.put(117, "C").equals("A");
		assert map.containsKey(117);
		assert map.get(117).equals("C");
		assert map.size() == 2;
		map.forEach((k, v) -> System.out.printf("%10d maps to %s%n", k, v));
		assert map.remove(117).equals("C");
		assert !map.containsKey(117);
		assert map.get(117) == null;
		assert map.size() == 1;
		assert map.putIfAbsent(17, "D").equals("B");
		assert map.get(17).equals("B");
		assert map.size() == 1;
		assert map.containsKey(17);
		assert map.putIfAbsent(217, "E") == null;
		assert map.get(217).equals("E");
		assert map.size() == 2;
		assert map.containsKey(217);
		assert map.putIfAbsent(34, "F") == null;
		map.forEach((k, v) -> System.out.printf("%10d maps to %s%n", k, v));
		// map.reallocateBuckets();
		assert map.size() == 3;
		assert map.get(17).equals("B") && map.containsKey(17);
		assert map.get(217).equals("E") && map.containsKey(217);
		assert map.get(34).equals("F") && map.containsKey(34);
		map.forEach((k, v) -> System.out.printf("%10d maps to %s%n", k, v));
		// map.reallocateBuckets();
		assert map.size() == 3;
		assert map.get(17).equals("B") && map.containsKey(17);
		assert map.get(217).equals("E") && map.containsKey(217);
		assert map.get(34).equals("F") && map.containsKey(34);
		map.forEach((k, v) -> System.out.printf("%10d maps to %s%n", k, v));
	}

	private static void testAllMaps() {
		testMap(new StmMap<Integer, String>(25));
	}

	// --- Benchmarking infrastructure ---

	// NB: Modified to show microseconds instead of nanoseconds

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
				double time = runningTime * 1e6 / count; // microseconds
				st += time;
				sst += time * time;
				totalCount += count;
			}
		} while (runningTime < 0.25 && count < Integer.MAX_VALUE / 2);
		double mean = st / n, sdev = Math.sqrt(sst / n - mean * mean);
		System.out.printf("%-25s %15.1f us %10.2f %10d%n", msg, mean, sdev,
				count);
		return dummy / totalCount;
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
}

interface Consumer<K, V> {
	void accept(K k, V v);
}

interface OurMap<K, V> {
	boolean containsKey(K k);

	V get(K k);

	V put(K k, V v);

	V putIfAbsent(K k, V v);

	V remove(K k);

	int size();

	void forEach(Consumer<K, V> consumer);
	// void reallocateBuckets();
}

// ----------------------------------------------------------------------
// A hashmap that permits thread-safe concurrent operations, based on
// software transactional memory.

class StmMap<K, V> implements OurMap<K, V> {
	private final TxnRef<TxnRef<ItemNode<K, V>>[]> buckets;
	private final TxnInteger size = StmUtils.newTxnInteger();

	public StmMap(int bucketCount) {
		final TxnRef<ItemNode<K, V>>[] buckets = makeBuckets(bucketCount);
		this.buckets = StmUtils.<TxnRef<ItemNode<K, V>>[]> newTxnRef(buckets);
	}

	@SuppressWarnings("unchecked")
	private static <K, V> TxnRef<ItemNode<K, V>>[] makeBuckets(int size) {
		// Java's @$#@?!! type system requires "unsafe" cast here:
		final TxnRef<ItemNode<K, V>>[] buckets = (TxnRef<ItemNode<K, V>>[]) new TxnRef[size];
		for (int hash = 0; hash < buckets.length; hash++)
			buckets[hash] = StmUtils.<ItemNode<K, V>> newTxnRef();
		return buckets;
	}

	// Protect against poor hash functions and make non-negative
	private static <K> int getHash(K k) {
		final int kh = k.hashCode();
		return (kh ^ (kh >>> 16)) & 0x7FFFFFFF;
	}

	// Return true if key k is in map, else false
	public boolean containsKey(K k) {
		return atomic(new Callable<Boolean>() {
			public Boolean call() {
				final TxnRef<ItemNode<K, V>>[] bs = buckets.get();
				final int h = getHash(k), hash = h % bs.length;
				return ItemNode.search(bs[hash].get(), k, null);
			}
		});
	}

	// Return value v associated with key k, or null
	// TODO
	public V get(K k) {
		final TxnRef<ItemNode<K, V>>[] bs = buckets.atomicGet();
		final int h = getHash(k), hash = h % bs.length;
		Holder<V> holder = new Holder<V>();
		boolean exists = atomic(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return ItemNode.search(bs[hash].get(), k, holder);
			}
		});
		if (!exists) {
			return null;
		} else {
			return holder.get();
		}
	}
	
	public int size() {
		return size.atomicGet();
	}

	// Put v at key k, or update if already present.
	//TODO
	public V put(K k, V v) {
		return atomic(new Callable<V>() {
			@Override
			public V call() {
				final int h = getHash(k), hash = h % buckets.get().length;
				final ItemNode<K, V> bs = buckets.get()[hash].get();
				Holder<V> holder = new Holder<V>();
				ItemNode<K, V> newNode = ItemNode.delete(bs, k, holder);
				buckets.get()[hash] = StmUtils.newTxnRef(new ItemNode<K, V>(k,
						v, newNode));
				if(holder.get() == null){
					size.increment();
				}
				return holder.get();
			}
		});
	}

	// Put v at key k only if absent.
	//TODO
	public V putIfAbsent(K k, V v) {
		return atomic(new Callable<V>() {
			@Override
			public V call() {
				final int h = getHash(k), hash = h % buckets.get().length;
				final ItemNode<K, V> bs = buckets.get()[hash].get();
				Holder<V> holder = new Holder<V>();
				boolean exists = ItemNode.search(bs, k, holder);
				if (exists) {
					return holder.get();
				} else {
					buckets.get()[hash] = StmUtils.newTxnRef(new ItemNode<K, V>(k,v, bs));
					size.increment();
					return null;
				}
			}
		});
	}

	// Remove and return the value at key k if any, else return null
	//TODO
	public V remove(K k) {
		return atomic(new Callable<V>() {
			@Override
			public V call() {
				Holder<V> holder = new Holder<V>();
				final int h = getHash(k), hash = h % buckets.get().length;
				final ItemNode<K, V> bs = buckets.get()[hash].get();
				
				ItemNode<K, V> node = ItemNode.delete(bs, k, holder);
				if (holder.get() != null) {
					buckets.get()[hash] = StmUtils.newTxnRef(node);
					size.decrement();
				}
				return holder.get();
			}});
	}

	// Iterate over the hashmap's entries one bucket at a time. Since a
	// reallocate does not affect the old buckets table, and item node
	// lists are immutable, only visibility is needed, no transactions.
	// This is good, because calling a consumer inside an atomic seems
	// suspicious.
	// TODO
	public void forEach(Consumer<K, V> consumer) {
		final TxnRef<ItemNode<K, V>>[] bs = buckets.atomicGet();
		// for each stripe
		for (int i = 0; i < bs.length; i++) {
			ItemNode<K, V> node = bs[i].atomicGet();
			while (node != null) {
				consumer.accept(node.k, node.v);
				node = node.next;
			}
		}
	}

	// public void reallocateBuckets() {
	// throw new RuntimeException("Not implemented");
	// }

	static class ItemNode<K, V> {
		private final K k;
		private final V v;
		private final ItemNode<K, V> next;

		public ItemNode(K k, V v, ItemNode<K, V> next) {
			this.k = k;
			this.v = v;
			this.next = next;
		}

		// These work on immutable data only, no synchronization or transaction
		// needed.

		public static <K, V> boolean search(ItemNode<K, V> node, K k,
				Holder<V> old) {
			while (node != null)
				if (k.equals(node.k)) {
					if (old != null)
						old.set(node.v);
					return true;
				} else
					node = node.next;
			return false;
		}

		public static <K, V> ItemNode<K, V> delete(ItemNode<K, V> node, K k,
				Holder<V> old) {
			if (node == null)
				return null;
			else if (k.equals(node.k)) {
				old.set(node.v);
				return node.next;
			} else {
				final ItemNode<K, V> newNode = delete(node.next, k, old);
				if (newNode == node.next)
					return node;
				else
					return new ItemNode<K, V>(node.k, node.v, newNode);
			}
		}
	}

	// Object to hold a "by reference" parameter. For use only on a
	// single thread, so no need for "volatile" or synchronization or
	// transactions.

	static class Holder<V> {
		private V value;

		public V get() {
			return value;
		}

		public void set(V value) {
			this.value = value;
		}
	}
}
