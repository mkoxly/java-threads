package ch10;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

public class StampLockTestDemo {
	public static void main(String[] args) throws InterruptedException {
		System.out.println("StampLock:" + test(new Point1()) + " ms");
		System.out.println("ReentrantReadWriteLock:" + test(new Point2()) + " ms");
	}

	public static long test(Point p) {
		long current = System.currentTimeMillis();
		ReadWorker r = new ReadWorker(p);
		WriteWorker w = new WriteWorker(p);
		Thread[] threads = new Thread[21];
		threads[0] = new Thread(w);
		for (int i = 1; i <= 20; i++) {
			threads[i] = new Thread(r);
		}
		for (int i = 0; i <= 20; i++) {
			threads[i].start();
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return System.currentTimeMillis() - current;
	}

}

class ReadWorker implements Runnable {
	private Point p;

	public ReadWorker(Point p) {
		this.p = p;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10000; i++)
			p.distanceFromOrigin();
	}

}

class WriteWorker implements Runnable {
	private Point p;

	public WriteWorker(Point p) {
		this.p = p;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10000; i++)
			p.move(0.1, 0.2);
	}

}

interface Point {
	public void move(double deltaX, double deltaY);

	public double distanceFromOrigin();

	public void moveIfAtOrigin(double newX, double newY);

}

class Point1 implements Point {
	private double x, y;
	private final StampedLock sl = new StampedLock();

	public void move(double deltaX, double deltaY) { // an exclusively locked
														// method
		long stamp = sl.writeLock();
		try {
			x += deltaX;
			y += deltaY;
		} finally {
			sl.unlockWrite(stamp);
		}
	}

	// ���濴���ֹ۶�������
	public double distanceFromOrigin() { // A read-only method
		long stamp = sl.tryOptimisticRead(); // ���һ���ֹ۶���
		double currentX = x, currentY = y; // �������ֶζ��뱾�ؾֲ�����
		if (!sl.validate(stamp)) { // ��鷢���ֹ۶�����ͬʱ�Ƿ�������д��������
			stamp = sl.readLock(); // ���û�У������ٴλ��һ����������
			try {
				currentX = x; // �������ֶζ��뱾�ؾֲ�����
				currentY = y; // �������ֶζ��뱾�ؾֲ�����
			} finally {
				sl.unlockRead(stamp);
			}
		}
		return Math.sqrt(currentX * currentX + currentY * currentY);
	}

	// �����Ǳ��۶�������
	public void moveIfAtOrigin(double newX, double newY) { // upgrade
		// Could instead start with optimistic, not read mode
		long stamp = sl.readLock();
		try {
			while (x == 0.0 && y == 0.0) { // ѭ������鵱ǰ״̬�Ƿ����
				long ws = sl.tryConvertToWriteLock(stamp); // ������תΪд��
				if (ws != 0L) { // ����ȷ��תΪд���Ƿ�ɹ�
					stamp = ws; // ����ɹ� �滻Ʊ��
					x = newX; // ����״̬�ı�
					y = newY; // ����״̬�ı�
					break;
				} else { // ������ܳɹ�ת��Ϊд��
					sl.unlockRead(stamp); // ������ʽ�ͷŶ���
					stamp = sl.writeLock(); // ��ʽֱ�ӽ���д�� Ȼ����ͨ��ѭ������
				}
			}
		} finally {
			sl.unlock(stamp); // �ͷŶ�����д��
		}
	}
}

class Point2 implements Point {
	private double x, y;
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	public void move(double deltaX, double deltaY) { // an exclusively locked
														// method
		w.lock();
		try {
			x += deltaX;
			y += deltaY;
		} finally {
			w.unlock();
		}
	}

	public double distanceFromOrigin() {
		double currentX, currentY;
		r.lock();
		try {
			currentX = x;
			currentY = y;
		} finally {
			r.unlock();
		}
		return Math.sqrt(currentX * currentX + currentY * currentY);

	}

	public void moveIfAtOrigin(double newX, double newY) {

	}

}