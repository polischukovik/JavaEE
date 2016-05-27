import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
		String output = "";
		try {
			SAXFinanceHelper.performRequest(query,"");
		} catch (IOException e) {
			System.err.println("Cannot obtain responce:");
			e.printStackTrace();
		}
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		
		try {
			SAXParser sp = spf.newSAXParser();
			
			XMLReader reader = sp.getXMLReader();
			SAXFinanceHelper financeHandler = new SAXFinanceHelper();
			reader.setContentHandler(financeHandler);
			reader.parse("file:///" + new File("tmp.xml").getAbsolutePath());
		} catch (IOException  e) {
			System.err.println("File IO exception");
			return;
		} catch (ParserConfigurationException | SAXException e) {
			System.err.println("That SAX!:");
			return;
		}
	}	
}
	
class SAXFinanceHelper extends DefaultHandler {
	    
	    public Result result;
	    private Rate currentRate; 
	    private String currentElement = "";
	    private String currentElementValue = "";
	    
	    
	    class Result{
	    	Date queryDate;
	    	List<Rate> rates;

			public Result() {
				this.rates = new ArrayList<>();
			}	    	
			
			public void add(Rate r){
				rates.add(r);
			}

			@Override
			public String toString() {
				return "Result queryDate=" + queryDate + ", rates: " + rates;
			}
	    }
	    
	    class Rate{
	    	String id;
	    	String name;
	    	Double rate;
	    	Date date;
	    	Date time;
	    	Double ask;
	    	Double bid;
	    	
	    	public Rate(){}
	    	
			public Rate(String id, String name, Double rate, Date date, Date time, Double ask, Double bid) {
				this.id = id;
				this.name = name;
				this.rate = rate;
				this.date = date;
				this.time = time;
				this.ask = ask;
				this.bid = bid;
			}
			public Rate(String id, String name, String rate, String date, String time, String ask, String bid) throws ParseException {
				SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
				SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
				this.id = id;
				this.name = name;
				this.rate = Double.valueOf(rate);
				this.date = dateFormat.parse(date);
				this.time = timeFormat.parse(time);
				this.ask = Double.valueOf(ask);
				this.bid = Double.valueOf(bid);
			}

			@Override
			public String toString() {
				return "Rate [id=" + id + ", name=" + name + ", rate=" + rate + ", date=" + date + ", time=" + time
						+ ", ask=" + ask + ", bid=" + bid + "]";
			}
			
			
	    }
	    
	    @Override
	    public void startDocument() throws SAXException {
	    	result = new Result();
	    }

	    @Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
	    	currentElement = localName;
	    	SimpleDateFormat attributeDate = new SimpleDateFormat("yyyy-MM-ddTHH:MM:ss");
	    	
	    	
	    	if (attributes.getLength() > 0) {
	    		  for (int i = 0; i < attributes.getLength(); i++) {
	    		    if(attributes.getQName(i).equals("yahoo:created")){
	    		    	try {
							result.queryDate = attributeDate.parse(attributes.getValue(i).replaceAll("Z", ""));
						} catch (ParseException e) {
							System.err.println("Cannot parse query date: " + attributes.getValue(i));
							break;
						}
	    		    }
	    		    System.out.println(" value: " + attributes.getValue(i));  
	    		  }
	    		}
	    	
	    	/*
	    	 * New rate element -- if tag name matches the name of target class(case insensitive) 
	    	 */
	    	if(currentElement.toLowerCase().equals(Rate.class.getName().toLowerCase())){
	    		if(currentRate == null){
	    			currentRate = new Rate();
	    			currentRate.id = attributes.getValue("id");
	    		}else{
	    			System.err.println(String.format("Exception while parsing: tag %s is not closed", currentElement));
	    			return;
	    		}
	    		currentElement = "";
	    	}else{
	    		/*
	    		 * bypass element if it has not corresponded declared field in class
	    		 */
	    		if(!Arrays.asList(Rate.class.getDeclaredFields()).stream().map(t -> t.getName()).collect(Collectors.toList()).contains(currentElement)){
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
	    	
	    	for(Field f : Rate.class.getFields()){
	    		if(f.getName().toLowerCase().equals(currentElement.toLowerCase())){
	    			try {
	    				f.setAccessible(true);
						f.set(currentRate, new String(ch, start, length));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						System.err.println("Cannot set value: \n" + currentElement + "\n" + currentElementValue);
					}
	    		}
	    	}	    	
		} 
	    
	    @Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
	    	if(currentElement.equals(Rate.class.getName().toLowerCase())){
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
			
			File file = new File("tmp.xml");
			if(!file.exists()){
				file.createNewFile();
			}
	        try(PrintWriter pw = new PrintWriter(new FileOutputStream(file))){
	        	pw.write(pw.toString());
	        }
	    }
}

