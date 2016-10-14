package ch8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

/**
 * @author andy
 * @create 2016-01-24 15:17
 */
public class ClientDemo {
    private static ExecutorService tp= Executors.newCachedThreadPool();
    private static final int sleep_time=1000*1000;

    public static class Client implements Runnable{
        public void run(){
            Socket client = null;
            PrintWriter writer = null;
            BufferedReader reader = null;
            try {
                client = new Socket();
                client.connect(new InetSocketAddress("localhost", 8001));
                writer = new PrintWriter(client.getOutputStream(), true);
                writer.print("(5");
                LockSupport.parkNanos(sleep_time);
                writer.print("*(3");
                LockSupport.parkNanos(sleep_time);
                writer.print("-1)");
                LockSupport.parkNanos(sleep_time);
                writer.print("+9");
                LockSupport.parkNanos(sleep_time);
                writer.print("-5");
                LockSupport.parkNanos(sleep_time);
                writer.print(")/2");
                LockSupport.parkNanos(sleep_time);
                writer.flush();

                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                System.out.print("from server: " + reader.readLine());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null)
                        writer.close();
                    if (reader != null)
                        reader.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        for(int i=0;i<1;i++)
            tp.execute(client);
    }
}