// For week 5
// sestoft@itu.dk * 2014-09-23

// A pipeline of transformers connected by bounded queues.  Each
// transformer consumes items from its input queue and produces items
// on its output queue.

// This is illustrated by generating URLs, fetching the corresponding
// webpages, scanning the pages for links to other pages, and printing
// those links; using four threads connected by three queues:

// UrlProducer --(BlockingQueue<String>)--> 
// PageGetter  --(BlockingQueue<Webpage>)--> 
// LinkScanner --(BlockingQueue<Link>)--> 
// LinkPrinter

// For reading webpages
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
// For regular expressions
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPipelineExecutorService {

	// private static final ExecutorService executor =
	// Executors.newWorkStealingPool(); //doesn't work on a dual core machine!!
	private static final ExecutorService executor = Executors
			.newCachedThreadPool();

	// .newFixedThreadPool(6);
	// .newFixedThreadPool(3);

	public static void main(String[] args) {
		runAsThreads();
	}

	private static void runAsThreads() {
		// final BlockingQueue<String> urls = new OneItemQueue<String>();
		// final BlockingQueue<Webpage> pages = new OneItemQueue<Webpage>();
		// final BlockingQueue<Link> refPairs = new OneItemQueue<Link>();
		// final BlockingQueue<Link> UniqueRefPairs = new OneItemQueue<Link>();

		final BlockingQueue<String> urls = new BoundedQueue<String>(3);
		final BlockingQueue<Webpage> pages = new BoundedQueue<Webpage>(3);
		final BlockingQueue<Link> refPairs = new BoundedQueue<Link>(3);
		final BlockingQueue<Link> UniqueRefPairs = new BoundedQueue<Link>(3);

		executor.submit(new UrlProducer(urls));
		executor.submit(new PageGetter(urls, pages));
		executor.submit(new PageGetter(urls, pages));
		executor.submit(new LinkScanner(pages, refPairs));
		executor.submit(new Uniquifier(refPairs, UniqueRefPairs));
		executor.submit(new LinkPrinter(UniqueRefPairs));
	}
}

class Uniquifier<T> implements Runnable {

	private final HashSet<T> hashSet;
	private final BlockingQueue<T> input;
	private final BlockingQueue<T> output;

	public Uniquifier(BlockingQueue<T> inQueue, BlockingQueue<T> outQueue) {
		hashSet = new HashSet<T>();
		input = inQueue;
		output = outQueue;
	}

	@Override
	public void run() {
		while (true) {
			T item = input.take();
			if (!hashSet.contains(item)) {
				hashSet.add(item);
				output.put(item);
			}
		}
	}

}

class UrlProducer implements Runnable {
	private final BlockingQueue<String> output;

	public UrlProducer(BlockingQueue<String> output) {
		this.output = output;
	}

	@Override
	public void run() {
		for (int i = 0; i < urls.length; i++) {
			output.put(urls[i]);
			// System.out.println(urls[i]);
		}
	}

	private static final String[] urls = { "http://www.itu.dk",
			"http://www.di.ku.dk", "http://www.miele.de",
			"http://www.microsoft.com", "http://www.amazon.com",
			"http://www.dr.dk", "http://www.vg.no", "http://www.tv2.dk",
			"http://www.google.com", "http://www.ing.dk", "http://www.dtu.dk",
			"http://www.bbc.co.uk" };
}

class PageGetter implements Runnable {
	private final BlockingQueue<String> input;
	private final BlockingQueue<Webpage> output;

	public PageGetter(BlockingQueue<String> input, BlockingQueue<Webpage> output) {
		this.input = input;
		this.output = output;
	}

	public void run() {
		while (true) {
			String url = input.take();
			// System.out.println("PageGetter: " + url);
			try {
				String contents = getPage(url, 200);
				output.put(new Webpage(url, contents));
			} catch (IOException exn) {
				System.out.println(exn);
			}
		}
	}

