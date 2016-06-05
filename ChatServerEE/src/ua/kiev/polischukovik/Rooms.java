package ua.kiev.polischukovik;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;

public class Rooms {
	
	private static final Rooms rooms = new Rooms();
	private List<Room> list = new ArrayList<>();
	
	private Rooms(){}
	
	public static Rooms getInstance() {
		return rooms;
	}
	/*
	 * PUBLIC ROOM module
	 */
	/*
	 * Gets JSON room list
	 * parameters type: "room", operation: "queryPublic": 	 
	 */
	public synchronized String getPublicRoomsJSON() {
		if (list.size() > 0) {
			return new GsonBuilder().setPrettyPrinting().create().toJson(list.stream().filter(t -> !t.isPrivate()).toArray());
		} else{
			return null;
		}
	}	

	/*
	 * parameters type: "room", operation: "addPublic": name, initiator	 
	 */
	public synchronized void addPublicRoom(String name, User initiator){
		Room room = new Room(name, initiator);
		list.add(room); //add initiator the one who can delete room
	}
	
	/*
	 * parameters type: "room", operation: "remPublic": name, creator	 
	 */
	public synchronized void removePublicRoom(Room name){
		list.remove(name);
	}
	
	/*
	 * PRIVATE ROOM module
	 */
	
	/*
	 * parameters type: "room", operation: "addPrivate": initiator, adressee
	 */
	public synchronized void addPrivateRoom(User initiator, User adressee){
		Room room = new Room(initiator, adressee);
		list.add(room);
	}
	
	/*
	 * parameters type: "room", operation: "remPublic": userName, room
	 *  +getPublicRoomCreator(String room)
	 */
	public synchronized void removePrivate(User you, User anotherone){
		list.remove(new Room(you, anotherone));
	}		
	
	/*
	 * Gets JSON room list
	 * parameters type: "room", operation: "queryPrivate": userName	 
	 */
	public synchronized String getPrivateRoomsJSON(User user) {
		if (list.size() > 0) {
			List<User> u = list.stream()
							.filter(t -> t.isPrivate() && t.containsParticipant(user))
							.map(t -> t.returnOtherone(user))
							.collect(Collectors.toList());
			return new GsonBuilder()
							.setPrettyPrinting()
							.create()
							.toJson(u);
		} else{
			return null;
		}
	}
	
	/*
	 * CHAT module
	 */
	/*
	 * Gets JSON message list from room since n
	 * parameters type: "room", operation: "queryMsg": roomName, n	 
	 */
	public synchronized String getRoomsMessagesJSON(String room, int n) {
		Room roomObj = getPublicRoomByName(room);
		if (roomObj != null) {
			return roomObj.getMessageJSON(n);
		} else{
			return null;
		}
	}
	
	/*
	 * Gets JSON message list from room since n
	 * parameters type: "room", operation: "addMsg": roomName, MsgJSON	 
	 */
	public synchronized void addRoomsMessages(Room room, Message message) {
		room.addMessage(message);
	}
	
	/*
	 * MISC
	 */
	public User getPublicRoomCreator(String room){
		Room roomObj = getPublicRoomByName(room);
		if(roomObj != null ){
			return roomObj.getCreator();
		}
		return null;
	}
	
	public Room getPublicRoomByName(String name){
		Room roomObj = null;		
		List<Room> rooms = list.stream()
				.filter(t -> !t.isPrivate() && t.getName().equals(name))
				.collect(Collectors.toList());
		if(rooms.size() > 0){
			roomObj = rooms.get(0);
		}			
		return roomObj;
	}
	
	public Room getPrivateRoomByParticipants(User initiator, User user){
		int index = list.indexOf(new Room(initiator, user));
		int indexR = list.indexOf(new Room(user, initiator));
		if(index != -1 ){
			return list.get(index);
		}else if(indexR != -1){
			return list.get(indexR);
		}else{
			return null;
		}
	}
}
