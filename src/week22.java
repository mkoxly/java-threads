
public class week22 {
	public static void main(String[] args) throws InterruptedException {
		Que que = new Que(20);
		Thread adda = new Thread(new Runnable() {
			public void run() {
				for (int i = 1; i <= 200; i++) {
					que.add('a');
					//notify();
				}
			}
		});
		Thread addb = new Thread(new Runnable() {
			public void run() {
				for (int i = 1; i <= 200; i++) {
					que.add('b');
					//notify();
				}
			}
		});
		Thread addc = new Thread(new Runnable() {
			public void run() {
				for (int i = 1; i <= 400; i++) {
					que.get();					
					//notify();
				}
			}
		});

		adda.start();
		addc.start();
		addb.start();
		Thread.sleep(3000);
		
	}

}

class Que {
	Quedata q;

	public Que(int len) {
		q = new Quedata();
		q.len = len;
		q.top = 0;
		q.data = new char[len];

	}

	public synchronized void add(char item) {		
			while (isFull()) {
				try {
					System.out.println(Thread.currentThread().getName()+" is waiting for add !");
					wait();
					Thread.sleep(10);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(Thread.currentThread().getName()+" is adding data !");
			q.data[q.top] = item;
			q.top++;
			notifyAll();
			
		

	}

	public synchronized char get() {		
			while (isEmpty()) {
				try {
					System.out.println(Thread.currentThread().getName()+" is waiting for get !");
					wait();
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			q.top--;			
			char ret = q.data[q.top];
			System.out.println(Thread.currentThread().getName()+" get :"+ ret);	
			notifyAll();
			return ret;		
	}

	public boolean isFull() {
		return q.top >= q.len;
	}

	public boolean isEmpty() {
		return q.top < 1;
	}

	public void print() {
		for (int i = 0; i < q.top; i++) {
			System.out.print(q.data[i]);
		}
		System.out.println();
		System.out.println("len=" + (q.top));
	}

	class Quedata {
		char[] data;
		int len;
		int top;
	}
}
