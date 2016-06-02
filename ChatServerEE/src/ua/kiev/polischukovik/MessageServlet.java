package ua.kiev.polischukovik;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MessageServlet extends HttpServlet {

	private MessageList msgList = MessageList.getInstance();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException 
		{		
			
			String type = req.getParameter("type");
			if(type == "" || type == null){
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.sendRedirect("core/BadRequest.html");
				return;
			}
			if(type == "login"){
				
			}
			
			if(type == "exit"){
				
			}
			
			if(type == "query"){			
				String nStr = req.getParameter("from");
				if(nStr == "" || nStr == null){ 
					resp.sendRedirect("core/BadRequest.html");
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
				 /*
				  * Obtain room id if exists
				  */
				int room = 0;
				String roomStr = req.getParameter("room");
				
				try{
					room = (roomStr == "" || roomStr == null) ? 0 : Integer.parseInt(roomStr); 
				}
				catch(NumberFormatException e){
					resp.sendRedirect("core/BadRequest.html");
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}	
				
				String msgJSON = null;
				try{				
					int n = Integer.parseInt(nStr);
					msgJSON = msgList.getMesgListFromRoomJSON(room, n);
				}catch(NumberFormatException e){
					resp.sendRedirect("core/BadRequest.html");
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}	
				
				try{
					if (msgJSON != null) {
						MessageIO.sendMessage(msgJSON, resp.getOutputStream());
					}
				}
				catch(IOException e){
					resp.sendRedirect("core/InternalServerError.html");
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}
			}			
		}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException 
	{
		String message = "";
		try(InputStream is = req.getInputStream()){
			message = MessageIO.formPackage(is);
			is.close();
		}
		catch(IOException e){
			resp.sendRedirect("core/InternalServerError.html");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		String roomStr = req.getParameter("room");
		int room = 0;
		try{
			room = (roomStr == null || roomStr == "") ? 0 : Integer.parseInt(roomStr);
		}catch(NumberFormatException e){
			resp.sendRedirect("core/BadRequest.html");
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		Message msg = Message.fromJSON(message);
		if (msg != null){
			msgList.add(room, msg);
		}
		else{
			resp.sendRedirect("core/BadRequest.html");
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}						
	}
}
