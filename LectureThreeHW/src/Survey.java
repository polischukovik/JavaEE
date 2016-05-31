
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTMLDocument;

@SuppressWarnings("serial")
public class Survey extends HttpServlet{
	private List<Responce> survey = new ArrayList<>();
	private String htmlCore = "<html><head><meta charset=\"utf-8\"><link rel=\"stylesheet\" type=\"text/css\" href=\"css/TableStyle.css\"><title>Survey</title></head><body>%s</body></html>";
	
	class Responce{
		private String name;
		private String surname;
		private int age;
		private String gender;
		private int like;
		private String from;
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/survey.html").forward(req, resp);
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
		String table = "<table class=\"rwd-table\"><tr><th>Name</th><th>Surname</th><th>Age</th><th>Gender</th><th>Like</th><th>From</th></tr>";
		for(Responce r : survey){
			table += String.format("<tr><td data-th=\"Name\">%s</td>"
									+ "<td data-th=\"Surname\">%s</td>"
									+ "<td data-th=\"Age\">%s</td>"
									+ "<td data-th=\"Gender\">%s</td>"
									+ "<td data-th=\"Like\">%s</td>"
									+ "<td data-th=\"From\">%s</td></tr>",
					r.name, r.surname, r.age, r.gender, r.like, r.from);
		}
		table += String.format("<tr><td><b>Total</b></td><td><b>Responses:%d</b></td><td><b>avg: %.2f </b></td><td></td><td><b>%d</b></td><td></td></tr></table>",
				survey.size(),survey.stream().mapToInt(t -> t.age).average().getAsDouble(), survey.stream().mapToInt(t -> t.like).sum());
		pw.println(String.format(htmlCore, table));
		
	}
}
