

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class BrowseServelet
 */
@WebServlet("/Browse")
public class BrowseServelet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrowseServelet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        //create string to setup path for css file
        
        //set response mime type
        response.setContentType("text/html");
        
        //get printwriter function to write response
        PrintWriter out = response.getWriter();
       
        
        out.println("<!DOCTYPE html>\r\n" + 
        		"<html>\r\n" + 
        		"<head>\r\n" + 
        		"<meta charset=\"ISO-8859-1\">\r\n" + 
        		"<title>Fabflix Browse Page</title>\r\n" + 
        		"\r\n" + 
        		" <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\r\n" + 
        		"\r\n" + 
        		"    <!-- include jquery autocomplete JS  -->\r\n" + 
        		" <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.devbridge-autocomplete/1.4.7/jquery.autocomplete.min.js\"></script>\r\n" + 
        		"    \r\n" + 
        		"<link rel=\"stylesheet\" href=\"browsePage.css\"/>\r\n" + 
        		"</head>\r\n" + 
        		"\r\n" + 
        		"<div>\r\n" + 
        		"	<h1>\r\n" + 
        		"		<img style=\"vertical-align:middle\" src=\"cameraIcon.png\" alt=\"Camera Icon\" width=\"75\" height=\"75\">\r\n" + 
        		"		<span class=\"title\">Fabflix</span>\r\n" + 
        		"	</h1>\r\n" + 
        		"\r\n" + 
        		"		<ul id=\"navigationBar\">\r\n" + 
        		"			<li><a href=\"mainPage.html\" onClick=\"loadCache();\">Home</a></li>\r\n" + 
        		"			<li><a class=\"active\" href=\"Browse\" onClick=\"loadCache();\">Browse</a></li>\r\n" + 
        		"			<li><a href=\"advancedSearch.html\" onClick=\"loadCache();\">Advanced Search</a></li>\r\n" + 
        		"			<li><a href=\"Checkout\" onClick=\"loadCache();\">Checkout</a></li>\r\n" + 
        		"			<li><form action = \"Search\" id=\"searchBar\">\r\n" + 
        		"				Search:<input type=\"text\" name=\"itemSearch\" id=\"autocomplete\">\r\n" + 
        		"				<input type=\"submit\" onclick=\"handleNormalSearch(document.getElementById('autocomplete').value)\" value=\"Search\"></form>\r\n" + 
        		"				\r\n" + 
        		"				<script src=\"autocomplete.js\"></script>\r\n" + 
        		"			</li>\r\n" + 
        		"		</ul>\r\n" + 
        		"</div>\r\n");
		
		try {
			
			Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/SlaveDB");
            
            if (ds == null)
                out.println("ds is null.");
            
            Connection connection = ds.getConnection();
    		Statement statement = connection.createStatement();
    		
    		String query = "SELECT * FROM genres";
    		
    		ResultSet resultSet = statement.executeQuery(query);
    		
    		out.println("<div id=\"categories\"> \n"
    				+ "<h2>Browse Genres</h2>");
    		
    		//loop thorugh result set to get the names of the genres
    		//make them into buttons and output them
    		while(resultSet.next())
    		{
    			String genreName = resultSet.getString("name");
    			String genreId = resultSet.getString("id");
    			
    			out.println("<form style=\"display: inline;\" action=\"BrowseGenre\" method = \"GET\">"
    					+       "<input type=\"hidden\" name=\"genreId\" value=\"" + genreId + "\">\n"
    					+		"<button class=\"buttons\" type=\"submit\">" + genreName + "</button>"
    					+	"</form>\n");
    		}
    		
    		out.println("<h2>Browse Titles</h2>");
    		
    		//print out links for numbers
    		for(char number = '0'; number <= '9'; number++)
    		{
    			out.println("<form style=\"display: inline;\" action=\"BrowseTitle\"  method = \"GET\">"
    					+       "<input type=\"hidden\" name=\"firstChar\" value=\"" + number + "\">\n"
    					+		"<button class=\"buttons\" type=\"submit\">" + number + "</button>"
    					+	"</form>\n");
    		}
    		
    		for(char letter = 'A'; letter <= 'Z'; letter++)
    		{
    			out.println("<form style=\"display: inline;\" action=\"BrowseTitle\" method = \"GET\">"
    					+       "<input type=\"hidden\" name=\"firstChar\" value=\"" + letter + "\">\n"
    					+		"<button class=\"buttons\" type=\"submit\">" + letter + "</button>"
    					+	"</form>\n");
    		}
    		
    		out.println("</div>");
    		
    		resultSet.close();
    		statement.close();
    		connection.close();

		} catch (Exception e) {
    		/*
    		 * After you deploy the WAR file through tomcat manager webpage,
    		 *   there's no console to see the print messages.
    		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
    		 * 
    		 * To view the last n lines (for example, 100 lines) of messages you can use:
    		 *   tail -100 catalina.out
    		 * This can help you debug your program after deploying it on AWS.
    		 */
    		e.printStackTrace();
    		
    		out.println("<body>");
    		out.println("<p>");
    		out.println("Exception in doGet: " + e.getMessage());
    		out.println("</p>");
    		out.print("</body>");
		}
		
		out.println("</html>");
		out.close();
		
    		
        
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		
		doGet(request, response);
	}

}
