package ch4;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class IntergerArrayDemo2 {
	static Vector<Integer> arr = new Vector<Integer>(10);
	
    public static class AddThread implements Runnable{
        public void run(){
           for(int k=0;k<100000;k++)  {      	  
               int  i = k%10;
               arr.set(i, arr.get(i)+1); }
        }
    }
	public static void main(String[] args) throws InterruptedException {
		for(int i=0;i<10;i++) arr.add(0);
        Thread[] ts=new Thread[10];
        int ths = 2;
        for(int k=0;k<ths;k++){
            ts[k]=new Thread(new AddThread());
        }
        for(int k=0;k<ths;k++){ts[k].start();}
        for(int k=0;k<ths;k++){ts[k].join();}
        System.out.println(arr); //10个线程共累加10万次 应该100000才对.结果不是
	}
}
