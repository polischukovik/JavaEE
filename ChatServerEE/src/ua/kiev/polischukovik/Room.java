package ua.kiev.polischukovik;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Room implements Serializable {
	
	private String name;
	private int numberOfParticipants = 0;
	private transient boolean isPrivate;
	private transient List<User> participants = new ArrayList<>();
	private transient List<Message> list = new ArrayList<>();
	
	/*
	 * Constructor for public room
	 */
	public Room(String name, User creator) {
		super();
		this.name = name;
		participants.add(creator);
		numberOfParticipants = participants.size();
		this.isPrivate = false;
	}
	/*
	 * Constructor for PRIVATE room
	 */
	public Room(User initiator, User adressee) {
		this.name = initiator.getName() + "#" + adressee.getName();
		participants.add(initiator);
		participants.add(adressee);
		numberOfParticipants = participants.size();
		this.isPrivate = true;
	}

	public synchronized String getMessageJSON(int n) {
		List<Message> res = list.subList(n, list.size());
		
		if (res.size() > 0) {
			Gson gson = new GsonBuilder().create();
			return gson.toJson(res.toArray());
		} else
			return null;
	}
	
	public synchronized void deleteParticipant(User user) {
		participants.remove(user);
		numberOfParticipants = participants.size();
	}
	
	/*
	 * parameters type: "room", operation: "queryPrivate": userName	 
	 */
	public synchronized boolean containsParticipant(User user) {
		return participants.contains(user);
	}
	
	public int getNumberOfParticipants() {
		return numberOfParticipants;
	}
	
	public synchronized void addMessage(Message m) {
		list.add(m);
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}

	/*
	 * Return JSON rooms name list
	 */
	public String getRoomJSON() {
		return new GsonBuilder().create().toJson(this);
	}	

	@Override
	public String toString() {
		return "Room [name=" + name + ", isPrivate=" + isPrivate + ", participants=" + participants.stream().map(t -> t.getName()).collect(Collectors.joining(",")) + ", numberOfParticipants=" + numberOfParticipants + "]";
	}

	public String getName() { 
		return name;
	}
	public User getCreator() {
		if(isPrivate){
			return null;
		}
		return participants.get(0);
	}
	
	

}
