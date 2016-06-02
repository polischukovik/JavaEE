package ua.kiev.polischukovik;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MessageServlet extends HttpServlet {
	
	private MessageList msgList = MessageList.getInstance();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException 
	{
		String fromStr = req.getParameter("from");
		int from = Integer.parseInt(fromStr);
		
		String json = msgList.toJSON(from);
		if (json != null) {
			OutputStream os = resp.getOutputStream();
			os.write(json.getBytes());
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException 
	{
		InputStream is = req.getInputStream();
		byte[] buf = new byte[req.getContentLength()];
		is.read(buf);
		
		Message msg = Message.fromJSON(new String(buf));
		if (msg != null)
			msgList.add(msg);
		else
			resp.setStatus(400); // Bad request
	}
}
