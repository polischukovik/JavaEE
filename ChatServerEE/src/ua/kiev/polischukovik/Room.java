package ua.kiev.polischukovik;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Room implements Serializable {
	
	private String name;
	private String initiator;
	private transient int numberOfParticipants = 0;
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
		initiator = creator.getName();
		numberOfParticipants = participants.size();
		this.isPrivate = false;
	}
	/*
	 * Constructor for PRIVATE room
	 */
	public Room(User initiator, User user) {
		this.name = null;
		participants.add(initiator);
		participants.add(user);
		numberOfParticipants = participants.size();
		this.isPrivate = true;
	}

	public synchronized String getMessageJSON(int n) {
		if(n > list.size()){
			return null;
		}
		return new GsonBuilder().
				setPrettyPrinting().
				create().
				toJson(list.subList(n, list.size()).toArray());		
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
	
	public synchronized User returnOtherone(User user) {
		if(participants.indexOf(user) == 0){
			return participants.get(1);
		}else{
			return participants.get(0);
		}
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((participants == null) ? 0 : participants.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;	
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (participants == null) {
			if (other.participants != null)
				return false;
		} else if (!participants.equals(other.participants))
			return false;
		return true;
	}
	
	
}
