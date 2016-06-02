package ua.kiev.polischukovik;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MessageList {
	
	private static final MessageList msgList = new MessageList();

	private final List<List<Message>> list = new ArrayList<List<Message>>();
	
	public static MessageList getInstance() {
		return msgList;
	}
  
	private MessageList() {
		list.add(new ArrayList<Message>());
		System.out.println("Default message list initialized");
	}
	
	public synchronized void add(int room, Message m) {
		try{
			list.get(room).add(m);
		}
		catch(NullPointerException e){
			System.err.println("Cannot add message to room: " + room + ". Room does not exist");
		}
	}
	
	/*
	 * Gets JSON message list since n
	 */
	public synchronized String getMessageJSON(int room, int n) {
		List<Message> res = list.get(room).subList(n, list.get(room).size());
		
		if (res.size() > 0) {
			Gson gson = new GsonBuilder().create();
			return gson.toJson(res.toArray());
		} else
			return null;
	}


	public String getMesgListFromRoomJSON(int room, int n) {
		// TODO Auto-generated method stub
		return null;
	}
}
