import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LockDemo {
	static int i = 0;
	private static Object lock = new Object();
	private static class Worker extends Thread {		
		public void run() {
			System.out.println(Thread.currentThread().getName());
			synchronized(lock) {
				for(int j=0;j<1000000000;j++) {
					i++;
				}
			}
		}
	}
	public static void main(String[] args ) throws InterruptedException {
		long b = System.currentTimeMillis();
		//ExecutorService es = Executors.newFixedThreadPool(2);
		Worker worker1 = new Worker();
		Worker worker2 = new Worker();
		
		//worker1.start();
		worker2.start();		
		//worker1.join();
		worker2.join();
		System.out.println(i + "-->" + (System.currentTimeMillis()-b));
		
	}
	
}

