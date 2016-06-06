package ua.kiev.polischukovik;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.servlet.ServletOutputStream;

import com.sun.org.apache.xalan.internal.xsltc.trax.OutputSettings;

public class MessageIO {
	public static String formPackage(InputStream inputStream) throws IOException{
		/*
		 * First int in message is message length
		 */
//		byte[] intBuff = new byte[Integer.BYTES];
//		inputStream.read(intBuff, 0, Integer.BYTES);		
//		int messageSize = ByteBuffer.wrap(intBuff).getInt();
//		
		byte[] messageBuff = new byte[100_000];
		inputStream.read(messageBuff, 0, 100_000);
		return new String(messageBuff);
	}

	public static void sendMessage(String json, OutputStream outputStream) throws IOException {
		/*
		 * create header containing size of transaction
		 */
		//outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(json.length()).array());
		outputStream.write(json.getBytes());
	}

}
