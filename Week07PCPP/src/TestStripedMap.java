// For week 7
// sestoft@itu.dk * 2014-10-09

// The underlying.forEach call in class WrapConcurrentHashMap works
// only in Java 8; comment out if you have Java 7 or just make it do
// nothing.

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

class MapUtilizer implements Runnable {
	private CyclicBarrier barrier;
	private OurMap<Integer, String> map;
	private int[] list;
	private AtomicInteger counter;
	private int t;
	private int[] countsEntries;
	private int keysAdded;
	private int keysRemoved;

	public MapUtilizer(OurMap<Integer, String> map, CyclicBarrier barrier,
			int[] list, AtomicInteger counter, int n, int t) {
		this.barrier = barrier;
		this.list = list.clone();
		this.map = map;
		this.counter = counter;
		this.t = t;
		this.countsEntries = new int[n];
	}

	@Override
	public void run() {
		// initialize variables etc.
		int listSize = list.length;
		randomizeList();
		keysAdded = 0;
		keysRemoved = 0;
		boolean putIfAbsent = true;
		try {
			// tell the main thread that this thread is ready to start!
			barrier.await();

			for (int k = 0; k < listSize; k++) {
				if (putIfAbsent) {
					if (null == map.putIfAbsent(list[k], t + ":" + list[k])) {
						keysAdded += list[k];
						countsEntries[t]++;
					}
					putIfAbsent = !putIfAbsent;
				} else {
					String r = map.put(list[k], t + ":" + list[k]);
					if (null == r) {
						// add key to the sum of keys added
						keysAdded += list[k];
					} else {
						// subtract from count entries at u=v.split()
						int u = getU(r);
						countsEntries[u]--;
					}
					// increment the sum of entries added by this thread t
					countsEntries[t]++;
					putIfAbsent = !putIfAbsent;
				}
				int rndKey = list[(k * 1000) % listSize];
				String removed = map.remove(rndKey);
				if (removed != null) {
					keysRemoved += rndKey;
					countsEntries[getU(removed)]--;
					// assert !map.containsKey(list[(k * 1000) % listSize]);
				}

			}
			// System.out.println("sum added: "+sumAdded+
			// " sum removed: "+sumRemoved);
			// counter.addAndGet(keysAdded - keysRemoved);
			// tell the main thread that this thread has finished!
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	private int getU(String r) {
		return Integer.parseInt(r.split(":")[0]);
	}

	private void randomizeList() {
		Random rnd = new Random();
		int listSize = list.length;
		for (int i = 0; i < listSize; i++) {
			int firstI = rnd.nextInt(listSize - 1);
			int first = list[firstI];
			int secondI = rnd.nextInt(listSize - 1);
			int second = list[secondI];
			int temp = first;
			list[firstI] = second;
			list[secondI] = temp;
		}
	}

	public int[] getCounts() {
		return countsEntries;
	}

	public int getKeysAddedSubFromKeysRemoved() {
		return keysAdded - keysRemoved;
	}
}

public class TestStripedMap {
	private static int keySumInMap;

	public static void main(String[] args) {
		SystemInfo();
		// testAllMaps(); // Must be run with: java -ea TestStripedMap
		// testMap(new StripedMap<Integer, String>(25, 5));
		// exerciseAllMaps();
		// timeAllMaps();
		// System.out.println(77 % 7);
		 testMapWithNthreads(16, new SynchronizedMap<Integer, String>(77));
		 testMapWithNthreads(16, new StripedMap<Integer, String>(77, 7));
		testMapWithNthreads(16, new StripedWriteMap<Integer, String>(77, 7));
		 testMapWithNthreads(16, new WrapConcurrentHashMap<Integer, String>());
		// System.out.println("DONE");
	}

	private static void testMapWithNthreads(int n, OurMap<Integer, String> map) {
		System.out.println(map.getClass().getName()+"**************************\n");
		ExecutorService executor = Executors.newFixedThreadPool(n);
		CyclicBarrier barrier = new CyclicBarrier(n + 1);
		int capacity = 100;
		int[] list = new int[capacity];
		AtomicInteger counter = new AtomicInteger();
		for (int i = 0; i < capacity; i++) {
			list[i] = i;
		}
		try {
			ArrayList<MapUtilizer> utils = new ArrayList<MapUtilizer>();
			for (int i = 0; i < n; i++) {
				MapUtilizer m = new MapUtilizer(map, barrier, list, counter, n,
						i);
				utils.add(m);
				executor.execute(m);
			}
			barrier.await(); // wait for all threads to be ready!
			barrier.await(); // wait for all threads to finish!
			// get the sum of the remaining keys in the map.
			keySumInMap = 0;
			map.forEach((k, v) -> keySumInMap += k);
			int totalKeySumInTest = 0;
			for (int i = 0; i < n; i++) {
				totalKeySumInTest += utils.get(i)
						.getKeysAddedSubFromKeysRemoved();
			}
			System.out.println("Keys sum in map:\t" + keySumInMap);
			System.out.println("Total Key sum:  \t" + totalKeySumInTest);
			System.out.println("map size " + map.size());
			assert keySumInMap == totalKeySumInTest;
			//Assert the format of the values in the map
			map.forEach((k, v) -> assertFormat(k, v));
			
			int[] counts = new int[n];
			for (int i = 0; i < n; i++) {
				int[] cs = utils.get(i).getCounts();
				for (int j = 0; j < n; j++) {
					counts[j] += cs[j];
				}
			}
			int[] countsInMap = new int[n];
			map.forEach((k, v) -> count(countsInMap, v));
			for (int i = 0; i < n; i++) {
//				System.out
//						.println(i + "\t" + counts[i] + "==" + countsInMap[i]);
				assert counts[i] == countsInMap[i];
			}
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
			System.out.println();
		}

	}

	private static void assertFormat(int k, String v) {
		String[] s = v.split(":");
		assert s.length == 2;
		assert s[1].equals(k + "");
	}

	private static void count(int[] counts, String v) {
		String[] s = v.split(":");
		counts[Integer.parseInt(s[0])]++;
	}

	private static void timeAllMaps() {
		final int bucketCount = 100_000, lockCount = 32;
		for (int t = 1; t <= 32; t++) {
			final int threadCount = t;
			Mark7(String.format("%-21s %d", "SynchronizedMap", threadCount),
					new IntToDouble() {
						public double call(int i) {
							return timeMap(threadCount,
									new SynchronizedMap<Integer, String>(
											bucketCount));
						}
					});
			Mark7(String.format("%-21s %d", "StripedMap", threadCount),
					new IntToDouble() {
						public double call(int i) {
							return timeMap(threadCount,
									new StripedMap<Integer, String>(
											bucketCount, lockCount));
						}
					});
			Mark7(String.format("%-21s %d", "StripedWriteMap", threadCount),
					new IntToDouble() {
						public double call(int i) {
							return timeMap(threadCount,
									new StripedWriteMap<Integer, String>(
											bucketCount, lockCount));
						}
					});
			Mark7(String.format("%-21s %d", "WrapConcHashMap", threadCount),
					new IntToDouble() {
						public double call(int i) {
							return timeMap(
									threadCount,
									new WrapConcurrentHashMap<Integer, String>());
						}
					});
		}
	}

	private static double timeMap(int threadCount,
			final OurMap<Integer, String> map) {
		final int iterations = 5_000_000, perThread = iterations / threadCount;
		final int range = 200_000;
		return exerciseMap(threadCount, perThread, range, map);
	}

	private static double exerciseMap(int threadCount, final int perThread,
			final int range, final OurMap<Integer, String> map) {
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
		map.reallocateBuckets();
		try {
			for (int t = 0; t < threadCount; t++)
				threads[t].join();
		} catch (InterruptedException exn) {
		}
		return map.size();
	}

	private static void exerciseAllMaps() {
		final int bucketCount = 100_000, lockCount = 32, threadCount = 16;
		final int iterations = 1_600_000, perThread = iterations / threadCount;
		final int range = 100_000;
		System.out.println(Mark7(
				String.format("%-21s %d", "SynchronizedMap", threadCount),
				new IntToDouble() {
					public double call(int i) {
						return exerciseMap(threadCount, perThread, range,
								new SynchronizedMap<Integer, String>(
										bucketCount));
					}
				}));
		System.out.println(Mark7(
				String.format("%-21s %d", "StripedMap", threadCount),
				new IntToDouble() {
					public double call(int i) {
						return exerciseMap(threadCount, perThread, range,
								new StripedMap<Integer, String>(bucketCount,
										lockCount));
					}
				}));
		System.out.println(Mark7(
				String.format("%-21s %d", "StripedWriteMap", threadCount),
				new IntToDouble() {
					public double call(int i) {
						return exerciseMap(threadCount, perThread, range,
								new StripedWriteMap<Integer, String>(
										bucketCount, lockCount));
					}
				}));
		System.out.println(Mark7(
				String.format("%-21s %d", "WrapConcHashMap", threadCount),
				new IntToDouble() {
					public double call(int i) {
						return exerciseMap(threadCount, perThread, range,
								new WrapConcurrentHashMap<Integer, String>());
					}
				}));
	}

	// Very basic sequential functional test of a hash map. You must
	// run with assertions enabled for this to work, as in
	// java -ea TestStripedMap
	private static void testMap(final OurMap<Integer, String> map) {
		System.out.printf("%n%s%n", map.getClass());
		assert map.size() == 0;
		assert !map.containsKey(117);
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
		map.reallocateBuckets();
		assert map.size() == 3;
		assert map.get(17).equals("B") && map.containsKey(17);
		assert map.get(217).equals("E") && map.containsKey(217);
		assert map.get(34).equals("F") && map.containsKey(34);
		map.forEach((k, v) -> System.out.printf("%10d maps to %s%n", k, v));
		map.reallocateBuckets();
		assert map.size() == 3;
		assert map.get(17).equals("B") && map.containsKey(17);
		assert map.get(217).equals("E") && map.containsKey(217);
		assert map.get(34).equals("F") && map.containsKey(34);
		map.forEach((k, v) -> System.out.printf("%10d maps to %s%n", k, v));
		// TODO additions:
		String var = "K";
		map.forEach((k, v) -> map.put(k, var));
		assert map.get(17).equals(var);
		assert map.get(217).equals(var);
		assert map.get(34).equals(var);

	}

	private static void testAllMaps() {
		testMap(new SynchronizedMap<Integer, String>(25));
		testMap(new StripedMap<Integer, String>(25, 5));
		testMap(new StripedWriteMap<Integer, String>(25, 5));
		testMap(new WrapConcurrentHashMap<Integer, String>());
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

	/**
	 * return null if not replacing the value, otherwise return if replacing a
	 * value
	 * 
	 * @param k
	 * @param v
	 * @return
	 */
	V put(K k, V v);

	V putIfAbsent(K k, V v);

	/**
	 * remove return null if nothing was removed, otherwise it returns the value
	 * of the removed element
	 */
	V remove(K k);

	int size();

	void forEach(Consumer<K, V> consumer);

	void reallocateBuckets();
}

// ----------------------------------------------------------------------
// A hashmap that permits thread-safe concurrent operations, similar
// to a synchronized version of HashMap<K,V>.

class SynchronizedMap<K, V> implements OurMap<K, V> {
	// Synchronization policy:
	// buckets[hash] and cachedSize are guarded by this
	private ItemNode<K, V>[] buckets;
	private int cachedSize;

	public SynchronizedMap(int bucketCount) {
		this.buckets = makeBuckets(bucketCount);
	}

	@SuppressWarnings("unchecked")
	private static <K, V> ItemNode<K, V>[] makeBuckets(int size) {
		// Java's @$#@?!! type system requires this unsafe cast
		return (ItemNode<K, V>[]) new ItemNode[size];
	}

	private static <K> int getHash(K k) {
		return k.hashCode() & 0x7FFFFFFF; // Make non-negative
	}

	// Return true if key k is in map, else false
	public synchronized boolean containsKey(K k) {
		final int h = getHash(k), hash = h % buckets.length;
		return ItemNode.search(buckets[hash], k) != null;
	}

	// Return value v associated with key k, or null
	public synchronized V get(K k) {
		final int h = getHash(k), hash = h % buckets.length;
		ItemNode<K, V> node = ItemNode.search(buckets[hash], k);
		if (node != null)
			return node.v;
		else
			return null;
	}

	public synchronized int size() {
		return cachedSize;
	}

	// Put v at key k, or update if already present
	public synchronized V put(K k, V v) {
		final int h = getHash(k), hash = h % buckets.length;
		ItemNode<K, V> node = ItemNode.search(buckets[hash], k);
		if (node != null) {
			V old = node.v;
			node.v = v;
			return old;
		} else {
			buckets[hash] = new ItemNode<K, V>(k, v, buckets[hash]);
			cachedSize++;
			return null;
		}
	}

	// Put v at key k only if absent
	public synchronized V putIfAbsent(K k, V v) {
		final int h = getHash(k), hash = h % buckets.length;
		ItemNode<K, V> node = ItemNode.search(buckets[hash], k);
		if (node != null)
			return node.v;
		else {
			buckets[hash] = new ItemNode<K, V>(k, v, buckets[hash]);
			cachedSize++;
			return null;
		}
	}

	// Remove and return the value at key k if any, else return null
	public synchronized V remove(K k) {
		final int h = getHash(k), hash = h % buckets.length;
		ItemNode<K, V> prev = buckets[hash];
		if (prev == null)
			return null;
		else if (k.equals(prev.k)) { // Delete first ItemNode
			V old = prev.v;
			cachedSize--;
			buckets[hash] = prev.next;
			return old;
		} else { // Search later ItemNodes
			while (prev.next != null && !k.equals(prev.next.k))
				prev = prev.next;
			// Now prev.next == null || k.equals(prev.next.k)
			if (prev.next != null) { // Delete ItemNode prev.next
				V old = prev.next.v;
				cachedSize--;
				prev.next = prev.next.next;
				return old;
			} else
				return null;
		}
	}

	// Iterate over the hashmap's entries one bucket at a time
	public synchronized void forEach(Consumer<K, V> consumer) {
		for (int hash = 0; hash < buckets.length; hash++) {
			ItemNode<K, V> node = buckets[hash];
			while (node != null) {
				consumer.accept(node.k, node.v);
				node = node.next;
			}
		}
	}

	// Double bucket table size, rehash, and redistribute entries.

	public synchronized void reallocateBuckets() {
		final ItemNode<K, V>[] newBuckets = makeBuckets(2 * buckets.length);
		for (int hash = 0; hash < buckets.length; hash++) {
			ItemNode<K, V> node = buckets[hash];
			while (node != null) {
				final int newHash = getHash(node.k) % newBuckets.length;
				ItemNode<K, V> next = node.next;
				node.next = newBuckets[newHash];
				newBuckets[newHash] = node;
				node = next;
			}
		}
		buckets = newBuckets;
	}

	static class ItemNode<K, V> {
		private final K k;
		private V v;
		private ItemNode<K, V> next;

		public ItemNode(K k, V v, ItemNode<K, V> next) {
			this.k = k;
			this.v = v;
			this.next = next;
		}

		public static <K, V> ItemNode<K, V> search(ItemNode<K, V> node, K k) {
			while (node != null && !k.equals(node.k))
				node = node.next;
			return node;
		}
	}
}

// ----------------------------------------------------------------------
// A hash map that permits thread-safe concurrent operations, using
// lock striping (intrinsic locks on Objects created for the purpose).

// NOT IMPLEMENTED: get, putIfAbsent, size, remove and forEach.

// The bucketCount must be a multiple of the number lockCount of
// stripes, so that h % lockCount == (h % bucketCount) % lockCount and
// so that h % lockCount is invariant under doubling the number of
// buckets in method reallocateBuckets. Otherwise there is a risk of
// locking a stripe, only to have the relevant entry moved to a
// different stripe by an intervening call to reallocateBuckets.

class StripedMap<K, V> implements OurMap<K, V> {
	// Synchronization policy:
	// buckets[hash] is guarded by locks[hash%lockCount]
	// sizes[stripe] is guarded by locks[stripe]
	private volatile ItemNode<K, V>[] buckets;
	private final int lockCount;
	private final Object[] locks;
	private final int[] sizes;

	public StripedMap(int bucketCount, int lockCount) {
		if (bucketCount % lockCount != 0)
			throw new RuntimeException(
					"bucket count must be a multiple of stripe count");
		this.lockCount = lockCount;
		this.buckets = makeBuckets(bucketCount);
		this.locks = new Object[lockCount];
		this.sizes = new int[lockCount];
		for (int stripe = 0; stripe < lockCount; stripe++)
			this.locks[stripe] = new Object();
	}

	@SuppressWarnings("unchecked")
	private static <K, V> ItemNode<K, V>[] makeBuckets(int size) {
		// Java's @$#@?!! type system requires this unsafe cast
		return (ItemNode<K, V>[]) new ItemNode[size];
	}

	private static <K> int getHash(K k) {
		return k.hashCode() & 0x7FFFFFFF; // Make non-negative
	}

	// Return true if key k is in map, else false
	public boolean containsKey(K k) {
		final int h = getHash(k), stripe = h % lockCount;
		synchronized (locks[stripe]) {
			final int hash = h % buckets.length;
			return ItemNode.search(buckets[hash], k) != null;
		}
	}

	// Return value v associated with key k, or null
	public V get(K k) {
		// TODO: IMPLEMENT
		final int h = getHash(k), stripe = h % lockCount;
		synchronized (locks[stripe]) {
			final int hash = h % buckets.length;
			ItemNode<K, V> node = buckets[hash];
			node = ItemNode.search(node, k);
			if (node == null) {
				return null;
			} else {
				return node.v;
			}
		}
	}

	public int size() {
		// TODO: IMPLEMENT
		int result = 0;
		for (int stripe = 0; stripe < lockCount; stripe++) {
			synchronized (locks[stripe]) {
				result += sizes[stripe];
			}
		}
		return result;
	}

	// Put v at key k, or update if already present
	public V put(K k, V v) {
		final int h = getHash(k), stripe = h % lockCount;

		synchronized (locks[stripe]) {
			final int hash = h % buckets.length;
			final ItemNode<K, V> bs = buckets[hash]; // make reference copy to
														// the bucket

			final ItemNode<K, V> node = ItemNode.search(bs, k);
			if (node != null) {
				V old = node.v;
				node.v = v;
				return old;
			} else {
				buckets[hash] = new ItemNode<K, V>(k, v, bs);
				sizes[stripe]++;
				return null;
			}
		}
	}

	// Put v at key k only if absent
	public V putIfAbsent(K k, V v) {
		// TODO: IMPLEMENT
		final int h = getHash(k), stripe = h % lockCount;
		synchronized (locks[stripe]) {
			final int hash = h % buckets.length;
			ItemNode<K, V> node = ItemNode.search(buckets[hash], k);
			if (node != null)
				return node.v;
			else {
				buckets[hash] = new ItemNode<K, V>(k, v, buckets[hash]);
				sizes[stripe]++;
				return null;
			}
		}
	}

	// Remove and return the value at key k if any, else return null
	public V remove(K k) {
		// TODO: IMPLEMENT
		final int h = getHash(k), stripe = h % lockCount;
		synchronized (locks[stripe]) {
			final int hash = h % buckets.length;
			ItemNode<K, V> prev = buckets[hash];
			if (prev == null)
				return null;
			else if (k.equals(prev.k)) { // Delete first ItemNode
				V old = prev.v;
				sizes[stripe]--;
				buckets[hash] = prev.next;
				return old;
			} else { // Search later ItemNodes
				while (prev.next != null && !k.equals(prev.next.k))
					prev = prev.next;
				// Now prev.next == null || k.equals(prev.next.k)
				if (prev.next != null) { // Delete ItemNode prev.next
					V old = prev.next.v;
					sizes[stripe]--;
					prev.next = prev.next.next;
					return old;
				} else
					return null;
			}
		}
	}

	// Iterate over the hashmap's entries one stripe at a time; less locking
	public void forEach(Consumer<K, V> consumer) {
		// TODO: IMPLEMENT
		final ItemNode<K, V>[] bs = buckets; // make reference copy
		// for each stripe
		for (int stripe = 0; stripe < lockCount; stripe++) {
			// take the lock for that stripe
			synchronized (locks[stripe]) {
				// for each bucket in that stripe
				for (int i = stripe; i < bs.length; i = i + lockCount) {
					ItemNode<K, V> node = bs[i];
					while (node != null) {
						consumer.accept(node.k, node.v);
						node = node.next;
					}
				}
			}
		}

	}

	// First lock all stripes. Then double bucket table size, rehash,
	// and redistribute entries. Since the number of stripes does not
	// change, and since N = buckets.length is a multiple of lockCount,
	// a key that belongs to stripe s because (getHash(k) % N) %
	// lockCount == s will continue to belong to stripe s. Hence the
	// sizes array need not be recomputed.

	public void reallocateBuckets() {
		lockAllAndThen(new Runnable() {
			public void run() {
				final ItemNode<K, V>[] newBuckets = makeBuckets(2 * buckets.length);
				for (int hash = 0; hash < buckets.length; hash++) {
					ItemNode<K, V> node = buckets[hash];
					while (node != null) {
						final int newHash = getHash(node.k) % newBuckets.length;
						ItemNode<K, V> next = node.next;
						node.next = newBuckets[newHash];
						newBuckets[newHash] = node;
						node = next;
					}
				}
				buckets = newBuckets;
			}
		});
	}

	// Lock all stripes, perform the action, then unlock all stripes
	private void lockAllAndThen(Runnable action) {
		lockAllAndThen(0, action);
	}

	private void lockAllAndThen(int nextStripe, Runnable action) {
		if (nextStripe >= lockCount)
			action.run();
		else
			synchronized (locks[nextStripe]) {
				lockAllAndThen(nextStripe + 1, action);
			}
	}

	static class ItemNode<K, V> {
		private final K k;
		private V v;
		private ItemNode<K, V> next;

		public ItemNode(K k, V v, ItemNode<K, V> next) {
			this.k = k;
			this.v = v;
			this.next = next;
		}

		// Assumes locks[hashCode(k) % lockCount] is held by the thread
		public static <K, V> ItemNode<K, V> search(ItemNode<K, V> node, K k) {
			while (node != null && !k.equals(node.k))
				node = node.next;
			return node;
		}
	}
}

// ----------------------------------------------------------------------
// A hashmap that permits thread-safe concurrent operations, using
// lock striping (intrinsic locks on Objects created for the purpose),
// and with immutable ItemNodes, so that reads do not need to lock at
// all, only need visibility of writes, which is ensured through the
// AtomicIntegerArray called sizes.

// NOT IMPLEMENTED: get, putIfAbsent, size, remove and forEach.

// The bucketCount must be a multiple of the number lockCount of
// stripes, so that h % lockCount == (h % bucketCount) % lockCount and
// so that h % lockCount is invariant under doubling the number of
// buckets in method reallocateBuckets. Otherwise there is a risk of
// locking a stripe, only to have the relevant entry moved to a
// different stripe by an intervening call to reallocateBuckets.

class StripedWriteMap<K, V> implements OurMap<K, V> {
	// Synchronization policy: writing to
	// buckets[hash] is guarded by locks[hash % lockCount]
	// sizes[stripe] is guarded by locks[stripe]
	// Visibility of writes to reads is ensured by writes writing to
	// the stripe's size component (even if size does not change) and
	// reads reading from the stripe's size component.
	private volatile ItemNode<K, V>[] buckets;
	private final int lockCount;
	private final Object[] locks;
	private final AtomicIntegerArray sizes;

	public StripedWriteMap(int bucketCount, int lockCount) {
		if (bucketCount % lockCount != 0)
			throw new RuntimeException(
					"bucket count must be a multiple of stripe count");
		this.lockCount = lockCount;
		this.buckets = makeBuckets(bucketCount);
		this.locks = new Object[lockCount];
		this.sizes = new AtomicIntegerArray(lockCount);
		for (int stripe = 0; stripe < lockCount; stripe++)
			this.locks[stripe] = new Object();
	}

	@SuppressWarnings("unchecked")
	private static <K, V> ItemNode<K, V>[] makeBuckets(int size) {
		// Java's @$#@?!! type system requires "unsafe" cast here:
		return (ItemNode<K, V>[]) new ItemNode[size];
	}

	private static <K> int getHash(K k) {
		return k.hashCode() & 0x7FFFFFFF; // Make non-negative
	}

	// Return true if key k is in map, else false
	public boolean containsKey(K k) {
		final ItemNode<K, V>[] bs = buckets;
		final int h = getHash(k), stripe = h % lockCount, hash = h % bs.length;
		// The sizes access is necessary for visibility of bs elements
		return sizes.get(stripe) != 0 && ItemNode.search(bs[hash], k, null);
	}

	// Return value v associated with key k, or null
	public V get(K k) {
		// TODO: IMPLEMENT
		return null;
	}

	public int size() {
		// TODO: IMPLEMENT
		int result = 0;
		for (int i = 0; i < sizes.length(); i++) {
			result += sizes.get(i);
		}
		return result;
	}

	// Put v at key k, or update if already present
	public V put(K k, V v) {
		final int h = getHash(k), stripe = h % lockCount;
		synchronized (locks[stripe]) {
			final ItemNode<K, V>[] bs = buckets;
			final int hash = h % bs.length;
			final Holder<V> old = new Holder<V>();
			final ItemNode<K, V> node = bs[hash],
					newNode = ItemNode.delete(node, k, old);
			bs[hash] = new ItemNode<K, V>(k, v, newNode);
			// Write for visibility; increment if k was not already in map
			sizes.getAndAdd(stripe, newNode == node ? 1 : 0);
			return old.get();
		}
	}

	// Put v at key k only if absent
	public V putIfAbsent(K k, V v) {
		// TODO: IMPLEMENT
		final int h = getHash(k), stripe = h % lockCount;
		Holder<V> holder = new Holder<V>();
		synchronized (locks[stripe]) {
			final int hash = h % buckets.length;
			boolean exists = ItemNode.search(buckets[hash], k, holder);
			if (exists) {
				return holder.get();
			} else {
				buckets[hash] = new ItemNode<K, V>(k, v, buckets[hash]);
				sizes.incrementAndGet(stripe);
				return null;
			}
		}
	}

	// Remove and return the value at key k if any, else return null
	public V remove(K k) {
		// TODO: IMPLEMENT
		final int h = getHash(k), stripe = h % lockCount;
		Holder<V> holder = new Holder<V>();
		synchronized (locks[stripe]) {
			final int hash = h % buckets.length;
			ItemNode<K, V> node = ItemNode.delete(buckets[hash], k, holder);
			if (holder.get() != null) {
				buckets[hash] = node;
				sizes.decrementAndGet(stripe);
			}
		}
		return holder.get();
	}

	// Iterate over the hashmap's entries one stripe at a time.
	public void forEach(Consumer<K, V> consumer) {
		// TODO: IMPLEMENT

		final ItemNode<K, V>[] bs = buckets; // make reference copy
		// for each stripe
		for (int stripe = 0; stripe < lockCount; stripe++) {
			// take the lock for that stripe
			synchronized (locks[stripe]) {
				sizes.get(stripe);
				// for each bucket in that stripe
				for (int i = stripe; i < bs.length; i = i + lockCount) {
					ItemNode<K, V> node = bs[i];
					while (node != null) {
						consumer.accept(node.k, node.v);
						node = node.next;
					}
				}
			}
		}
	}

	// First lock all stripes. Then double bucket table size, rehash,
	// and redistribute entries. Since the number of stripes does not
	// change, and since buckets.length is a multiple of lockCount, a
	// key that belongs to stripe s because (getHash(k) % N) %
	// lockCount == s will continue to belong to stripe s. Hence the
	// sizes array need not be recomputed.

	public void reallocateBuckets() {
		lockAllAndThen(new Runnable() {
			public void run() {
				final ItemNode<K, V>[] bs = buckets;
				final ItemNode<K, V>[] newBuckets = makeBuckets(2 * bs.length);
				for (int hash = 0; hash < bs.length; hash++) {
					ItemNode<K, V> node = bs[hash];
					while (node != null) {
						final int newHash = getHash(node.k) % newBuckets.length;
						newBuckets[newHash] = new ItemNode<K, V>(node.k,
								node.v, newBuckets[newHash]);
						node = node.next;
					}
				}
				buckets = newBuckets; // Visibility: buckets is volatile
			}
		});
	}

	// Lock all stripes, perform action, then unlock all stripes
	private void lockAllAndThen(Runnable action) {
		lockAllAndThen(0, action);
	}

	private void lockAllAndThen(int nextStripe, Runnable action) {
		if (nextStripe >= lockCount)
			action.run();
		else
			synchronized (locks[nextStripe]) {
				lockAllAndThen(nextStripe + 1, action);
			}
	}

	static class ItemNode<K, V> {
		private final K k;
		private final V v;
		private final ItemNode<K, V> next;

		public ItemNode(K k, V v, ItemNode<K, V> next) {
			this.k = k;
			this.v = v;
			this.next = next;
		}

		// These work on immutable data only, no synchronization needed.
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
	// single thread, so no need for "volatile" or synchronization.

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

// ----------------------------------------------------------------------
// A wrapper around the Java class library's sophisticated
// ConcurrentHashMap<K,V>, making it implement OurMap<K,V>

class WrapConcurrentHashMap<K, V> implements OurMap<K, V> {
	final ConcurrentHashMap<K, V> underlying = new ConcurrentHashMap<K, V>();

	public boolean containsKey(K k) {
		return underlying.containsKey(k);
	}

	public V get(K k) {
		return underlying.get(k);
	}

	public V put(K k, V v) {
		return underlying.put(k, v);
	}

	public V putIfAbsent(K k, V v) {
		return underlying.putIfAbsent(k, v);
	}

	public V remove(K k) {
		return underlying.remove(k);
	}

	public int size() {
		return underlying.size();
	}

	public void forEach(Consumer<K, V> consumer) {
		underlying.forEach((k, v) -> consumer.accept(k, v));
	}

	public void reallocateBuckets() {
	}
}
