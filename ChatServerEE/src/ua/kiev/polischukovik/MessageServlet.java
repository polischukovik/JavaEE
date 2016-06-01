package ua.kiev.polischukovik;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MessageServlet extends HttpServlet {

	private MessageList msgList = MessageList.getInstance();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException 
	{
		String message = "";
		try(InputStream is = req.getInputStream()){
			message = MessageIO.extractMessage(is);
			is.close();
		}
		catch(IOException e){
			resp.sendRedirect("core/InternalServerError.html");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		Message msg = Message.fromJSON(message);
		if (msg != null){
			msgList.add(msg);
		}
		else{
			resp.sendRedirect("core/BadRequest.html");
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}						
	}
}
