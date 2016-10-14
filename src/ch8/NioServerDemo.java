package ch8;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author andy
 * @create 2016-01-24 14:25
 */
public class NioServerDemo {

    public static ScriptEngine javascript = new ScriptEngineManager().getEngineByName("javascript");

    private Selector selector;
    private ExecutorService tp = Executors.newCachedThreadPool();

    public void startServer() throws IOException {
        selector = Selector.open();
        //��һ�������ͨ��
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//���÷�����

        //����socket�����˿�,���󶨵�ServerSocketChannel��
        InetSocketAddress isa = new InetSocketAddress(8001);
        ssc.socket().bind(isa);

        //��selectorע�ᵽ����ͨ����,������selector����Ȥ���¼�
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        for (; ; ) {
            selector.select();//�ȴ��ͻ�������
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                iterator.remove();

                if (sk.isAcceptable()) {
                    //���ӿͻ���
                    doAccept(sk);
                }
                else if (sk.isValid() && sk.isReadable()) {
                    //�ͻ���׼������,���Զ�ȡ�ͻ��˵�����
                    doRead(sk);
                }
                else if (sk.isValid() && sk.isWritable()) {
                    //������ͻ���д������
                    doWrite(sk);
                }

            }
        }
    }

    public void doAccept(SelectionKey sk) {
        ServerSocketChannel ssc = (ServerSocketChannel) sk.channel();
        SocketChannel clientChannel;
        try {
            clientChannel = ssc.accept();
            clientChannel.configureBlocking(false);//���÷�����

            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
            StringBuffer sb = new StringBuffer();//�������ݴ�ŵĶ���
            clientKey.attach(sb);

            InetAddress clientAddress = clientChannel.socket().getInetAddress();
            System.out.println("Accepted connection from " + clientAddress.getHostAddress() + ".");
        } catch (IOException e) {
            System.out.println("Failed to accept new client.");
            e.printStackTrace();
        }
    }

    public void doRead(SelectionKey sk) {
        SocketChannel clientChannel = (SocketChannel) sk.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int len;
        try {
            len = clientChannel.read(byteBuffer);
            if (len < 0) {
                disconnect(sk);
                return;
            }
        } catch (IOException e) {
            System.out.println("Failed to accept new client.");
            e.printStackTrace();
        }
        byteBuffer.flip();
        tp.execute(new HandleMsg(sk, byteBuffer));
    }

    public void doWrite(SelectionKey sk) {
        SocketChannel clientChannel = (SocketChannel) sk.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //�ͻ��˴��������
        StringBuffer sb = (StringBuffer) sk.attachment();
        int i;
        try {
            String res = sb + "=" + javascript.eval(sb.toString());
            byteBuffer.put(res.getBytes());
            byteBuffer.flip();
            //byteBuffer.put(sb.toString().getBytes());
            i = clientChannel.write(byteBuffer);
            //if (i == -1) {
                disconnect(sk);
                //return;
            //}
        } catch (Exception e) {
            System.out.println("ִ�нű������쳣");
            e.printStackTrace();
        }

        //sk.interestOps(SelectionKey.OP_READ);

    }

    private void disconnect(SelectionKey sk) {
        SocketChannel channel = (SocketChannel) sk.channel();

        InetAddress clientAddress = channel.socket().getInetAddress();
        System.out.println(clientAddress.getHostAddress() + " disconnected.");

        try {
            channel.close();
        } catch (Exception e) {
            System.out.println("Failed to close client socket channel.");
            e.printStackTrace();
        }
    }

    class HandleMsg implements Runnable {
        SelectionKey sk;
        ByteBuffer bb;

        public HandleMsg(SelectionKey sk, ByteBuffer bb) {
            this.sk = sk;
            this.bb = bb;
        }

        @Override
        public void run() {
            StringBuffer stringBuffer = (StringBuffer) sk.attachment();
            stringBuffer.append(byteBufferToString(bb));
            sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            selector.wakeup();
        }
    }

    public static String byteBufferToString(ByteBuffer buffer) {
        CharBuffer charBuffer = null;
        try {
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer);
            //buffer.flip();
            return charBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        NioServerDemo nioServerDemo = new NioServerDemo();
        try {
            nioServerDemo.startServer();
        } catch (IOException e) {
            System.out.println(" nioServerDemo.startServer() �����쳣");
            e.printStackTrace();
        }
    }
}
