
public class week21 {
	public static void main(String[] args) throws InterruptedException {
		Thread T1,T2,T3;
		T1 = new mythread("T1");
		T2 = new mythread("T2");
		T3 = new mythread("T3");
		T1.start();
		T1.join();
		T2.start();
		T2.join();
		T3.start();
		T3.join();
		
		
	}
}
class mythread extends Thread {
	public mythread(String name) {
		super(name);
	}
	public void run() {
		System.out.println(this.getName()+" is runnning !");
	}
}