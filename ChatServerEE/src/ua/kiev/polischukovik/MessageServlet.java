package ua.kiev.polischukovik;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MessageServlet extends HttpServlet {
	private static final int MAX_PARAM_LENGTH = 200;

	private Users users = Users.getInstance();
	private Rooms rooms = Rooms.getInstance();
	
	{
		users.addUser("Alex");
		users.addUser("Maxim");
		users.addUser("Igor");
		users.addUser("Viktor");
		users.addUser("Andrew");
		users.addUser("Oleg");
		
		rooms.addPublicRoom("Alex created room", users.getUserByName("Alex"));
		rooms.addPublicRoom("Selling things", users.getUserByName("Maxim"));
		rooms.addPublicRoom("Hello fellous", users.getUserByName("Viktor"));
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException 
		{		
			resp.setContentType("application/json");
			
			String type = req.getParameter("type");
			if(type.equals("") || type == null){
				returnBadRequest(req, resp);
			}
			
			if(type.equals("user")){
				String op = req.getParameter("operation");
				if(op.equals("query")){
					try(OutputStream os = resp.getOutputStream()){
						MessageIO.sendMessage(users.getVisableUsersJSON(), os);
						resp.setStatus(HttpServletResponse.SC_OK);
						//System.out.println(users.getVisableUsersJSON());
					}catch(IOException e){
						returnInternalError(resp);
					}
				}else if(op.equals("addUsr")){
					String user = req.getParameter("name");
					if(!(checkParameter(user))){
						returnBadRequest(req, resp);
					}else{
						User userObj = users.getUserByName(user);
						if(userObj == null){
							users.addUser(user);
						}else{
							returnBadRequest(req, resp);
						}
					}
				}else if(op.equals("remUsr")){
					String user = req.getParameter("name");
					if(!(checkParameter(user))){
						returnBadRequest(req, resp);
					}else{
						User userObj = users.getUserByName(user);
						if(userObj == null){
							returnBadRequest(req, resp);
						}else{
							users.removeUser(userObj);
						}
					}
				}
				else{
					returnBadRequest(req, resp);
				}					
			}
			
			if(type.equals("rooms")){
				String op = req.getParameter("operation");
				if(op.equals("queryPublic")){
					try(OutputStream os = resp.getOutputStream()){
						MessageIO.sendMessage(rooms.getPublicRoomsJSON(), os);
						resp.setStatus(HttpServletResponse.SC_OK);
					}catch(IOException e){
						returnInternalError(resp);
					}
				}else if(op.equals("addPublic")){
					String roomName = req.getParameter("name");
					String initiator = req.getParameter("initiator");					
					if(!(checkParameter(roomName) && checkParameter(initiator))){
						returnBadRequest(req, resp);
					}else{
						User initiatorObj = users.getUserByName(initiator);
						if(initiatorObj == null){
							returnBadRequest(req, resp);
						}else{
							rooms.addPublicRoom(roomName, initiatorObj);
							resp.setStatus(HttpServletResponse.SC_OK);
						}	
					}
				}else if(op.equals("remPublic")){
					String roomName = req.getParameter("name");
					String initiator = req.getParameter("initiator");
					if(!(checkParameter(roomName) && checkParameter(initiator))){
						returnBadRequest(req, resp);
					}else{					
						User initiatorObj = users.getUserByName(initiator);					
						if(initiatorObj == null){
							returnBadRequest(req, resp);
						}else{
							Room room = rooms.getPublicRoomByName(roomName);
							if(room == null){
								returnBadRequest(req, resp);
							}else{
								if(!room.getCreator().equals(initiatorObj)){
									returnBadRequest(req, resp);
								}else{
									rooms.removePublicRoom(room);
								}
							}
						}
					}
				}else if(op.equals("queryMsg")){
					String roomName = req.getParameter("name");
					String n = req.getParameter("n");
					if(!(checkParameter(roomName) && checkParameter(n))){
						returnBadRequest(req, resp);
					}else{
						Room room = rooms.getPublicRoomByName(roomName);
						if(room == null){
							returnBadRequest(req, resp);
						}else{
							int N = -1;
							try{
								N = Integer.valueOf(n);
							}catch(NumberFormatException e){
								System.err.println("Cannot parse int" + n);							
							}
							if(N == -1){
								returnBadRequest(req, resp);
							}else{
								try(OutputStream os = resp.getOutputStream()){
									MessageIO.sendMessage(room.getMessageJSON(N), os);									
								}
								catch(IOException e){
									returnInternalError(resp);
								}
							}
						}						
					}
				}else if(op.equals("addMsg")){
					String roomName = req.getParameter("roomName");
					String message = req.getParameter("message");
					if(!(checkParameter(roomName) && checkParameter(message))){
						returnBadRequest(req, resp);
					}else{
						Room room = rooms.getPublicRoomByName(roomName);
						if(room == null){
							returnBadRequest(req, resp);
						}else{
							Message messageObj= Message.fromJSON(message);							
							if(messageObj == null){
								returnBadRequest(req, resp);
							}else{
								rooms.addRoomsMessages(room, messageObj);
							}									
						}						
					}
				}else if(op.equals("queryPrivate")){
					String user = req.getParameter("name");
					if(!(checkParameter(user))){
						returnBadRequest(req, resp);
					}else{
						User userObj = users.getUserByName(user);
						if(userObj == null){
							returnBadRequest(req, resp);
						}else{
							try(OutputStream os = resp.getOutputStream()){
								MessageIO.sendMessage(rooms.getPrivateRoomsJSON(userObj), os);	
								resp.setStatus(HttpServletResponse.SC_OK);								
							}
							catch(IOException e){
								returnInternalError(resp);
							}
						}	
					}
				}else if(op.equals("addPrivate")){
					String initiator = req.getParameter("initiator");
					String user = req.getParameter("user");
					if(!(checkParameter(initiator) && checkParameter(user))){
						returnBadRequest(req, resp);
					}else{
						User initiatorObj = users.getUserByName(initiator);
						User userObj = users.getUserByName(user);
						if(initiatorObj == null || userObj == null){
							returnBadRequest(req, resp);
						}else{
							rooms.addPrivateRoom(initiatorObj, userObj);
							resp.setStatus(HttpServletResponse.SC_OK);
						}	
					}
				}
				else if(op.equals("remPrivate")){
					String initiator = req.getParameter("initiator");
					String user = req.getParameter("user");
					if(!(checkParameter(initiator) && checkParameter(user))){
						returnBadRequest(req, resp);
					}else{					
						User initiatorObj = users.getUserByName(initiator);
						User userObj = users.getUserByName(user);
						if(initiatorObj == null || userObj == null){
							returnBadRequest(req, resp);
						}else{
							Room room = rooms.getPrivateRoomByParticipants(initiatorObj, userObj);
							if(room == null){
								returnBadRequest(req, resp);
							}else{
								rooms.removePublicRoom(room);
							}
						}
					}
				}
				else{
					returnBadRequest(req, resp);
				}					
			}		
		}

	private void returnInternalError(HttpServletResponse resp) {
		try {
			resp.sendRedirect("core/InternalServerError.html");
		} catch (IOException e) {
			System.err.println("Cannot redirect page");
		}
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return;
	}

	private void returnBadRequest(HttpServletRequest req, HttpServletResponse resp) {
		
	    try {
	    	resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			resp.sendRedirect("core/BadRequest.html");
			//RequestDispatcher dispatcher = req.getRequestDispatcher(req.toString());
			//dispatcher.forward(req, resp);
		} catch ( IOException e) {
			System.err.println("Dispatcher error");
		}
		return;
	}
	
	private boolean checkParameter(String value){
		return value != null && !value.equals("") && value.length() < MAX_PARAM_LENGTH;
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
			returnInternalError(resp);
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
			//msgList.add(room, msg);
		}
		else{
			resp.sendRedirect("core/BadRequest.html");
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
		}						
	}
}
