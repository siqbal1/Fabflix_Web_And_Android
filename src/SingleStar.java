

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
 * Servlet implementation class SingleStar
 */
@WebServlet("/SingleStar")
public class SingleStar extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SingleStar() {
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
        		"<title>Fabflix Single Star Page</title>\r\n" + 
        		"\r\n" + 
        		" <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\r\n" + 
        		"\r\n" + 
        		"    <!-- include jquery autocomplete JS  -->\r\n" + 
        		" <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.devbridge-autocomplete/1.4.7/jquery.autocomplete.min.js\"></script>\r\n" + 
        		"    \r\n" + 
        		"<link rel=\"stylesheet\" href=\"singleStarPage.css\"/>\r\n" + 
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
        		"			<li><a href=\"Browse\" onClick=\"loadCache();\">Browse</a></li>\r\n" + 
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


        
        //get http session to check attributes
		//HttpSession session = request.getSession(true);
		
       String starId = request.getParameter("starId");
        
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
    		
    		String query = "SELECT * \n"
    				+ "FROM stars \n"
    				+ "WHERE stars.id = ?";
    		
    		PreparedStatement statement = connection.prepareStatement(query);

    		statement.setString(1, starId);
    		
    		ResultSet resultSet = statement.executeQuery();
    		
    		query = "SELECT movies.title, movies.id \n"
    				+ "FROM stars, movies, stars_in_movies \n"
    				+ "WHERE stars.id = ? \n"
    				+ "AND stars_in_movies.starId = stars.id "
    				+ "AND movies.id = stars_in_movies.movieId "
    				+ "ORDER BY movies.title";
    		
    		
    		
    		PreparedStatement movieStatement = connection.prepareStatement(query);
    		
    		movieStatement.setString(1, starId);
    		
    		ResultSet movieSet = movieStatement.executeQuery();
    		
    		
    		resultSet.next();
    		
    		String name = resultSet.getString("name");
    		String birthyear = resultSet.getString("birthyear");
    		
    		if(birthyear == null || birthyear.isEmpty() || birthyear.equals("0"))
    			birthyear = "N/A";
    		
    		out.println("<div id=\"starInfo\">");
    		out.println("<body>");
    		out.println("<h1>" + name + "</h1>");
    		
    		out.println("<p>Birth Year: " + birthyear + "</p>\n");

    		out.println("<p>Movie List: </p>");
    		
    		while(movieSet.next())
    		{
    			String movieTitle = movieSet.getString("title");
    			String movieId = movieSet.getString("id");
    			out.println("<form style=\"display:inline;\" action=\"SingleMovie\" method=\"GET\">"
    							+ "<input type=\"hidden\" name=\"movieId\" value=\"" + movieId + "\">\n"
    							+ "<button class=\"movieLinkButton\" type=\"submit\">" + movieTitle + ", </button>"
    						+ "</form>");
    		}
    	
    	
    		out.println("</div>");
    		out.println("</body>");
    		
    		
    		resultSet.close();
    		movieSet.close();
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
