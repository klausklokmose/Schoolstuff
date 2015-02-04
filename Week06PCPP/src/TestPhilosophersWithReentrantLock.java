import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// For week 6
// sestoft@itu.dk * 2014-09-29

// The Dining Philosophers problem, due to E.W. Dijkstra 1965.  Five
// philosophers (threads) sit at a round table on which there are five
// forks (shared resources), placed between the philosophers.  A
// philosopher alternatingly thinks and eats spaghetti.  To eat, the
// philosopher needs exclusive use of the two forks placed to his left
// and right, so he tries to lock them.  

// Both the places and the forks are numbered 0 to 5.  The fork to the
// left of place p has number p, and the fork to the right has number
// (p+1)%5.

// This solution is wrong; it will deadlock after a while.

public class TestPhilosophersWithReentrantLock {
	
	
	public static void main(String[] args) {
		final AtomicInteger[] counters = new AtomicInteger[5];
		for (int i = 0; i < counters.length; i++) {
			counters[i] = new AtomicInteger();
		}
		Fork[] forks = { new Fork(), new Fork(), new Fork(), new Fork(),
				new Fork() };
		for (int place = 0; place < forks.length; place++) {
			Thread phil = new Thread(new Philosopher(forks, place, counters[place]));
			phil.start();
		}
		
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < forks.length; j++) {
				System.out.println("Philosopher["+j+"] has eaten " + counters[j].intValue() + " times");
			}
			
			try {
				Thread.sleep(10_000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class Philosopher implements Runnable {
	private final Fork[] forks;
	private final int place;
	private final AtomicInteger count;

	public Philosopher(Fork[] forks, int place, AtomicInteger count) {
		this.forks = forks;
		this.place = place;
		this.count = count;
	}

	public void run() {
		while (true) {
			// Take the two forks to the left and the right
			int left = place, right = (place + 1) % forks.length;
			if (forks[left].tryLock()) {
				if (forks[right].tryLock()) {
					// Eat
					System.out.print(place + " ");
					count.incrementAndGet();
				}
			}
			// Think
			try {
				Thread.sleep(10);
			} catch (InterruptedException exn) {
			}
		}
	}
}

class Fork extends ReentrantLock {
}
