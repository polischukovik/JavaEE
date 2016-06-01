import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class Main {

	public static void main(String[] args) throws ParseException {		
		Date today = new SimpleDateFormat("DD.MM.yyyy HH:mm:ss").parse("19.01.2013 12:33:14");
		
		Trains trains = TrainXMLHelper.importXML("source.xml");
		System.out.println("Filtered by date and departure time: \n" + trains.filter(today, "15:00", "19:00"));
		
		trains.add(new Train(13, "Kyiv", "Ternopil", "19.01.2013", "15:35"));
		TrainXMLHelper.exportXML(trains, "source.xml");		
		trains = TrainXMLHelper.importXML("source.xml");
		System.out.println("Filtered by date and departure time: \n" + trains.filter(today, "15:00", "19:00"));

	}

}

@XmlRootElement(name="trains")
@XmlAccessorType(XmlAccessType.FIELD)
class Trains{
	Trains(){
	}
	
	Trains(Collection<Train> trains){
		this.trains = (Set<Train>) trains;
	}
	
	@XmlElement(name="train")	
	private Set<Train> trains = new HashSet<>();
	
	public Set<Train> getTrains(){
		return new HashSet<Train>(trains);		
	}
	
	public void add(Train train){
		trains.add(train);
	}
	
	public Trains filter(Date date, String timeFrom, String timeTo){
		return new Trains ((Set<Train>) this.getTrains().stream().filter(
				t -> {
						try {
							//return t.getDate().equals(dateFormat.parse(dateFormat.format(Calendar.getInstance().getTime()))) //current date
							return t.getDate().equals(TrainXMLHelper.dateFormat.parse(TrainXMLHelper.dateFormat.format(date))) //specified Date
									&& t.getDeparture().after(TrainXMLHelper.timeFormat.parse(timeFrom))
									&& t.getDeparture().before(TrainXMLHelper.timeFormat.parse(timeTo));
						} catch (Exception e) {
							System.out.println("Cannot parse date" + timeFrom);
							e.printStackTrace();
						}
						return false;					
					}						
				).collect(Collectors.toSet()));
	}
	
	@Override
	public String toString(){
		String result = "";
		for(Train train : trains){
			result += train.toString() + "\n";
		}
		return result;
	}
}
@XmlAccessorType(XmlAccessType.FIELD)
class Train{
	@XmlAttribute
	private int id;
	@XmlElement
	private String from;
	@XmlElement
	private String to;
	@XmlElement
	@XmlJavaTypeAdapter(TrainXMLHelper.DateAdapter.class)
	private Date date;
	@XmlElement
	@XmlJavaTypeAdapter(TrainXMLHelper.TimeAdapter.class)
	private Date departure;
	
	public Train(){		
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	public Train(int id, String from, String to, String date, String departure) {
		super();
		this.id = id;
		this.from = from;
		this.to = to;
		try {
			this.date = TrainXMLHelper.dateFormat.parse(date);		
			this.departure = TrainXMLHelper.timeFormat.parse(departure);
		} catch (ParseException e) {
			System.err.println("Cannot parse dates for train id = " + id);
		}
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;		
	}
	public Date getDeparture() {
		return departure;
	}
	public void setDeparture(Date departure) {
		this.departure = departure;
	}
	@Override
	public String toString() {
		return "Train [id=" + id + ", from=" + from + ", to=" + to + ", date=" + TrainXMLHelper.dateFormat.format(date) + ", departure=" + TrainXMLHelper.timeFormat.format(departure)
				+ "]";
	}
}

class TrainXMLHelper{	
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("DD.MM.yyyy");
	public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");	
	
	public static Trains importXML(String path){
		File file = new File(path);
		Trains trains = null;
		try {			
			trains = (Trains) JAXBContext.newInstance(Trains.class).createUnmarshaller().unmarshal(file);
		} catch (JAXBException e) {
			System.out.println("Cannot parse file: ");
			e.printStackTrace();
		}			
		return trains;			
	}
	
	public static void exportXML(Trains trains, String path) {
		File file = new File(path);
		try {
			Marshaller marshaller = JAXBContext.newInstance(Trains.class).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(trains, file);
		} catch (JAXBException e) {
			System.out.println("Cannot create xml: ");
			e.printStackTrace();
		}			
	}

	static class TimeAdapter extends XmlAdapter<String, Date> {

	    @Override
	    public String marshal(Date v) throws Exception {
	        synchronized (timeFormat) {
	            return timeFormat.format(v);
	        }
	    }

	    @Override
	    public Date unmarshal(String v) throws Exception {
	        synchronized (timeFormat) {
	            return timeFormat.parse(v);
	        }
	    }
	}
	
	static class DateAdapter extends XmlAdapter<String, Date> {

	    @Override
	    public String marshal(Date v) throws Exception {
	        synchronized (dateFormat) {
	            return dateFormat.format(v);
	        }
	    }

	    @Override
	    public Date unmarshal(String v) throws Exception {
	        synchronized (dateFormat) {
	            return dateFormat.parse(v);
	        }
	    }

	}
}
