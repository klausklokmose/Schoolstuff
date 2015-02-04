import java.util.concurrent.atomic.AtomicReference;

public class SimpleRWTryLock {

	AtomicReference<Holders> holders;

	public SimpleRWTryLock() {
		holders = new AtomicReference<Holders>();
		holders.set(null);
	}

	/**
	 * Method readerTryLock is called by a thread that tries to obtain a read
	 * lock. It must succeed and return true if the lock is held only by readers
	 * (or nobody), and return false if the lock is held by a writer.
	 * @return
	 */
	public boolean readerTryLock() {
		// TODO
		/*
		 * There is nothing stated saying that a thread cannot 
		 * try to take the lock multiple times
		 */
		final Thread current = Thread.currentThread();
		Holders old = null;
		ReaderList newHolder = null;
		// if lock is null or if instance is not a writer
		do {
			old = holders.get();
			if (old == null) {
				newHolder = new ReaderList(current, null);
			} else if(old instanceof ReaderList){
				try {
					old = (ReaderList) holders.get();
					newHolder = new ReaderList(current, (ReaderList) old);
				} catch (Exception e) {
				}
			}else {
				return false;
			}
		} while (!holders.compareAndSet(old, newHolder));
		return true;
	}

	/**
	 * Method readerUnlock is called to release a read lock, and must throw an
	 * exception if the calling thread does not hold a read lock.
	 * @throws Exception
	 */
	public void readerUnlock() throws Exception, ClassCastException {
		// TODO
		if(holders.get() == null){
			throw new Exception("Lock is not held by anyone");
		}
		final Thread current = Thread.currentThread();
		ReaderList newList;
		Holders readerLock;
		do {
			readerLock = holders.get(); //return the value - not the instance holders
			if (readerLock instanceof Writer) {
				throw new Exception("Lock is not held by any reader");
			}
			if(!((ReaderList) readerLock).contains(current)){
				throw new Exception("Lock not acquired");
			}
			newList = ((ReaderList) readerLock).remove(current);
		} while (!holders.compareAndSet(readerLock, newList));
	}

	/**
	 * Method writerTryLock is called by a thread that tries to obtain a write
	 * lock. It must succeed and return true if the lock is not already held by
	 * any thread, and return false if the lock is held by at least one reader
	 * or by a writer.
	 */
	public boolean writerTryLock() {
		// TODO
		Holders h = holders.get();
		final Thread current = Thread.currentThread();
		if (h == null) {
			return holders.compareAndSet(null, new Writer(current));
		} else {
			return false;
		}
	}

	/**
	 * Method writerUnlock is called to release the write lock, and must throw
	 * an exception if the calling thread does not hold a write lock.
	 * @throws Exception
	 */
	public void writerUnlock() throws Exception {
		// TODO
		if(holders.get() instanceof ReaderList){
			throw new Exception("Lock was held by a reader, one should use readerUnlock()");
		}else if(holders.get() == null){
			throw new Exception("Lock was not held by anyone");
		}
		final Thread current = Thread.currentThread();
		if(((Writer)holders.get()).thread.equals(current)){
			holders.compareAndSet(holders.get(), null);
		}else{
			throw new Exception("Lock was not held by this thread ");
		}
	}

	static abstract class Holders {

		public static final Thread thread = null;
	}

	private static class ReaderList extends Holders {
		private final Thread thread;
		private final ReaderList next;

		public ReaderList(Thread t, ReaderList next) {
			this.thread = t;
			this.next = next;
		}

		public boolean contains(Thread t) {
			ReaderList l = this;
			while (l != null) {
				if (l.thread.equals(t)) {
					return true;
				} else {
					l = l.next;
				}
			}
			return false;
		}

		/**
		 * Implementation very similar to week 10s delete
		 * @param t
		 * @return
		 */
		public ReaderList remove(Thread t) {
			ReaderList rList = this;
			if (rList == null || rList.next == null) {
				return null;
			} else if (t.equals(rList.thread)) {
				return rList.next;
			} else {
				final ReaderList newList = rList.next.remove(t);
				if (newList == rList.next) {
					return rList;
				} else {
					return new ReaderList(rList.thread, newList);
				}
			}
		}

	}

	private static class Writer extends Holders {
		public final Thread thread;

		public Writer(Thread t) {
			this.thread = t;
		}
	}
}
