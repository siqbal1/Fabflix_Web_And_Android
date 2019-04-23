

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
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * Servlet implementation class SingleMovieServlet
 */
@WebServlet("/SingleMovie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SingleMovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
        //create string to setup path for css file
        
        //set response mime type
        response.setContentType("text/html");
        
        //get printwriter function to write response
        PrintWriter out = response.getWriter();
       
        out.println("<!DOCTYPE html>\r\n" + 
        		"<html>\r\n" + 
        		"<head>\r\n" + 
        		"<meta charset=\"ISO-8859-1\">\r\n" + 
        		"<title>Fabflix Single Movie Page</title>\r\n" + 
        		"\r\n" + 
        		" <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\r\n" + 
        		"\r\n" + 
        		"    <!-- include jquery autocomplete JS  -->\r\n" + 
        		" <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.devbridge-autocomplete/1.4.7/jquery.autocomplete.min.js\"></script>\r\n" + 
        		"    \r\n" + 
        		"<link rel=\"stylesheet\" href=\"singleMoviePage.css\"/>\r\n" + 
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
		
       String movieId = request.getParameter("movieId");
        
        try {
        	
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                System.out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/SlaveDB");
            
            if (ds == null)
                System.out.println("ds is null.");
			
            Connection connection = ds.getConnection();
    		// declare statement
    	
    		String query = "SELECT *\r\n" + 
    				"FROM\r\n" + 
    				"(SELECT movies.id, movies.title, movies.director, movies.year, GROUP_CONCAT(DISTINCT genres.id) as genreIds, GROUP_CONCAT(DISTINCT genres.name) as genres\r\n" + 
    				"FROM movies, genres, genres_in_movies\r\n" + 
    				"WHERE movies.id = ? \r\n" + 
    				"	AND movies.id = genres_in_movies.movieId\r\n" + 
    				"	AND genres.id = genres_in_movies.genreId\r\n" + 
    				"GROUP BY movies.id, movies.title, movies.director, movies.year\r\n" + 
    				"ORDER BY title) as movieList\r\n" + 
    				"LEFT OUTER JOIN ratings ON movieList.id = ratings.movieId;\r\n";
    		
    		PreparedStatement statement = connection.prepareStatement(query);
    		
    		statement.setString(1, movieId);
    		
    		ResultSet resultSet = statement.executeQuery();
    		
    		resultSet.next();
    		
    		String title = resultSet.getString("title");
    		String director = resultSet.getString("director");
    		String year = resultSet.getString("year");
    		String rating = resultSet.getString("rating");
			String [] genreIdList = resultSet.getString("genreIds").split(",");
    		String[] genres = resultSet.getString("genres").split(",");
    		
    		if(rating == null)
    			rating = "N/A";
    		
    		if(year == null || year.equals('0'))
    			year = "N/A";
    		
    		if(director == null)
    		{
    			director = "N/A";
    		}
    		
    		int genreLength = genres.length - 1;
    		
    		out.println("<div id=\"movieInfo\">");
    		out.println("<body>");
    		out.println("<h1>" + title + "</h1>");
    		
    		out.println("<p>Director: " + director + "<br>\n"
    				+ "Year:" + year + "<br>\n"
    				+ "Rating: " + rating + "<br><br>\n"
    				+ "Genres:");
    		
    		
    		
	    		for(int genre = 0; genre <= genreLength; genre++)
	    		{
	    			out.print("<form style=\"display: inline;\" action=\"BrowseGenre\" method = \"GET\">"
	    					+       "<input type=\"hidden\" name=\"genreId\" value=\"" + genreIdList[genre] + "\">\n"
	    					+		"<button class=\"genreLinkButton\" type=\"submit\">" + genres[genre] + "</button>"
	    					+	"</form>\n");
	    		}
    		
    		
   
    		out.println("<br><br>");
    		out.println("Cast List: <br><br>");

    		
    		String starQuery = "SELECT stars.id, stars.name\n"
    				+ "FROM stars_in_movies, stars \n"
    				+ "WHERE stars_in_movies.movieId = ? \n"
    				+ "AND stars_in_movies.starId = stars.id \n"
    				+ "ORDER BY stars.name \n";
    		
    		PreparedStatement starStatement = connection.prepareStatement(starQuery);
    		
    		starStatement.setString(1, movieId);
    		
    		ResultSet starSet = starStatement.executeQuery();
    		
    		while(starSet.next())
    		{
    			String starName = starSet.getString("name");
    			String starId = starSet.getString("id");
    			out.println("<form style=\"display:inline;\" action=\"SingleStar\" method=\"GET\">"
    							+ "<input type=\"hidden\" name=\"starId\" value=\"" + starId + "\">\n"
    							+ "<button class=\"starLinkButton\" type=\"submit\">" + starName + ", </button>"
    						+ "</form>");
    		}
    		
    		out.println("<br><br>");
    		
    		out.println("<form action=\"Checkout\" method=\"GET\">"
			+ 		"<input type=\"hidden\" name=\"newCartItemId\" value=\"" + movieId + "\">\n"
			+		"<input type=\"hidden\" name=\"newCartItemTitle\" value=\"" + title + "\">\n"
			+		"<button class=\"addToCartButton\" type=\"submit\">Add to Cart</button>"
			+	"</form>\n");
    		
    		out.println("</p>");
    		out.println("</div>");
    		out.println("</body>");
    		
    		
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
