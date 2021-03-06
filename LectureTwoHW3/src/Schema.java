import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Schema {
	static class Result{
		static SimpleDateFormat dateFormat =  new SimpleDateFormat("dd.MM.yyyy");
		static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
				
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
			return "Result queryDate=" + dateFormat.format(queryDate) + ", rates:" + rates;
		}
    }
    
    static class Rate{
    	private String id;
    	private String name;
    	@DataFormat(dataFormat="double")
    	private Double rate;
    	@DataFormat(dataFormat="date")
    	private Date date;
    	@DataFormat(dataFormat="time")
    	private Date time;
    	@DataFormat(dataFormat="double")
    	private Double ask;
    	@DataFormat(dataFormat="double")
    	private Double bid;
    	
    	public Rate(){}

		@Override
		public String toString() {
			return "\n\tRate [id=" + id + ", name=" + name + ", rate=" + rate + ", date=" + Result.dateFormat.format(date) + ", time=" + Result.timeFormat.format(time)
					+ ", ask=" + ask + ", bid=" + bid + "]";
		}
    }
}
