package ua.kiev.polischukovik;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.GsonBuilder;

public class Users {
	private static final Users users = new Users();
	private Map<String,User> list = new HashMap<>();
	
	private Users(){}
	
	public static Users getInstance() {
		return users;
	}
	
	/*	
	 * parameters type: "login", operation: "addUser":	 userName
	 */
	public synchronized void addUser(String name){
		User user = new User(name, UserStatus.ACTIVE);
		list.put(user.getName(), user);
	}
	
	/*	
	 * parameters type: "login", operation: "remUser":	 userName
	 */
	public synchronized void removeUser(User name){		
		list.values().remove(name);
	}
	
	/*	
	 * parameters type: "login", operation: "setStatus":	 userName, status
	 */
	public void setUserStatus(User user, UserStatus status) {
		list.get(user.getName()).setStatus(status);
	}
	
	public User getUserByName(String name){
		return list.get(name);
	}
	
	/*
	 * Returns JSON array of JSON user entries
	 * 	
	 * parameters type: "user", operation: "query": 
	 *
	 */
	public String getVisableUsersJSON(){
		if (list.size() > 0) {
			return new GsonBuilder().setPrettyPrinting().create().toJson(list.values().stream().filter(t -> t.getStatus() != UserStatus.INVISIBLE).toArray());
		}
		else{
			return null;
		}
	}
}
