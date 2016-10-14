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

public class CalClient {
	private static ExecutorService  tp=Executors.newCachedThreadPool();
	public static class EchoClient implements Runnable{
		private String name;
		private String exp;
		public EchoClient(String name,String exp) {
			this.name = name;
			this.exp = exp;
			
		}
		public void run(){
	        Socket client = null;
	        PrintWriter writer = null;
	        BufferedReader reader = null;
	        try {
	            client = new Socket();
	            client.connect(new InetSocketAddress("localhost", 8001));
	            writer = new PrintWriter(client.getOutputStream(), true);	            
	            writer.print(exp);
	            writer.flush();

	            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
	            System.out.println("Client "+this.name+" send "+exp+" got from server: " + reader.readLine());
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
    	//EchoClient ec=new EchoClient();
    	for(int i=0;i<10;i++)
    		tp.execute(new EchoClient("client"+i,"100+"+i+"*"+i));
    	
    }
}
