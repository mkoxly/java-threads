package ch2;

public class SimpleWNA {
	final static Object object = new Object();
	public static class T1 extends Thread{
		public T1 (String name) {
			setName(name);
		}
        public void run()
        {
            synchronized (object) {
                System.out.println(Thread.currentThread().getName()+" T1 start! wait on object");
                try {
                    object.wait();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+" T1 end!");
            }
        }
	}
	public static class T2 extends Thread{
		public T2(String name) {
			setName(name);
		}
        public void run()
        {
            synchronized (object) {
                System.out.println(Thread.currentThread().getName()+" T2 start! notify all threads");
                object.notifyAll();
                System.out.println(Thread.currentThread().getName()+" T2 end!");
                try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
            }
        }
	}
	public static void main(String[] args) throws InterruptedException {
        Thread t1 = new T1("T1_1") ;
        Thread t1_1 = new T1("T1_2") ;
        t1_1.start();
        t1.start();
        Thread.sleep(1000);
        Thread t2 = new T2("T2_1") ;
        t2.start();
	}
}
