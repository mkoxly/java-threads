package ch10;

import java.util.Random;

public class DeadLockDemo {
	public static void main(String[] args) {  
        Object o1 = new Object();  
        Object o2 = new Object(); 
        Runner r1 = new Runner(1, o1, o2, 5000);  
        Runner r2 = new Runner(2, o2, o1, 500);  
        new Thread(r1).start();  
        new Thread(r2).start();  
    }  
}  
class Runner implements Runnable {  
    private int id;  
    private Object o1;  
    private Object o2;   
    private int sleep;  
    public Runner(int id, Object o1, Object o2, int sleep) {  
        this.id = id;  
        this.o1 = o1;  
        this.o2 = o2;  
        this.sleep = sleep;  
    }  
    public void run() {  
        System.out.println(id);  
       
        synchronized (o1) {  
            try {  
                Thread.sleep(sleep);
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
            // Ëø¶¨×ÊÔ´2  
            synchronized (o2) {  
                System.out.println("Runner " + id);  
            }  
        }  
    }  
} 