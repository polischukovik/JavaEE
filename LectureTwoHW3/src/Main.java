import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class Main {
	private static String query = "http://query.yahooapis.com/v1/public/yql?format=xml&q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22USDEUR%22,%20%22USDUAH%22)&env=store://datatables.org/alltableswithkeys";
	
	public static void main(String[] args){
		String tmpXML = "tmpFile.xml";
		try {
			SAXFinanceHelper.performRequest(query, tmpXML);
		} catch (IOException e) {
			System.err.println("Cannot obtain responce:");
			e.printStackTrace();
		}
		
		Schema.Result result = new SAXFinanceHelper().parseFinanceXML(new File(tmpXML).getAbsolutePath());
		System.out.println(result);
	}	
}
	
class SAXFinanceHelper extends DefaultHandler {
		private List<String> rateFieldList;
	    private SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
	    private SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
	
		private Schema.Result result;
	    private Schema.Rate currentRate; 	    
	    private String currentElement = "";	    
		
		public Schema.Result parseFinanceXML(String path){
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				spf.setNamespaceAware(true);
				SAXParser sp = spf.newSAXParser();
				
				XMLReader reader = sp.getXMLReader();
				reader.setContentHandler(this);
				reader.parse("file:///" + path);
				return result;
			} catch (IOException  e) {
				System.err.println("File IO exception");
			} catch (ParserConfigurationException | SAXException e) {
				System.err.println("That SAX!:");
			}
			return result;
		}
	    
	    @Override
	    public void startDocument() throws SAXException {
	    	result = new Schema.Result();
	    	rateFieldList =  Arrays.asList(Schema.Rate.class.getDeclaredFields()).stream().map(t -> t.getName()).collect(Collectors.toList());
	    }

	    @Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
	    	currentElement = localName;
	    	SimpleDateFormat attributeDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	    	
 	    	if (currentElement.equals("query") && attributes.getLength() > 0) {
	    		for (int i = 0; i < attributes.getLength(); i++) {
	    			if(attributes.getQName(i).equals("yahoo:created")){
	    				try {
	    					result.queryDate = attributeDate.parse(attributes.getValue(i));
	    					break;
	    				} catch (ParseException e) {
	    					System.err.println("Cannot parse query date: " + attributes.getValue(i));
	    					break;
	    				}
	    			}
	    		}
	    	}
	    	
	    	/*
	    	 * New rate element -- if tag name matches the name of target class(case insensitive) 
	    	 */
	    	if(currentElement.equals(Schema.Rate.class.getSimpleName().toLowerCase()) && currentRate == null){
	    		if(currentRate == null){
	    			currentRate = new Schema.Rate();	    			
	    			try {
	    				Field id = Schema.Rate.class.getDeclaredField("id");
		    			id.setAccessible(true);
						id.set(currentRate, attributes.getValue("id"));
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
						System.err.println("Cannot set field");
						e.printStackTrace();
					}
	    		}else{
	    			System.err.println(String.format("Exception while parsing: tag %s is not closed", currentElement));
	    			return;
	    		}
	    		currentElement = "";
	    	}else{
	    		/*
	    		 * bypass element if it has not corresponded declared field in class
	    		 */	    		
	    		if(!rateFieldList.contains(currentElement.toLowerCase())){
	    			currentElement = "";
	    		}	    		
	    	}	    	
		}
	    
	    @Override
		public void characters(char[] ch, int start, int length) throws SAXException {
	    	/*
			 * No target elements for text -- bypass
			 */
	    	if(currentElement.equals("")){
				return;
			}
	    	
	    	for(Field f : Schema.Rate.class.getDeclaredFields()){
	    		if(f.getName().toLowerCase().equals(currentElement.toLowerCase())){
	    			try {
	    				Annotation anotation = f.getAnnotation(DataFormat.class);
	    				f.setAccessible(true);
	    				if(anotation == null){
	    					f.set(currentRate, new String(ch, start, length));
							break;
	    				}
	    				else if(((DataFormat) anotation).dataFormat().equals("date")){
	    					f.set(currentRate, dateFormat.parse(new String(ch, start, length)));
							break;
	    				}
	    				else if(((DataFormat) anotation).dataFormat().equals("time")){
	    					f.set(currentRate, timeFormat.parse(new String(ch, start, length)));
							break;
	    				}	
	    				else if(((DataFormat) anotation).dataFormat().equals("double")){
	    					f.set(currentRate, Double.valueOf(new String(ch, start, length)));
							break;
	    				}	
					} catch (IllegalArgumentException | IllegalAccessException e) {
						System.err.println("Cannot set value: \n" + currentElement + "\n" + new String(ch, start, length) + e);
					} catch (ParseException e) {
						System.err.println("Cannot set value: \n" + currentElement + "\n" + new String(ch, start, length) + e);
					}
	    		}
	    	}	    	
		} 
	    
	    @Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
	    	if(localName.equals(Schema.Rate.class.getSimpleName().toLowerCase())){
	    		if(currentRate != null){
	    			result.add(currentRate);
	    			currentRate = null;
	    		}else{
	    			throw new SAXException(String.format("Exception while parsing: tag %s is not opened", currentElement));
	    		}
	    	}
		}
	    
	    public static void performRequest(String urlStr, String tmpFile) throws IOException {
			URL url = new URL(urlStr);		
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			StringBuilder sb = new StringBuilder();
			try{	
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				char[] buff = new char[10_000_000];
				int cnt = 0;
				while(true){
					if((cnt = in.read(buff)) > 0){
						sb.append(new String(buff, 0, cnt));
					}else{
						break;
					}
				}
			}finally {
		            connection.disconnect();
		    }
			
			File file = new File(tmpFile);
			if(!file.exists()){
				file.createNewFile();
			}
	        try(PrintWriter pw = new PrintWriter(new FileOutputStream(file))){
	        	pw.println(sb.toString());
	        }
	    }
}

