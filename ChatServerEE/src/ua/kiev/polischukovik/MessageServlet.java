package ua.kiev.polischukovik;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MessageServlet extends HttpServlet {
	private static final int MAX_PARAM_LENGTH = 200;
	
    static final String LOGIN = "admin";
    static final String PASS = "admin";

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
		
		Calendar cal = Calendar.getInstance();		
		rooms.getPublicRoomByName("Alex created room").addMessage(new Message(cal.getTime() , "Alex", "Hello dummy"));
		rooms.getPublicRoomByName("Alex created room").addMessage(new Message(cal.getTime() , "Viktor", "You are dummy dummy"));
		
	}
	
	public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    DBHelper.init();	    
	  }
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException 
		{		
			//resp.setContentType("application/json");			
			String type = req.getParameter("type");
			if(!checkParameter(type)){
				returnBadRequest(req, resp);
				return;
			}else{

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
					}else{
						returnBadRequest(req, resp);
						return;
					}					
				}
				
				if(type.equals("login")){
					String op = req.getParameter("operation");
					if(op.equals("exit")){
				        HttpSession session = req.getSession(false);
				        if (session != null){
				        	users.removeUser(users.getUserByName((String) session.getAttribute("user_login")));
				        	session.removeAttribute("user_login");
				        }
				        try {
							req.getRequestDispatcher("index.jsp").forward(req, resp);
						} catch (ServletException e) {
							e.printStackTrace();
						}
				        return;	
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
					}else if(op.equals("queryMsg")){
						String roomName = req.getParameter("name");
						String n = req.getParameter("n");
						if(!(checkParameter(roomName) && checkParameter(n))){
							returnBadRequest(req, resp);return;
						}else{
							Room room = rooms.getPublicRoomByName(roomName);
							if(room == null){
								returnBadRequest(req, resp);return;
							}else{
								int N = -1;
								try{
									N = Integer.valueOf(n);
								}catch(NumberFormatException e){
									System.err.println("Cannot parse int" + n);							
								}
								if(N == -1){
									returnBadRequest(req, resp);return;
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
					}else if(op.equals("queryPrivate")){
						String user = req.getParameter("name");
						if(!(checkParameter(user))){
							returnBadRequest(req, resp);return;
						}else{
							User userObj = users.getUserByName(user);
							if(userObj == null){
								returnBadRequest(req, resp);return;
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
					}else{
						returnBadRequest(req, resp);return;
					}					
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
		String type = req.getParameter("type");
		
		if(!checkParameter(type)){
			returnBadRequest(req, resp);return;
		}else{
			if(type.equals("user")){
				String op = req.getParameter("operation");
				if(op.equals("addUsr")){
					String user = req.getParameter("name");
					if(!(checkParameter(user))){
						returnBadRequest(req, resp);return;
					}else{
						User userObj = users.getUserByName(user);
						if(userObj == null){
							users.addUser(user);
						}else{
							returnBadRequest(req, resp);return;
						}
					}
				}else if(op.equals("remUsr")){
					String user = req.getParameter("name");
					if(!(checkParameter(user))){
						returnBadRequest(req, resp);return;
					}else{
						User userObj = users.getUserByName(user);
						if(userObj == null){
							returnBadRequest(req, resp);return;
						}else{
							users.removeUser(userObj);
						}
					}
				}else if(op.equals("status")){
					String status = req.getParameter("status");
					if(!(checkParameter(status))){					
						returnBadRequest(req, resp);return;
					}else{
						String user = req.getParameter("user");
						if(!(checkParameter(user))){					
							returnBadRequest(req, resp);return;
						}else{
							User userObj = users.getUserByName(user);
							if(userObj == null){
								returnBadRequest(req, resp);return;
							}else{
								try{
									UserStatus statusObj = UserStatus.valueOf(UserStatus.class, status);
									users.setUserStatus(userObj, statusObj);
								}catch(NullPointerException e){
									req.setAttribute("info", "Cannot change status for user");
								}catch(IllegalArgumentException e){
									req.setAttribute("info", "Cannot change status for user");
								}
							}	
						}
					}
				}
				else{
					returnBadRequest(req, resp);return;
				}
			}
			
			if(type.equals("login")){
				String op = req.getParameter("operation");
				if(op.equals("enter")){
					String login = req.getParameter("login");
			        String password = req.getParameter("password");

			        if (DBHelper.checkCredentials(login, password)) {
			            HttpSession session = req.getSession(true);
			            session.setAttribute("user_login", login);
			            users.addUser(login);
			        }			        
			        try {
						req.getRequestDispatcher("index.jsp").forward(req, resp);
					} catch (ServletException e) {
						e.printStackTrace();
					}
			        return;	
				}else if(op.equals("register")){
					String login = req.getParameter("login");
			        String password = req.getParameter("password");

			        if (DBHelper.createAccount(login, password)) {
			        	resp.getWriter().write("Account " + login + " created");
			        }else{
			        	resp.getWriter().write("Cannot create account");
			        }
			        try {
						req.getRequestDispatcher("index.jsp").forward(req, resp);
					} catch (ServletException e) {
						e.printStackTrace();
					}
			        return;	
				}
			}
			
			if(type.equals("rooms")){
				String op = req.getParameter("operation");
				if(op.equals("addPublic")){
					String roomName = req.getParameter("name");
					String initiator = req.getParameter("initiator");					
					if(!(checkParameter(roomName) && checkParameter(initiator))){
						returnBadRequest(req, resp);return;
					}else{
						User initiatorObj = users.getUserByName(initiator);
						if(initiatorObj == null){
							returnBadRequest(req, resp);return;
						}else{
							Room room = rooms.getPublicRoomByName(roomName);
							if(room == null){
								rooms.addPublicRoom(roomName, initiatorObj);
								resp.setStatus(HttpServletResponse.SC_OK);
							}else{
								returnBadRequest(req, resp);
								return;
							}
						}	
					}
				}else if(op.equals("remPublic")){
					String roomName = req.getParameter("name");
					String initiator = req.getParameter("initiator");
					if(!(checkParameter(roomName) && checkParameter(initiator))){
						returnBadRequest(req, resp);return;
					}else{					
						User initiatorObj = users.getUserByName(initiator);					
						if(initiatorObj == null){
							returnBadRequest(req, resp);return;
						}else{
							Room room = rooms.getPublicRoomByName(roomName);
							if(room == null){
								returnBadRequest(req, resp);return;
							}else{
								if(!room.getCreator().equals(initiatorObj)){
									returnBadRequest(req, resp);return;
								}else{
									rooms.removePublicRoom(room);
								}
							}
						}
					}
				}else if(op.equals("addMsg")){
					String roomName = req.getParameter("roomName");
					String message = req.getParameter("message");
					if(!(checkParameter(roomName) && checkParameter(message))){
						returnBadRequest(req, resp);return;
					}else{
						Room room = rooms.getPublicRoomByName(roomName);
						if(room == null){
							returnBadRequest(req, resp);return;
						}else{
							Message messageObj= Message.fromJSON(message);							
							if(messageObj == null){
								returnBadRequest(req, resp);return;
							}else{
								rooms.addRoomsMessages(room, messageObj);
							}									
						}						
					}
				}else if(op.equals("addPrivate")){
					String initiator = req.getParameter("initiator");
					String user = req.getParameter("user");
					if(!(checkParameter(initiator) && checkParameter(user))){
						returnBadRequest(req, resp);return;
					}else{
						User initiatorObj = users.getUserByName(initiator);
						User userObj = users.getUserByName(user);
						if(initiatorObj == null || userObj == null){
							returnBadRequest(req, resp);return;
						}else{
							Room room = rooms.getPrivateRoomByParticipants(initiatorObj, userObj);
							if(room == null){
								rooms.addPrivateRoom(initiatorObj, userObj);
								resp.setStatus(HttpServletResponse.SC_OK);
							}else{
								returnBadRequest(req, resp);return;
							}
						}	
					}
				}else if(op.equals("remPrivate")){
					String initiator = req.getParameter("initiator");
					String user = req.getParameter("user");
					if(!(checkParameter(initiator) && checkParameter(user))){
						returnBadRequest(req, resp);return;
					}else{					
						User initiatorObj = users.getUserByName(initiator);
						User userObj = users.getUserByName(user);
						if(initiatorObj == null || userObj == null){
							returnBadRequest(req, resp);return;
						}else{
							Room room = rooms.getPrivateRoomByParticipants(initiatorObj, userObj);
							if(room == null){
								returnBadRequest(req, resp);return;
							}else{
								rooms.removePublicRoom(room);
							}
						}
					}
				}
				else{
					returnBadRequest(req, resp);return;
				}					
			}
			
			if(checkParameter(type)){
				if(type.equals("login")){
					String op = req.getParameter("operation");
					
					if(op.equals("enter")){
						String login = req.getParameter("login");
				        String password = req.getParameter("password");
				        
				        if (DBHelper.checkCredentials(login, password)) {
				            HttpSession session = req.getSession(true);
				            session.setAttribute("user_login", login);
				            users.addUser(login);
				        }			        
				        resp.sendRedirect("index.jsp");				
					}else if(op.equals("register")){		
						String login = req.getParameter("login");
				        String password = req.getParameter("password");
				        
				        if (DBHelper.createAccount(login, password)) {
				            req.setAttribute("info", "Success");
				        }else{
				        	req.setAttribute("info", "Register failed");
				        }
				        
				        resp.sendRedirect("index.jsp");		
					}
				}	
			}
		}
		
	}
}
