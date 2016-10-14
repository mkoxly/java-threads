import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueDemo {
	public static void main(String[] args) throws InterruptedException {
		Queue<String> que1 = new LinkedBlockingQueue<String>();
		Queue<String> que2 = new ConcurrentLinkedQueue<String>();
		Thread[] ths = new Thread[200];
		for(int i=0;i<100;i++) ths[i] = new Product("product",que1);
		for(int i=100;i<200;i++) ths[i] = new Consume("Consume",que1);
		long begin = System.currentTimeMillis();
		for(int i=0;i<200;i++) {ths[i].start();}
		boolean alive = true;
		while (true) {
			alive = false;
			for(int i=0;i<200;i++) {
				if (ths[i].isAlive()) {
					alive = true;
					break;
				};
			}
			if(!alive) break;		
		}		
		System.out.println("LinkedBlockingQueue:"+(System.currentTimeMillis()-begin));	
		for(int i=0;i<100;i++) ths[i] = new Product("product",que2);
		for(int i=100;i<200;i++) ths[i] = new Consume("Consume",que2);
		begin = System.currentTimeMillis();
		for(int i=0;i<200;i++) {ths[i].start();}
		alive = true;
		while (true) {
			alive = false;
			for(int i=0;i<200;i++) {
				if (ths[i].isAlive()) {
					alive = true;
					break;
				};
			}
			if(!alive) break;		
		}		
		System.out.println("ConcurrentLinkedQueue"+(System.currentTimeMillis()-begin));	
	}
}

class Product extends Thread {	
	private Queue<String> que;
	public Product(String name,Queue<String> que) {
		super(name);
		this.que = que;
	}
	public void run() {
		for(int i=0;i<1000;i++) {
			que.add("item");
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
class Consume  extends Thread{
	private Queue<String> que;
	public Consume(String name,Queue<String> que) {
		super(name);
		this.que = que;
	}
	public void run() {
		for(int i=0;i<1000;i++) {
			que.poll();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}