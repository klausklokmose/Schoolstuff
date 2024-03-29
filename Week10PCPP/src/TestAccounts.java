// For week 10
// sestoft@itu.dk * 2014-11-06

// Compile and run like this:
//   javac -cp ~/lib/multiverse-core-0.7.0.jar TestAccounts.java 
//   java -cp ~/lib/multiverse-core-0.7.0.jar:. TestAccounts

// The Multiverse transactional memory library:
import static org.multiverse.api.StmUtils.atomic;
import static org.multiverse.api.StmUtils.newTxnLong;

import java.util.Random;
import java.util.concurrent.Callable;

import org.multiverse.api.references.TxnLong;

public class TestAccounts {
  public static void main(String[] args) {
    final Account account1 = new Account(), account2 = new Account();
    final Random rnd = new Random();
    final int transfers = 2_000_000;
    account1.deposit(3000); account2.deposit(2000);
    Thread clerk1 = new Thread(new Runnable() {
    	public void run() { 
    	  for (int i=0; i<transfers; i++) 
    	    account1.transfer(account2, rnd.nextInt(10000));
    	}
      });
    Thread clerk2 = new Thread(new Runnable() {
    	public void run() { 
    	  for (int i=0; i<transfers; i++) 
    	    account2.transfer(account1, rnd.nextInt(10000));
    	}
      });
    clerk1.start(); clerk2.start();
    // We may occasionally print the account balances during the transfers:
    for (int i=0; i<40; i++) {
      try { Thread.sleep(10); } catch (InterruptedException exn) { }
      long sum = atomic(new Callable<Long>() { public Long call() {
      	    return account1.get() + account2.get();
      	  }});
      System.out.println(sum);
    }
    // The auditor prints the account balance sum when the clerks are finished: 
    try { clerk1.join(); clerk2.join(); } catch (InterruptedException exn) { }
    System.out.println(account1.get() + account2.get());
    
  }
}

class Account {
  private final TxnLong balance = newTxnLong(0);

  public void deposit(final long amount) {
    atomic(new Runnable() { public void run() {
      balance.set(balance.get() + amount);
      // Alternative, but does not give read atomicity:
      // balance.increment(amount); 
    }});
  }

  public long get() {
    return atomic(new Callable<Long>() { public Long call() {
      return balance.get();
    }});
  }

  public void transfer(Account that, final long amount) {
    final Account thisAccount = this, thatAccount = that;
    atomic(new Runnable() { public void run() {
      thisAccount.deposit(-amount);
      thatAccount.deposit(+amount);
    }});
  } 
}
