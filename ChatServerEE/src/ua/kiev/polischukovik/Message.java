package ua.kiev.polischukovik;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date date;
	private String from;
	private String text;
	
	public Message(Date date, String from, String text) {
		super();
		this.date = date;
		this.from = from;
		this.text = text;
	}

	public String toJSON() {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}
	
	public static Message fromJSON(String s) {
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() { 
			   @Override
			   public Date deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
				      return new Date(arg0.getAsJsonPrimitive().getAsLong()); 
				   }
			
				}).create();
		return gson.fromJson(s, Message.class);
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append("[").append(date.toString())
				.append(", From: ").append(from).append("] ").append(text).toString();
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}


//public int send(String url) throws IOException {
//	URL obj = new URL(url);
//	HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
//	
//	conn.setRequestMethod("POST");
//	conn.setDoOutput(true);
//
//	OutputStream os = conn.getOutputStream();
//	try {
//		String json = toJSON();
//		os.write(json.getBytes());
//		
//		return conn.getResponseCode();
//	} finally {
//		os.close();
//	}
//}
