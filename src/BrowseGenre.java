

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
 * Servlet implementation class BrowseGenre
 */
@WebServlet("/BrowseGenre")
public class BrowseGenre extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrowseGenre() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //set response mime type
        response.setContentType("text/html");
        
        //get printwriter function to write response
        PrintWriter out = response.getWriter();
        
        
        
        out.println("<!DOCTYPE html>\r\n" + 
        		"<html>\r\n" + 
        		"<head>\r\n" + 
        		"<meta charset=\"ISO-8859-1\">\r\n" + 
        		"<title>Fabflix Browse Genre Page</title>\r\n" + 
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

        //get genreId for search
        
        String genreId = request.getParameter("genreId");
        
        int currentPage = 0;
		int recordsPerPage = 10;
		int offset = 0;
		String orderByType = "title";
		String orderInType = "ASC";
		
		//check to see if the parameters exist before assigning values
		if(request.getParameterMap().containsKey("currentPage"))
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		
		if(request.getParameterMap().containsKey("recordsPerPage"))
			recordsPerPage = Integer.parseInt(request.getParameter("recordsPerPage"));
		
		if(request.getParameterMap().containsKey("lastRecord"))
			offset = Integer.parseInt(request.getParameter("lastRecord"));
		
		if(request.getParameterMap().containsKey("orderByType"))
			orderByType = request.getParameter("orderByType");
		
		if(request.getParameterMap().containsKey("orderInType"))
			orderInType = request.getParameter("orderInType");
			
		out.println("<body>");
		out.println("<h1> Search Results </h1>");
		//make buttons for paginiation
		out.println("<span>Number of Results:</span>");
		out.println("<div id=\"numRecordsButtons\">");
		out.println("<form class = \"numRecords\" action=\"BrowseGenre\" method=\"GET\">"
				+ 		"<input type=\"hidden\" name=\"recordsPerPage\" value=\"10\">\n"
				+ 		"<input type=\"hidden\" name=\"genreId\" value=\"" + genreId + "\">\n"
				+ 		"<input type=\"hidden\" name=\"currentPage\" value=\"" + currentPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"lastRecord\" value=\"" + offset + "\">\n"
				+		"<input type=\"hidden\" name=\"orderByType\" value=\"" + orderByType + "\">\n"
				+		"<input type=\"hidden\" name=\"orderInType\" value=\"" + orderInType + "\">\n"
				+		"<button type=\"submit\">" + 10 + "</button>"
				+	"</form>\n");
		out.println("<form class = \"numRecords\" action=\"BrowseGenre\" method=\"GET\">"
				+ 		"<input type=\"hidden\" name=\"recordsPerPage\" value=\"20\">\n"
				+ 		"<input type=\"hidden\" name=\"genreId\" value=\"" + genreId + "\">\n"
				+ 		"<input type=\"hidden\" name=\"currentPage\" value=\"" + currentPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"lastRecord\" value=\"" + offset + "\">\n"
				+		"<input type=\"hidden\" name=\"orderByType\" value=\"" + orderByType + "\">\n"
				+		"<input type=\"hidden\" name=\"orderInType\" value=\"" + orderInType + "\">\n"
				+		"<button type=\"submit\">" + 20 + "</button>"
				+	"</form>\n");
		out.println("<form class = \"numRecords\" action=\"BrowseGenre\" method=\"GET\">"
				+ 		"<input type=\"hidden\" name=\"recordsPerPage\" value=\"50\">\n"
				+ 		"<input type=\"hidden\" name=\"genreId\" value=\"" + genreId + "\">\n"
				+ 		"<input type=\"hidden\" name=\"currentPage\" value=\"" + currentPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"lastRecord\" value=\"" + offset + "\">\n"
				+		"<input type=\"hidden\" name=\"orderByType\" value=\"" + orderByType + "\">\n"
				+		"<input type=\"hidden\" name=\"orderInType\" value=\"" + orderInType + "\">\n"
				+		"<button type=\"submit\">" + 50 + "</button>"
				+	"</form>\n");
		out.println("</div>");
		
		out.println("<span>Order By:</span>");
		out.println("<div id=\"orderByList\">");
		out.println("<form class = \"orderBy\" action=\"BrowseGenre\" method=\"GET\">"
				+ 		"<input type=\"hidden\" name=\"recordsPerPage\" value=\"" + recordsPerPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"genreId\" value=\"" + genreId + "\">\n"
				+ 		"<input type=\"hidden\" name=\"currentPage\" value=\"" + currentPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"lastRecord\" value=\"" + offset + "\">\n"
				+		"<input type=\"hidden\" name=\"orderByType\" value=\"title\">\n"
				+		"<input type=\"hidden\" name=\"orderInType\" value=\"" + orderInType + "\">\n"
				+		"<button type=\"submit\">Title</button>"
				+	"</form>\n");
		
		out.println("<form class = \"orderBy\" action=\"BrowseGenre\" method=\"GET\">"
				+ 		"<input type=\"hidden\" name=\"recordsPerPage\" value=\"" + recordsPerPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"genreId\" value=\"" + genreId + "\">\n"
				+ 		"<input type=\"hidden\" name=\"currentPage\" value=\"" + currentPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"lastRecord\" value=\"" + offset + "\">\n"
				+		"<input type=\"hidden\" name=\"orderByType\" value=\"rating\">\n"
				+		"<input type=\"hidden\" name=\"orderInType\" value=\"" + orderInType + "\">\n"
				+		"<button type=\"submit\">Rating</button>"
				+	"</form>\n");
		
		out.println("</div>");
		
		out.println("<span>Order:</span>");
		out.println("<div id=\"orderInList\">");
		out.println("<form class = \"orderIn\" action=\"BrowseGenre\" method=\"GET\">"
				+ 		"<input type=\"hidden\" name=\"recordsPerPage\" value=\"" + recordsPerPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"genreId\" value=\"" + genreId + "\">\n"
				+ 		"<input type=\"hidden\" name=\"currentPage\" value=\"" + currentPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"lastRecord\" value=\"" + offset + "\">\n"
				+		"<input type=\"hidden\" name=\"orderByType\" value=\"" + orderByType + "\">\n"
				+		"<input type=\"hidden\" name=\"orderInType\" value=\"ASC\"> \n"
				+		"<button type=\"submit\">Ascending</button>"
				+	"</form>\n");
		out.println("<form class = \"orderIn\" action=\"BrowseGenre\" method=\"GET\">"
				+ 		"<input type=\"hidden\" name=\"recordsPerPage\" value=\"" + recordsPerPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"genreId\" value=\"" + genreId + "\">\n"
				+ 		"<input type=\"hidden\" name=\"currentPage\" value=\"" + currentPage + "\">\n"
				+ 		"<input type=\"hidden\" name=\"lastRecord\" value=\"" + offset + "\">\n"
				+		"<input type=\"hidden\" name=\"orderByType\" value=\"" + orderByType + "\">\n"
				+		"<input type=\"hidden\" name=\"orderInType\" value=\"DESC\"> \n"
				+		"<button type=\"submit\">Descending</button>"
				+	"</form>\n");
		out.println("</div>");
    	
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
    		
    		String query = "SELECT *\r\n" + 
    				"FROM\r\n" + 
    				"(SELECT movies.id, movies.title, movies.director, movies.year, GROUP_CONCAT(DISTINCT stars.id,',',stars.name SEPARATOR ';') as stars, GROUP_CONCAT(DISTINCT genres.id) as genreIds, GROUP_CONCAT(DISTINCT genres.name) as genres\r\n" + 
    				"FROM movies, stars, stars_in_movies, genres, genres_in_movies\r\n" + 
    				"WHERE genres.id = ?\r\n" + 
    				"	 AND genres.id = genres_in_movies.genreId\r\n" + 
    				"	 AND movies.id = genres_in_movies.movieId\r\n" + 
    				"	AND movies.id = stars_in_movies.movieId\r\n" + 
    				"	AND stars_in_movies.starId = stars.id\r\n" + 
    				"GROUP BY movies.id, movies.title, movies.director, movies.year\r\n" + 
    				") as movieList\r\n" + 
    				"LEFT OUTER JOIN ratings ON movieList.id = ratings.movieId \n" +
    				"ORDER BY " + orderByType + " " + orderInType + "\n" +
    				"LIMIT " + recordsPerPage + "\r\n" + 
    				"OFFSET " + offset;
    		
    		PreparedStatement statement = connection.prepareStatement(query);
    		
    		statement.setString(1, genreId);
    	
    		
    		ResultSet resultSet = statement.executeQuery();
    		
    		int numOfRecords = 0;
    		
    		out.println("<div style=\"overflow-x:auto;\">");
    		out.println("<table id=\"movieTable\">\n");
    		
    		
    		while(resultSet.next())
    		{
    			//increment count for number of records
    			++numOfRecords;
    			
    			String movieId = resultSet.getString("id");
    			String movieTitle = resultSet.getString("title");
    			String movieDirector = resultSet.getString("director");
    			String movieGenres = resultSet.getString("genres");
    			String [] starsList = resultSet.getString("stars").split(";");
    			String movieRating = resultSet.getString("rating");
    			
    			int starsLength = starsList.length - 1;

    			int movieYear = resultSet.getInt("year");
    			
    			if(movieRating == null)
    			{
    				movieRating = "N/A";
    			}

    			
    			out.println("<tr>\n"
    					+ "<td>" + movieId + "</td>\n" 
    					+ "<td>"
    					+ "<form action=\"SingleMovie\" method = \"GET\">"
    					+       "<input type=\"hidden\" name=\"movieId\" value=\"" + movieId + "\">\n"
    					+		"<button class = \"movieLinkButton\" type=\"submit\">" + movieTitle + "</button>"
    					+	"</form>\n"
    					+ "</td>"
    					+ "<td>"
    					+ "<form action=\"Checkout\" method=\"GET\">"
    					+ 		"<input type=\"hidden\" name=\"newCartItemId\" value=\"" + movieId + "\">\n"
    					+		"<input type=\"hidden\" name=\"newCartItemTitle\" value=\"" + movieTitle + "\">\n"
    					+		"<button class=\"addToCartButton\" type=\"submit\">Add to Cart</button>"
    					+	"</form>\n"
    					+ "</td>\n"
    					+ "<td>" + movieRating + "</td>"
    					+ "<td>" + movieDirector + "</td>\n"
    					+ "<td>" + movieYear + "</td>\n"
    					+ "<td>" + movieGenres + "</td>\n"
    					+ "<td>");
    			

    			for(int star = 0; star <= starsLength; star++) 
    			{
    				String[] starInfo = starsList[star].split(",");
    				String starId = starInfo[0];
    				String starsName = starInfo[1];
    				
    				out.print("<form style=\"display:inline\" action=\"SingleStar\" method = \"GET\">"
        					+       "<input type=\"hidden\" name=\"starId\" value=\"" + starId + "\">\n"
        					+		"<button class = \"starLinkButton\" type=\"submit\">" + starsName + "</button>"
        					+	"</form>\n");
    			}
    			
    			out.println("</td>\n"
    					+ "</tr>"); 
    		}
    		
    		out.println("</table>");
    		out.println("</div>");
    		
    		if(currentPage != 0)
    		{
    			out.println("<form style=\"display:inline;\" action=\"BrowseGenre\" method=\"GET\">"
    						+ "<input type=\"hidden\" name=\"recordsPerPage\" value=\"10\">\n"
    						+ 		"<input type=\"hidden\" name=\"genreId\" value=\"" + genreId + "\">\n"
    						+ 	"<input type=\"hidden\" name=\"currentPage\" value=\"" + (--currentPage) + "\">\n"
    						+ 	"<input type=\"hidden\" name=\"lastRecord\" value=\"" + (offset - recordsPerPage) + "\">\n"
    						+		"<input type=\"hidden\" name=\"orderByType\" value=\"" + orderByType + "\">\n"
    						+		"<input type=\"hidden\" name=\"orderInType\" value=\"" + orderInType + "\">\n"
    						+	"<button class=\"prevButton\" type=\"submit\">Previous</button>"
    						+ "</form>\n");
    		}
    		
    		
    		if(numOfRecords == recordsPerPage)
    		{
    			out.println("<form style=\"display:inline;\" action=\"BrowseGenre\" method=\"GET\">"
						+ "<input type=\"hidden\" name=\"recordsPerPage\" value=\"" + recordsPerPage + "\">\n"
						+ 		"<input type=\"hidden\" name=\"genreId\" value=\"" + genreId + "\">\n"
						+ 	"<input type=\"hidden\" name=\"currentPage\" value=\"" + (++currentPage) + "\">\n"
						+ 	"<input type=\"hidden\" name=\"lastRecord\" value=\"" + (offset + recordsPerPage) + "\">\n"
						+		"<input type=\"hidden\" name=\"orderByType\" value=\"" + orderByType + "\">\n"
						+		"<input type=\"hidden\" name=\"orderInType\" value=\"" + orderInType + "\">\n"
						+	"<button class = \"nextButton\" type=\"submit\">Next</button>"
						+ "</form>\n");
    		}
    		
    		
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
