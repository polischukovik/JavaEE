import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

	public static void main(String[] args){
		
		Container inner = new Container(6,5,3.5,"Hello",false,null,false);
		Container c = new Container(5,3, 4.5, "Test", true, inner, true);
		SerializeHelper.Save(new String[]{SerializeHelper.serialize(c)}, "data_file.dat");
		
		for(Object o : SerializeHelper.Load("data_file.dat")){
			System.out.println((Container) o);
		}		
	}
		
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Save {
}

class Container{
	@Save
	private int vInt;
	
	private int vNoserialInt;
	@Save
	private double vDouble; 
	@Save
	private String vString;
	@Save
	private boolean vBoolean;
	@Save
	private Container oContainer;
	@Save
	private Boolean vOBoolean;
	
	public Container(){
		
	}
	
	public Container(int v_int, int v_noserial_int, double v_double, String v_string, boolean v_boolean, Container o_container, Boolean v_o_boolean) {
		super();
		this.vInt = v_int;
		this.vInt = v_noserial_int;
		this.vDouble = v_double;
		this.vString = v_string;
		this.vBoolean = v_boolean;
		this.oContainer = o_container;
		this.vOBoolean = v_o_boolean;
	}

	@Override
	public String toString() {
		return "Container [vInt=" + vInt + ", vNoserialInt=" + vNoserialInt + ", vDouble=" + vDouble + ", vString="
				+ vString + ", vBoolean=" + vBoolean + ", oContainer=" + oContainer + ", vOBoolean=" + vOBoolean + "]";
	}

}

class SerializeHelper{
	//should not appear in String values
	final static String OBJECT_CONTAINER_BEGIN = "[";
	final static String OBJECT_CONTAINER_END = "]";
	final static String TYPE_CONTAINER_BEGIN = "(";
	final static String TYPE_CONTAINER_END = ")";
	final static String FIELD_SEPARATOR = ";"; 
	
	public final static Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
	static {
	    map.put(boolean.class, Boolean.class);
	    map.put(byte.class, Byte.class);
	    map.put(short.class, Short.class);
	    map.put(char.class, Character.class);
	    map.put(int.class, Integer.class);
	    map.put(long.class, Long.class);
	    map.put(float.class, Float.class);
	    map.put(double.class, Double.class);
	}
	
