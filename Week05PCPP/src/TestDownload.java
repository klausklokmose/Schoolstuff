// For week 5
// sestoft@itu.dk * 2014-09-19

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestDownload {

	private static final String[] urls = { "http://www.itu.dk",
			"http://www.di.ku.dk", "http://www.miele.de",
			"http://www.microsoft.com", "http://www.amazon.com",
			"http://www.dr.dk", "http://www.vg.no", "http://www.tv2.dk",
			"http://www.google.com", "http://www.ing.dk", "http://www.dtu.dk",
			"http://www.eb.dk", "http://www.nytimes.com",
			"http://www.guardian.co.uk", "http://www.lemonde.fr",
			"http://www.welt.de", "http://www.dn.se", "http://www.heise.de",
			"http://www.wsj.com", "http://www.bbc.co.uk", "http://www.dsb.dk",
			"http://www.bmw.com", "https://www.cia.gov" };

	public static void main(String[] args) throws IOException {
		// String url = "http://www.wikipedia.org/";
		// String page = getPage(url, 10);
		// System.out.printf("%-30s%n%s%n", url, page);
		//
		Map<String, String> pages = new HashMap<String, String>();
		int maxLines = 200;
		// for (int i = 0; i < 5; i++) {
		// Timer time = new Timer();
		// pages = getPages(urls, maxLines);
		// System.out.println("i= " + i + " - " + time.check());
		// }
		for (int i = 0; i < 5; i++) {
			Timer time = new Timer();
			pages = getPagesParallel(urls, maxLines);
			System.out.println("i= " + i + " - " + time.check());
		}
		for (Map.Entry<String, String> entry : pages.entrySet()) {
			System.out.println(entry.getKey() + " ---> "
					+ entry.getValue().length());
		}
	}

	private static ExecutorService executor = Executors.newWorkStealingPool();

	@SuppressWarnings("unchecked")
	public static Map<String, String> getPagesParallel(String[] urls,
			final int maxLines) {
		Map<String, String> result = new HashMap<>();
		List<Callable<String>> tasks = new ArrayList<Callable<String>>();
		for (int i = 0; i < urls.length; i++) {
			String url = urls[i];
			tasks.add(new Callable<String>() {
				@Override
				public String call() throws Exception {
					try {
						return getPage(url, maxLines);
					} catch (IOException e) {
						System.out.println("page not found");
					}
					return "";
				}
			});
		}
		List<Future<String>> futures;
		try {
			futures = executor.invokeAll(tasks);
			for (int i = 0; i < urls.length; i++) {
				result.put(urls[i], futures.get(i).get());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static Map<String, String> getPages(String[] urls, int maxLines) {
		Map<String, String> result = new HashMap<>();
		for (String url : urls) {
			try {
				String content = getPage(url, maxLines);
				result.put(url, content);
			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("url: " + url + " did not respond!");
			}
		}
		return result;
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
