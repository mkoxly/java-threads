
package ch6;

import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExtThreadPool {
	

	public static void main(String[] args) throws InterruptedException {

		ES es = new ES();
		for (int i = 0; i < 10; i++)
		{
			MyTask task = new MyTask("TASK-GEYM-" + i);
			//es.execute(task);
			es.submit(task);
			Thread.sleep(10);
		}
		es.shutdown();
	}
}
class ES extends ThreadPoolExecutor {
	public ES () {
		super(5, 5, 0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
	}
	protected void beforeExecute(Runnable r,Thread t) {
		System.out.println("准备执行：" + ((MyTask) r).name);
	}	

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		printException(r,t);
		//System.out.println("执行完成：" + ((MyTask) r).name); 如果线程异常,此转换同时也异常
		System.out.println("执行完成!");
	}
	protected void terminated() {
		System.out.println("线程池退出");
	}
	private void printException(Runnable r, Throwable t) {		
		if (t == null && r instanceof Future<?>) {
			try {
				Future<?> future = (Future<?>) r;
				if (future.isDone())
					future.get();
			} catch (CancellationException ce) {
				t = ce;
			} catch (ExecutionException ee) {
				t = ee.getCause();
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt(); // ignore/reset
			}

		}
		if (t != null) {
			//System.out.println(t.getMessage());	
			System.out.println("exception:");
			t.printStackTrace();
		}
	}	
}
class MyTask extends Thread {
	public String name;
	public MyTask(String name) {
		setName(name);
		this.name= name;
	}
	@Override
	public void run() {
		System.out.println("正在执行" + ":Thread ID:" + Thread.currentThread().getId() + ",Task Name=" + name);
		Random ran = new Random();
		if (ran.nextInt(10) <=2) throw new NullPointerException();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
