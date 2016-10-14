package ch8;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileCopy {
	public static void main(String[] args ) throws IOException {
		FileInputStream fin = new FileInputStream("E:\\abc.txt");
		FileOutputStream  fout = new FileOutputStream("E:\\abc1.txt");
		FileChannel fic = fin.getChannel() ;
		FileChannel foc = fout.getChannel();
		ByteBuffer buff = ByteBuffer.allocate(1024);
		while(true) {
			buff.clear();
			int len = fic.read(buff);
			if (len == -1) break;
			buff.flip();
			foc.write(buff);			
		}
		fic.close();
		foc.close();
		fin.close();
		fout.close();
	}

}
