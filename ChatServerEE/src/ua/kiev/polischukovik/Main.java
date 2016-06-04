package ua.kiev.polischukovik;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

public class Main {
	private static Users users = Users.getInstance();
	private static Rooms rooms = Rooms.getInstance();
	

	public static void main(String[] args) {
		String req = null; String resp = null;
		
		users.addUser("Alex");
		users.addUser("Maxim");
		users.addUser("Igor");
		users.addUser("Viktor");
		users.addUser("Andrew");
		users.addUser("Oleg");
		
		rooms.addPublicRoom("Alex created room", users.getUserByName("Alex"));
		rooms.addPublicRoom("Selling things", users.getUserByName("Maxim"));
		rooms.addPublicRoom("Hello fellous", users.getUserByName("Viktor"));
		
		Message m = new Message(Calendar.getInstance().getTime(), "Alex", "Hi thhrtrffffffffffff text ololol-lol= lolo!!");
		rooms.addRoomsMessages(rooms.getPublicRoomByName("Hello fellous"), m);
		Message ma = new Message(Calendar.getInstance().getTime(), "Igor", "WWhat a hrll");
		rooms.addRoomsMessages(rooms.getPublicRoomByName("Hello fellous"), ma);
		Message ms = new Message(Calendar.getInstance().getTime(), "Viktor", "Kon kon kon puup pup up");
		rooms.addRoomsMessages(rooms.getPublicRoomByName("Hello fellous"), ms);
		Message md = new Message(Calendar.getInstance().getTime(), "Oleg", "OOooooOOoooooooOooOooooOoOOOOooOOo");
		rooms.addRoomsMessages(rooms.getPublicRoomByName("Hello fellous"), md);
		
		String initiator = "Alex";
		String user = "Igor";
		if(!(checkParameter(initiator) && checkParameter(user))){
			returnBadRequest(req, resp);
		}else{
			User initiatorObj = users.getUserByName(initiator);
			User userObj = users.getUserByName(user);
			if(initiatorObj == null || userObj == null){
				returnBadRequest(req, resp);
			}else{
				rooms.addPrivateRoom(initiatorObj, userObj);
			}	
		}
		
		initiator = "Alex";
		user = "Igor";
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
		
		String user1 = "Max";
		if(!(checkParameter(user1))){
			returnBadRequest(req, resp);
		}else{
			User userObj = users.getUserByName(user1);
			if(userObj == null){
				returnBadRequest(req, resp);
			}else{
				System.out.println(rooms.getPrivateRoomsJSON(userObj));
			}	
		}	
		
		
				
	}
	
	private static boolean checkParameter(String value){
		return value != null && !value.equals("") && value.length() < 200;
	}
	
	public static void returnBadRequest(String req, String resp){
		System.out.println("error");
	}
	public static void returnInternalError(String req, String resp){
		System.out.println("Internal error");
	}
	

}
