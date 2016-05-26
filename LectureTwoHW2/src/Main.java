import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class JSON{
	String name;
	String surname;
	String[] phones;
	String[] sites;
	Address	address;
	@Override
	public String toString() {
		return "JSON [name=" + name + ", surname=" + surname + ", phones=" + Arrays.toString(phones) + ", sites="
				+ Arrays.toString(sites) + ", address=" + address + "]";
	}
	
}

class Address{
	String country;
	String city;
	String street;
	@Override
	public String toString() {
		return "Address [country=" + country + ", city=" + city + ", street=" + street + "]";
	}
	
}

public class Main {
	public static void main(String[] args){
		StringBuilder sb = new StringBuilder();
		try(Scanner in = new Scanner(new FileInputStream(new File("data.json")))){
			while(in.hasNext()){
				sb.append(in.nextLine());
			}
		}catch(Exception e){
			System.out.println("can't read file");
			return;
		}
		
		 Gson gson = new GsonBuilder().create();
	     JSON json = (JSON) gson.fromJson(sb.toString(), JSON.class);
		
		System.out.println(json);
	}

}
