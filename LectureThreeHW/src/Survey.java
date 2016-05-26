
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Survey extends HttpServlet{
	private int javaLovers = 0;
	private int netLovers = 0;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if(req.getParameter("java").equals("yes")){
			javaLovers++;
		}
		if(req.getParameter("dotNet").equals("yes")){
			netLovers++;
		}
		PrintWriter pw = resp.getWriter();
		pw.println("<html><head><meta charset=\"ISO-8859-1\"><title>Survey</title></head><body>");
		pw.println(String.format("Java lovers: %d<br>.NET lovers:%d",javaLovers,netLovers));
		pw.println("</body></html>");
	}
}
