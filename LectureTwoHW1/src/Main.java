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
import javax.xml.bind.Unmarshaller;
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
		
		Trains trains = TrainXMLHelper.parseXML("source.xml");
		Trains filtered = new Trains ((Set<Train>) trains.getTrains().stream().filter(
				t -> {
						try {
							SimpleDateFormat dateFormat = new SimpleDateFormat("DD.MM.yyyy");
							SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
							//return t.getDate().equals(dateFormat.parse(dateFormat.format(Calendar.getInstance().getTime()))) //current date
							return t.getDate().equals(dateFormat.parse(dateFormat.format(today))) //specified Date
									&& t.getDeparture().after(timeFormat.parse("15:00"))
									&& t.getDeparture().before(timeFormat.parse("19:00"));
						} catch (Exception e) {
							System.out.println("Cannot parse date");
						}
						return false;					
					}						
				).collect(Collectors.toSet()));
		
		System.out.println("Filtered by date and departure time: \n" + filtered);
		System.out.println("Full list of trains: \n" + trains);

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
		return new HashSet<>(trains);		
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
		return "Train [id=" + id + ", from=" + from + ", to=" + to + ", date=" + new SimpleDateFormat("DD.MM.yyyy").format(date) + ", departure=" + new SimpleDateFormat("HH:mm").format(departure)
				+ "]";
	}
}

class TrainXMLHelper{	
	
	public static Trains parseXML(String path){
		File file = new File(path);
		JAXBContext context;
		Trains trains = null;
		try {
			context = JAXBContext.newInstance(Trains.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			trains = (Trains) unmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			System.out.println("Cannot parse file: ");
			e.printStackTrace();
		}			
		return trains;			
	}
	
	static class TimeAdapter extends XmlAdapter<String, Date> {

	    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

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
	
	static class DateAdapter extends XmlAdapter<String, Date> {

	    private final SimpleDateFormat dateFormat = new SimpleDateFormat("DD.MM.yyyy");

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
