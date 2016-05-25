
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface SaveTo{
	String path();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Saver{
}

@SaveTo(path="file.txt")
class TextContainer{
	String text;
	
	public TextContainer() {
		 text = "testtext";
	}
	
	@Saver
	public void saver(String path, String text){
		File file = new File(path);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Cannot create file");
			}
		}
		try(PrintStream out = new PrintStream(new FileOutputStream(file))){
			out.println(text);
		} catch (FileNotFoundException e) {
			System.out.println("Cannot open file");
		}
	}
}

public class Main {	
	public static void main(String[] args) {
		TextContainer o = new TextContainer();
		Class<?> cls = o.getClass();
		String path = cls.getAnnotation(SaveTo.class).path();
		Field textField = null;
		Method saverMethod = null;
		for(Field f : cls.getDeclaredFields()){
			f.setAccessible(true);
			if(f.getType().getName().equals("java.lang.String")){
				textField = f;
				break;
			}
		}
		for(Method m : cls.getDeclaredMethods()){
			m.setAccessible(true);
			if(m.isAnnotationPresent(Saver.class)){
				saverMethod = m;
				break;
			}
		}
		
		if(textField != null && saverMethod != null && path != null){
			try {
				saverMethod.invoke(o, path, textField.get(o));
			} catch (IllegalAccessException e) {
				System.out.println("Method is private");
			} catch (IllegalArgumentException e) {
				System.out.println("Wrong nunmber or type of arguments");
			} catch (InvocationTargetException e) {
				System.out.println(e);
			}
		}else{
			System.out.println((textField == null ? "No text in textfield or no textfield\n" : "") + (saverMethod == null ? "No saver method found\n" : "") + (path == null ? "Cannnot detect saver path\n" : ""));
		}
		
		
	}
}