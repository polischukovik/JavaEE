import java.awt.List;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchFieldException, SecurityException, InvocationTargetException, NoSuchMethodException {
		
		Container inner = new Container(6,5,3.5,"Hello",false,null,false);
		Container c = new Container(5,3, 4.5, "Test", true, inner, true);
		
		SerializeHelper.Save(Arrays.asList(new Container[]{c}), "data_file.dat");		
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
	private int v_int;
	
	private int v_noserial_int;
	@Save
	private double v_double;
	@Save
	private String v_string;
	@Save
	private boolean v_boolean;
	@Save
	private Container o_container;
	@Save
	private Boolean v_o_boolean;
	
	public Container(){
		
	}
	
	public Container(int v_int, int v_noserial_int, double v_double, String v_string, boolean v_boolean, Container o_container, Boolean v_o_boolean) {
		super();
		this.v_int = v_int;
		this.v_int = v_noserial_int;
		this.v_double = v_double;
		this.v_string = v_string;
		this.v_boolean = v_boolean;
		this.o_container = o_container;
		this.v_o_boolean = v_o_boolean;
	}

	@Override
	public String toString() {
		return "Container [v_int=" + v_int + ", v_noserial_int=" + v_noserial_int + ", v_double=" + v_double
				+ ", v_string=" + v_string + ", v_boolean=" + v_boolean + ", o_container=" + o_container
				+ ", v_o_boolean=" + v_o_boolean + "]";
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
	
	static <T> void Save(Collection<T> obj, String path){
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
			for(Object o : obj){
				out.println(serialize(o.getClass().cast(o)));
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
	public static String serialize(Object o) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException{
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
		}				
		return o.getClass().getName() + OBJECT_CONTAINER_BEGIN + objectContent + OBJECT_CONTAINER_END;
	}	
	
	public static Object deserialize(String s) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchFieldException, SecurityException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException{		
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
		Object o = cls.newInstance();
		
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
		return o;
	}	
}
