
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Survey extends HttpServlet{
	private List<Responce> survey = new ArrayList<>();
	private String htmlCore = "<html><head><meta charset=\"ISO-8859-1\"><title>Survey</title></head><body><table border=\"1\">%s</table></body></html>";
	
	class Responce{
		String name;
		String surname;
		int age;
		String gender;
		int like;
		String from;
		public Responce(String name, String surname, int age, String gender, int like, String from) {
			super();
			this.name = name;
			this.surname = surname;
			this.age = age;
			this.gender = gender;
			this.like = like;
			this.from = from;
		}		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		survey.add( new Responce(
				req.getParameter("name"),
				req.getParameter("surname"),
				Integer.parseInt(req.getParameter("age")),
				req.getParameter("gender"), 
				Integer.parseInt(req.getParameter("like")), 
				req.getParameter("from")));
		
		PrintWriter pw = resp.getWriter();
		String table = "<tr><td>Name</td><td>Surname</td><td>Age</td><td>Gender</td><td>Like</td><td>From</td></tr>";
		for(Responce r : survey){
			table += String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
					r.name, r.surname, r.age, r.gender, r.like, r.from);
		}
		table += String.format("<tr><td><b>Total</b></td><td><b>Responses:%d</b></td><td><b>avg: %.2f </b></td><td></td><td><b>%d</b></td><td></td></tr>",
				survey.size(),survey.stream().mapToInt(t -> t.age).average().getAsDouble(), survey.stream().mapToInt(t -> t.like).sum());
		pw.println(String.format(htmlCore, table));
		
	}
}
