// For week 4

// Experiment with volatile and non-volatile
// sestoft@itu.dk * 2012-10-21, 2014-09-10

class TestVolatileCost {
  public static void main(String[] args) {
    final int size = 10_000;
    final IntArray ia = new IntArray(size);
    IntArrayVolatile iav = new IntArrayVolatile(size);

    Mark7("IntArray", new IntToDouble() {
	public double call(int i) { 
	  return ia.isSorted() ? 1.0 : 0.0;
	}});
    Mark7("IntArrayVolatile", new IntToDouble() {
	public double call(int i) { 
	  return ia.isSorted() ? 1.0 : 0.0;
	}});
    Mark7("IntArray", new IntToDouble() {
	public double call(int i) { 
	  return ia.isSorted() ? 1.0 : 0.0;
	}});
    Mark7("IntArrayVolatile", new IntToDouble() {
	public double call(int i) { 
	  return ia.isSorted() ? 1.0 : 0.0;
	}});
  }

  // --- Benchmarking infrastructure ---

  // NB: Modified to show microseconds instead of nanoseconds

  public static double Mark7(String msg, IntToDouble f) {
    int n = 10, count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do { 
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) 
          dummy += f.call(i);
        runningTime = t.check();
        double time = runningTime * 1e6 / count; // microseconds
        st += time; 
        sst += time * time;
        totalCount += count;
      }
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt(sst/n - mean*mean);
    System.out.printf("%-25s %15.1f us %10.2f %10d%n", msg, mean, sdev, count);
    return dummy / totalCount;
  }

  public static void SystemInfo() {
    System.out.printf("# OS:   %s; %s; %s%n", 
                      System.getProperty("os.name"), 
                      System.getProperty("os.version"), 
                      System.getProperty("os.arch"));
    System.out.printf("# JVM:  %s; %s%n", 
                      System.getProperty("java.vendor"), 
                      System.getProperty("java.version"));
    // This line works only on MS Windows:
    System.out.printf("# CPU:  %s%n", System.getenv("PROCESSOR_IDENTIFIER"));
    java.util.Date now = new java.util.Date();
    System.out.printf("# Date: %s%n", 
      new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
  }
}

class IntArray {
  private int[] array; 

  public IntArray(int length) {
    array = new int[length];
    for (int i=0; i<length; i++)
      array[i] = 2 * i + 1;
  }

  public boolean isSorted() {
    for (int i=1; i<array.length; i++)
      if (array[i-1] > array[i])
	return false;
    return true;
  }
}

class IntArrayVolatile {
  private volatile int[] array; 

  public IntArrayVolatile(int length) {
    array = new int[length];
    for (int i=0; i<length; i++)
      array[i] = 2 * i + 1;

  }

  public boolean isSorted() {
    for (int i=1; i<array.length; i++)
      if (array[i-1] > array[i])
	return false;
    return true;
  }
}