	public static String getPage(String url, int maxLines) throws IOException {
		// This will close the streams after use (JLS 8 para 14.20.3):
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				new URL(url).openStream()))) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < maxLines; i++) {
				String inputLine = in.readLine();
				if (inputLine == null)
					break;
				else
					sb.append(inputLine).append("\n");
			}
			return sb.toString();
		}
	}
}

class LinkScanner implements Runnable {
	private final BlockingQueue<Webpage> input;
	private final BlockingQueue<Link> output;

	public LinkScanner(BlockingQueue<Webpage> input, BlockingQueue<Link> output) {
		this.input = input;
		this.output = output;
	}

	private final static Pattern urlPattern = Pattern
			.compile("a href=\"(\\p{Graph}*)\"");

	public void run() {
		while (true) {
			Webpage page = input.take();
			// System.out.println("LinkScanner: " + page.url);
			// Extract links from the page's <a href="..."> anchors
			Matcher urlMatcher = urlPattern.matcher(page.contents);
			while (urlMatcher.find()) {
				String link = urlMatcher.group(1);
				output.put(new Link(page.url, link));
			}
		}
	}
}

class LinkPrinter implements Runnable {
	private final BlockingQueue<Link> input;

	public LinkPrinter(BlockingQueue<Link> input) {
		this.input = input;
	}

	public void run() {
		while (true) {
			Link link = input.take();
			// System.out.println("LinkPrinter: " + link.from);
			System.out.printf("%s links to %s%n", link.from, link.to);
		}
	}
}

class Webpage {
	public final String url, contents;

	public Webpage(String url, String contents) {
		this.url = url;
		this.contents = contents;
	}
}

class Link {
	public final String from, to;

	public Link(String from, String to) {
		this.from = from;
		this.to = to;
	}

	// Override hashCode and equals so can be used in HashSet<Link>

	public int hashCode() {
		return (from == null ? 0 : from.hashCode()) * 37
				+ (to == null ? 0 : to.hashCode());
	}

	public boolean equals(Object obj) {
		Link that = obj instanceof Link ? (Link) obj : null;
		return that != null
				&& (from == null ? that.from == null : from.equals(that.from))
				&& (to == null ? that.to == null : to.equals(that.to));
	}
}

// Different from java.util.concurrent.BlockingQueue: Allows null
// items, and methods do not throw InterruptedException.

interface BlockingQueue<T> {
	void put(T item);

	T take();
}

class OneItemQueue<T> implements BlockingQueue<T> {
	private T item;
	private boolean full = false;

	public void put(T item) {
		synchronized (this) {
			while (full) {
				try {
					this.wait();
				} catch (InterruptedException exn) {
				}
			}
			full = true;
			this.item = item;
			this.notifyAll();
		}
	}

	public T take() {
		synchronized (this) {
			while (!full) {
				try {
					this.wait();
				} catch (InterruptedException exn) {
				}
			}
			full = false;
			this.notifyAll();
			return item;
		}
	}
}

class BoundedQueue<T> implements BlockingQueue<T> {
	private final int capacity;
	private final List<T> buffer = new ArrayList<T>();
	int enqueueIndex = 0;
	int dequeueIndex = 0;
	int count = 0;

	public BoundedQueue(int capacity) {
		synchronized (this) {
			this.capacity = capacity;
			for (int i = 0; i < capacity; i++) {
				this.buffer.add(null);
			}
		}
	}

	@Override
	public void put(T item) {
		synchronized (this) {
			while (count == capacity) {
				try {
					this.wait();
				} catch (InterruptedException exn) {
				}
			}
			buffer.set(enqueueIndex, item);
			enqueueIndex = ++enqueueIndex == capacity ? 0 : enqueueIndex;
			++count;
			this.notifyAll();
		}
	}

	@Override
	public T take() {
		synchronized (this) {
			while (count == 0) {
				try {
					this.wait();
				} catch (InterruptedException exn) {
				}
			}
			T item = buffer.get(dequeueIndex);
			dequeueIndex = ++dequeueIndex == capacity ? 0 : dequeueIndex;
			--count;
			this.notifyAll();
			return item;
		}
	}
}
