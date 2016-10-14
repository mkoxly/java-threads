package ch8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CalServerAIO {
    public final static int PORT = 8001;
    private AsynchronousServerSocketChannel server;
    public CalServerAIO() throws IOException {
        server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));
    }

    public void start() throws InterruptedException, ExecutionException, TimeoutException {
        System.out.println("Server listen on " + PORT);
        //注册事件和事件完成后的处理器
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
        	final BaseExpression BE = new BaseExpression();
            final ByteBuffer buffer = ByteBuffer.allocate(8024);
            public String byteBufferToString(final ByteBuffer buffer) {
        		CharBuffer charBuffer = null;
        		try {
        			Charset charset = Charset.forName("UTF-8");
        			CharsetDecoder decoder = charset.newDecoder();
        			charBuffer = decoder.decode(buffer);
        			buffer.flip();
        			return charBuffer.toString();
        		} catch (Exception ex) {
        			ex.printStackTrace();
        			return null;
        		}
        	}
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                System.out.println(Thread.currentThread().getName());
                Future<Integer> writeResult=null;
                try {
                    buffer.clear();
                    result.read(buffer).get(100, TimeUnit.SECONDS);
                    buffer.flip();
                    
                    String exp = byteBufferToString(buffer);
                	System.out.println("exp:"+exp);
                	String res = null;
					try {
						res = (String) BE.calculate(exp);
					} catch (ExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    System.out.println("result:" + res); 
                    writeResult=result.write(ByteBuffer.wrap(res.getBytes()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        server.accept(null, this);
                        writeResult.get();
                        result.close();
                    } catch (Exception e) {
                        System.out.println("expception " + e.toString());
                    }
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed: " + exc);
            }
        });
    }

    public static void main(String args[]) throws Exception {
        new CalServerAIO().start();
        // 主线程可以继续自己的行为
        while (true) {
            Thread.sleep(1000);
        }
    }
}
