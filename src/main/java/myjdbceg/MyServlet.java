package myjdbceg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

@WebServlet("/Check")
@MultipartConfig()
public class MyServlet extends HttpServlet {
	private final static Logger LOGGER = 
		    Logger.getLogger(FileUpload.class.getCanonicalName());
	 OutputStream out1 = null;
	    InputStream filecontent = null;
	    java.sql.Statement stmt = null;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("<html><body><form method=\"POST\"   >");
		out.println("<table>");
		out.println("<tr>");
		out.println("<td>");
		out.println("user");
		out.println("</td>");
		out.println("<td>");
		out.println("<input name=\"user\" />");
		out.println("</td>");
		out.println("</tr>");
		
		out.println("<tr>");
		out.println("<td>");
		out.println("password");
		out.println("</td>");
		out.println("<td>");
		out.println("<input name=\"password\" type=\"password\"/>");
		out.println("</td>");
		out.println("</tr>");
	
		out.println("<tr>");
		out.println("<td>");
		out.println("host");
		out.println("</td>");
		out.println("<td>");
		out.println("<input name=\"host\" />");
		out.println("</td>");
		out.println("</tr>");
		
		out.println("<tr>");
		out.println("<td>");
		out.println("port");
		out.println("</td>");
		out.println("<td>");
		out.println("<input name=\"port\" />");
		out.println("</td>");
		
		out.println("</tr>");
		out.println("</table>");

		out.println("<input type=\"submit\" value=\"submit\"/>");
		out.println("</form></body></html>");
	}



	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		PrintWriter out = resp.getWriter();
		out.println("<html><body>");
		out.println("<head>");
		out.println("<style>");
		out.println("table {");
		out.println("    border-collapse: collapse;");
		out.println("}");
		out.println("");
		out.println("table, td, th {");
		out.println("    border: 1px solid black;");
		out.println("}");
		out.println("</style>");
		out.println("</head>");
		try {
			process(out, req);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(out);
			System.out.println(e);
			
		}
		finally
		{
			out.println("</body></html>");
		}
	}
	

	private void process(PrintWriter out, HttpServletRequest req) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, FileUploadException, IOException, ServletException, org.apache.commons.fileupload.FileUploadException {
		  		out.println("processing...........<br/>");
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String host = req.getParameter("host");
		String port = req.getParameter("port");
		String url="jdbc:mysql://"+host+":"+port;
		String u=req.getParameter("user");
		String p=req.getParameter("password");
		Connection conn = DriverManager.getConnection(url, u, p );
		out.println("got connection...........<br/>");  
		URL u1 = getResourceInternal("/Registration.sql");
		
		if(u1==null){
			u1 = getResourceInternal("\\Registration.sql");
		}
		if(u1==null){
			u1 = getResourceInternal("Registration.sql");
		}
		if(u1==null){
			out.println("Null Resources");
		}
		else{
			out.println(u1.toExternalForm()+" got");
			InputStream inp = u1.openStream();
			useInputStream(conn, inp);              
		}
		
		
		DatabaseMetaData md = conn.getMetaData();
		showRs(out, md.getCatalogs(),  "catalogs");
		showRs(out, md.getSchemas(),  "schemas");
		showRs(out, md.getTables(null, null, null, null),  "tables");
				
	}



	private void useInputStream(Connection conn, InputStream input) throws IOException, SQLException {
		InputStreamReader reader=new InputStreamReader(input);
		BufferedReader buffer=new BufferedReader(reader);
		String line=null;
		StringBuffer stringBuffer = new StringBuffer();

		while((line =buffer.readLine())!=null){
			 
			   stringBuffer.append(line).append("\n");
			  }
		String content=stringBuffer.toString();
		//System.out.println(content);	
		String[] query=content.split(";");
		   stmt = conn.createStatement();
		   System.out.println(query.length);
		for(int i=0;i<query.length-1;i++){
			String q=query[i];
			if(q!=null){
				q=q.trim();
				if(q.length()>0){
					
					stmt.execute(q);
					
				}
			}
	
		}
	}



	private URL getResourceInternal(String path) {
		URL u1=this.getClass().getResource(path);
		if(u1==null){
		u1=this.getClass().getClassLoader().getResource(path);
		
		}
		return u1;
	}

	

	private void showRs(PrintWriter out, ResultSet rs,  String label) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		out.println(label+"<hr/>");
		out.println("<table>");
		
		out.println("<tr><th colspan=\""+columnCount+"\">"+label+"</th></tr>");
		out.println("<tr>");
		for (int i = 1; i <= columnCount; i++) 
		{
			out.println("<th>");
			out.println(rsmd.getColumnName(i));
			out.println("</th>");
		}
		out.println("</tr>");
		while(rs.next())
		{
			out.println("<tr>");
			for (int i = 1; i <= columnCount; i++) 
			{
				out.println("<td>");
				out.println(rs.getString(i));
				out.println("</td>");
			}
			out.println("</tr>");
		}
		out.println("</table>");
	}

}
