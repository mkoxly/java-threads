package ch4;

import java.util.concurrent.atomic.AtomicInteger;

public class IntegerDemo {
    static int i= 0;
    static Object lock = new Object();
    public static class AddThread implements Runnable{
        public void run(){
           for(int k=0;k<10000;k++)
             synchronized(lock) {
        	   i++;
             }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Thread[] ts=new Thread[10];
        for(int k=0;k<10;k++){
            ts[k]=new Thread(new AddThread());
        }
        for(int k=0;k<10;k++){ts[k].start();}
        for(int k=0;k<10;k++){ts[k].join();}
        System.out.println(i);
    }
}
