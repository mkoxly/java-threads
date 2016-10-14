package ch4;

import java.util.concurrent.atomic.AtomicReference;

public class NoAtomicReferenceTest {  
    public  static String Str = new String("abc");  
      
    public static void main(String []args) {  
        for(int i = 0 ; i < 10 ; i++) {  
            final int num = i;  
            new Thread() {  
                public void run() {  
                    try {  
                        Thread.sleep(Math.abs((int)(Math.random() * 100)));  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                    if(Str.equals("abc")) {  
                    	Str = "def" ;
                        System.out.println("Thread:"+Thread.currentThread().getId()+" Change value to "+Str);  
                    } else{
                    	System.out.println("Thread:"+Thread.currentThread().getId()+" FAILED");
                    }
                }  
            }.start();  
        }  
    }  
}  
