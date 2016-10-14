package ch5;

import java.util.concurrent.locks.ReentrantLock;

public class ReenterLockMyDemo {
	public static void main(String[] args) throws InterruptedException {
		for(int i=0;i<10;i++) {
			new worker().start();
		}
		Thread.sleep(2000);
		System.out.println(new worker().i);	
	}
}

class worker extends Thread {
	public static ReentrantLock lock = new ReentrantLock();
	public static int  i = 0;
	public void run () {
		for(int j=0;j<10000;j++) {
			lock.lock();
			lock.lock();
			lock.lock();
			i++;
			lock.unlock();
			lock.unlock();
			lock.unlock();
			
		}		
	}
}