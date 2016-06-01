package ua.kiev.polischukovik;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.servlet.ServletOutputStream;

public class MessageIO {
	public static String extractMessage(InputStream is) throws IOException{
		/*
		 * First int in message is message length
		 */
		byte[] intBuff = new byte[4];
		is.read(intBuff, 0, 4);		
		int messageSize = ByteBuffer.wrap(intBuff).getInt();
		
		byte[] messageBuff = new byte[messageSize];
		is.read(messageBuff, 0, messageSize);
		return new String(messageBuff);
	}

	public static void compressMessage(String json, ServletOutputStream outputStream) {
		// TODO Auto-generated method stub
		
	}

}