	static void Save(String[] s, String path){
		File file = new File(path);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Cant create new file: " + e.getMessage());
				return;
			}
		}
		
		try(PrintStream out = new PrintStream(new FileOutputStream(file))) {
			for(String str : s){
				out.println(str);
			}
		} catch (Exception e) {
			System.out.println("Cannot write to file: " + e.getMessage());
			return;
		}		
	}
	
	static ArrayList<Object> Load(String path){
		File file = new File(path);
		if(!file.exists()){
			System.out.println("Cannot find file: " + path);
			return null;
		}
		ArrayList<Object> collection = new ArrayList<>();
		try(Scanner in = new Scanner(new FileInputStream(file))) {
			while(in.hasNext()){
				collection.add(deserialize(in.nextLine()));
			}			
		} catch (Exception e) {
			System.out.println("Cannot read file: " + e.getMessage());
			return null;
		}
		return collection;
		
	}
	
	static String[] split(String s){
		String[] res = new String[0];
		int fieldBegins = 0, fieldEnds;
		boolean loop = true;
		while(loop){
			fieldEnds = s.indexOf(FIELD_SEPARATOR, fieldBegins + 1);
			String sub = s.substring(fieldBegins == 0 ? fieldBegins : fieldBegins + 1, fieldEnds == -1 ? s.length() : fieldEnds);
			if(sub.contains(OBJECT_CONTAINER_BEGIN)){				
				fieldEnds = s.indexOf(OBJECT_CONTAINER_END, s.indexOf(OBJECT_CONTAINER_BEGIN, fieldBegins))+1;
				sub = s.substring(fieldBegins + 1, fieldEnds);
			}
			if(s.indexOf(FIELD_SEPARATOR, fieldBegins + 1) == -1){
				loop = false;
			}
			fieldBegins = fieldEnds;
			String[] tmp = new String[res.length+1];
			System.arraycopy(res, 0, tmp, 0, res.length);
			tmp[tmp.length - 1] = sub;
			res = tmp;			
		}
		return res;
	}

	//Container[v_int(int)=3;v_double(double)=4.5;v_string(java.lang.String)=Test;v_boolean(boolean)=true;o_container(Container)=Container[...];v_o_boolean(java.lang.Boolean)=true]
	public static String serialize(Object o) {
		if (o == null){
			return "null"; //recurtion exit point
		}
		String objectContent = "";
		
		for(Field field : o.getClass().getDeclaredFields()){
			if(!field.isAnnotationPresent(Save.class)){
				continue;
			}
			field.setAccessible(true);
			String fieldEntity = field.getName();
			if(field.getType().isPrimitive()){
				fieldEntity += TYPE_CONTAINER_BEGIN + map.get(field.getType()).getName() + TYPE_CONTAINER_END + "=";;
			}else{
				fieldEntity += TYPE_CONTAINER_BEGIN + field.getType().getName() + TYPE_CONTAINER_END + "=";;
			}
			try {
				if(field.getType().isPrimitive()){	//is primitive	
					
						fieldEntity += field.get(o);
					
					if(objectContent.length() != 0){
						objectContent += ";";
					}
					objectContent += fieldEntity;
				}else{	//not primitive: Wrap-Class or Custom Class
					if(objectContent.length() != 0){
						objectContent += FIELD_SEPARATOR;
					}
					//if(Class.forName(field.getType().getName()).getName().equals("java.lang."+field.getType().getSimpleName())){
					if(map.values().contains(field.getType()) || field.getType().getName() == "java.lang.String"){
						objectContent += fieldEntity + field.get(o);
					}else{
						objectContent += fieldEntity + serialize(field.get(o));
					}				
				}
			} catch (IllegalArgumentException e) {
				System.out.println("Wrong number or type of arguments");
				return "";
			} catch (IllegalAccessException e) {
				System.out.println("Field is not accessible");
				return "";
			}
		}				
		return o.getClass().getName() + OBJECT_CONTAINER_BEGIN + objectContent + OBJECT_CONTAINER_END;
	}	
	
	public static Object deserialize(String s) {		
		if (s.equals("null")){
			return null; //recurtion exit point
		}
		String className = s.substring(0, s.indexOf(OBJECT_CONTAINER_BEGIN));
		String objContent = s.substring(s.indexOf(OBJECT_CONTAINER_BEGIN) + 1, s.lastIndexOf(OBJECT_CONTAINER_END));
		
		Class cls;
		try {
			cls = Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to find class " + s);
			return null;
		}
		Object o = null;
		try {
					
			o = cls.newInstance();		
		
			for(String sRecord : split(objContent)){			
				String type = sRecord.substring(0,sRecord.indexOf('='));
				String value = sRecord.substring(sRecord.indexOf('=') + 1, sRecord.length());
				String fieldType = type.substring(type.indexOf(TYPE_CONTAINER_BEGIN) + 1, type.lastIndexOf(TYPE_CONTAINER_END));
				Field field = cls.getDeclaredField(type.substring(0, type.indexOf(TYPE_CONTAINER_BEGIN)));
				
				boolean access  = field.isAccessible();			
				field.setAccessible(true);
				if(map.values().contains(Class.forName(fieldType))){
					field.set(o, Class.forName(fieldType).getDeclaredMethod("valueOf",java.lang.String.class).invoke(null, value));
				}else{			
					if(field.getType().getName() == "java.lang.String"){
						field.set(o, value);
					}else{
						field.set(o, deserialize(value));
					}
				}
				field.setAccessible(access);			
			}
		} catch (InstantiationException | IllegalAccessException e) {
			System.out.println("Instantiation exception occured: cannot create object");
		} catch (IllegalArgumentException e) {
			System.out.println("Wrong number or type of arguments");
		} catch (InvocationTargetException e) {
			System.out.println("Cannot invoke method valueOf()");
		} catch (NoSuchMethodException e) {
			System.out.println("Method valueOf() not found");
		} catch (SecurityException e) {
			System.out.println("Security exception occured");
		} catch (ClassNotFoundException e) {
			System.out.println("Cannot retrieve class: class not found");
		} catch (NoSuchFieldException e) {
			System.out.println("Cannot find field");
		}
		return o;
	}	
}
