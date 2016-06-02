package ua.kiev.polischukovik;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;

public class Rooms {
	
	private static final Rooms rooms = new Rooms();
	private Map<String,Room> list = new HashMap<>();
	
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
			return new GsonBuilder().create().toJson(list.values().stream().filter(t -> !t.isPrivate()).toArray());
		} else{
			return null;
		}
	}	
	
	/*
	 * parameters type: "room", operation: "addPublic": name, initiator	 
	 */
	public synchronized void addPublicRoom(String name, User initiator){
		Room room = new Room(name, initiator);
		list.put(room.getName(), room); //add initiator the one who can delete room
	}
	
	/*
	 * parameters type: "room", operation: "remPublic": name, creator	 
	 */
	public synchronized void removePublicRoom(String name){
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
		list.put(room.getName(), room);
	}
	
	/*
	 * parameters type: "room", operation: "remPublic": userName, room
	 *  +getPublicRoomCreator(String room)
	 */
	public synchronized void deletePrivateRoom(User you, User anotherone){
		Room room = list.get(new Room(you, anotherone).getName());
		if(room == null){
			return;
		}
		room.deleteParticipant(you);
		if(room.getNumberOfParticipants() == 0){
			list.remove(room);
		}
	}	
	
	/*
	 * Gets JSON room list
	 * parameters type: "room", operation: "queryPrivate": userName	 
	 */
	public synchronized String getPrivateRoomsJSON(User user) {
		if (list.size() > 0) {
			return new GsonBuilder().create().toJson(list.values().stream().filter(t -> t.containsParticipant(user)).toArray());
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
		Room roomObj = list.get(room);
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
	public synchronized void addRoomsMessages(String room, Message message) {
		Room roomObj = list.get(room);
		if (roomObj != null) {
			roomObj.addMessage(message);
		}
	}
	
	/*
	 * MISC
	 */
	public User getPublicRoomCreator(String room){
		Room roomObj = list.get(room);
		if(roomObj != null ){
			return roomObj.getCreator();
		}
		return null;
	}

}
