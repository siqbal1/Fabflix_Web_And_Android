

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
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
 * Servlet implementation class EmployeeDashboardAddMovie
 */
@WebServlet("/EmployeeDashboardAddMovie")
public class EmployeeDashboardAddMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeeDashboardAddMovie() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		response.setContentType("text/html"); 
      
	    HttpSession session = request.getSession(true);
		
	     out.println("<html>\r\n" + 
	        		"<head>\r\n" + 
	        		"<meta charset=\"ISO-8859-1\">\r\n" + 
	        		"<title>Fabflix Employee Page</title>\r\n" + 
	        		"<link rel=\"stylesheet\" href=\"EmployeeDashboard.css\"/>\r\n" + 
	        		"</head>\r\n" + 
	        		"\r\n" + 
	        		"<div>\r\n" + 
	        		"	<h1>\r\n" + 
	        		"		<img style=\"vertical-align:middle\" src=\"cameraIcon.png\" alt=\"Camera Icon\" width=\"75\" height=\"75\">\r\n" + 
	        		"		<span class=\"title\">Fabflix</span>\r\n" + 
	        		"	</h1>\r\n" + 
	        		"\r\n" + 
	        		"		<ul id=\"navigationBar\">\r\n" + 
	        		"			<li><a href=\"EmployeeDashboardMain\">MetaData</a></li>\r\n" + 
	        		"			<li><a href=\"EmployeeAddStar\">Add Star</a></li>\r\n" + 
	        		"			<li><a class=\"active\" href=\"EmployeeDashboardAddMovie\">Add Movie</a></li>\r\n" + 
	        		"		</ul>"  + 
	        		"</div>");
	     out.println("<body>");
	     
	     out.println("<h2>Add Movie<h2>");
	     
	     out.println("<form action=\"EmployeeDashboardAddMovie\" method=\"GET\">"
		     		+ "Movie title:<input type=\"text\" name=\"movieTitle\"><br>"
		     		+ "Movie director:<input type=\"text\" name=\"movieDirector\"><br>"
		     		+ "Movie year:<input type=\"number\" name=\"movieYear\"><br>"
		     		+ "Movie Rating:<input type=\"number\" step=\"0.1\" min=\"0\" max=\"10\" name=\"movieRating\"><br>"
		     		+ "Star Name:<input type=\"text\" name=\"starName\"> <br>"
		     		+ "Genre Name:<input type=\"text\" name=\"genreName\"><br>"
		     		+ "<input type=\"hidden\" name=\"requestSent\" value=\"true\">"
		     		+ "<input type=\"submit\" value=\"Add Movie\">"
		     		+ "</form>");
	     
	     if(request.getParameterMap().containsKey("starName") && (request.getParameter("movieTitle").isEmpty()
	    		 || request.getParameter("movieDirector").isEmpty()
	    		 || request.getParameter("movieYear").isEmpty()
	    		 || request.getParameter("starName").isEmpty()
	    		 || request.getParameter("genreName").isEmpty()))
	     {
	    	 out.println("<p style=\"color:red;\"> Error: Movie Information cannot be empty. </p>");
	     }
	     else if(request.getParameterMap().containsKey("requestSent"))
	     {
	    	 String movieTitle = request.getParameter("movieTitle");
	    	 String movieDirector = request.getParameter("movieDirector");
	    	 Integer movieYear = Integer.parseInt(request.getParameter("movieYear"));
	    	 String starName = request.getParameter("starName");
	    	 String genreName = request.getParameter("genreName");
	    	 double movieRating = Double.parseDouble(request.getParameter("movieRating"));
	    	 
	    	 Integer genreId = null;
	    	 String starId = "";
	    	 String movieId = "";
	    	 
	    	 try
 	    	 {
	    		 Context initCtx = new InitialContext();

	             Context envCtx = (Context) initCtx.lookup("java:comp/env");
	             if (envCtx == null)
	                 out.println("envCtx is NULL");

	             // Look up our data source
	             DataSource ds = (DataSource) envCtx.lookup("jdbc/MasterDB");
	             
	             if (ds == null)
	                 out.println("ds is null.");
	             
	             Connection connection = ds.getConnection();
 	    		// declare statement
 	    		String movieQuery = "SELECT * "
 	    				+ "FROM movies "
 	    				+ "WHERE movies.title = ? AND movies.director = ? AND movies.year = ?";
 	    		
 	    		PreparedStatement movieCheckStatement = connection.prepareStatement(movieQuery);
 	    		
 	    		movieCheckStatement.setString(1, movieTitle);
 	    		movieCheckStatement.setString(2, movieDirector);
 	    		movieCheckStatement.setInt(3, movieYear);
 	    		
 	    		ResultSet movieResultSet = movieCheckStatement.executeQuery();
 	    		
 	    		if(movieResultSet.next())
 	    		{
 	    			out.println("<p style=\"color:red;\"> Error: Movie Already Exists.</p>");
 	    		}
 	    		else
 	    		{
 	    			//Add movie using stored procedure
 	    			
 	    			//make a new id for the the movie
 	    			movieQuery = "SELECT max(id) as maxId FROM movies";
 	    			Statement movieIdStatement = connection.createStatement();
 	    			ResultSet movieIdResultSet = movieIdStatement.executeQuery(movieQuery);
 	    			
 	    			movieIdResultSet.next();
 	    			
 	    			movieId = movieIdResultSet.getString("maxId");
 	    			String[] part = movieId.split("(?<=\\D)(?=\\d)");
 	    			
 	    			int nextMovieId = Integer.parseInt(part[1]);
 	    			nextMovieId++;
 	    			
 	    			movieId = part[0] + nextMovieId;
 	    			//check to see if star exists
 	    			//if it does get the star Id
 	    			//else parse max starId and increment
 	    			
 	    			movieIdResultSet.close();
 	    			
 	    			String starQuery = "SELECT * "
 	    					+ "FROM stars "
 	    					+ "WHERE stars.name = ?";
 	    			
 	    			PreparedStatement starResultStatement = connection.prepareStatement(starQuery);
 	    			
 	    			starResultStatement.setString(1, starName);
 	    			
 	    			ResultSet starResultSet = starResultStatement.executeQuery();
 	    			
 	    			if(starResultSet.next())
 	    			{
 	    				starId = starResultSet.getString("id");
 	    			}
 	    			else
 	    			{
 	    				Statement statement = connection.createStatement();
 	    				starQuery = "SELECT max(id) as maxId FROM stars";
 		 	    		ResultSet starIdSet = statement.executeQuery(starQuery);
 		 	         
 		 	    		starIdSet.next();
 		 	    		
 		 	    		String maxStarId = starIdSet.getString("maxId");
 		 	    		
 		 	    		part = maxStarId.split("(?<=\\D)(?=\\d)");
 		 	    		
 		 	    		String nextIdStart = part[0];
 		 	    		Integer nextIdNumber = Integer.parseInt(part[1]);
 		 	    		nextIdNumber++;
 		 	    		
 		 	    		starId = nextIdStart + nextIdNumber;
 		 	    		
 		 	    		starIdSet.close();
 	    			}
 	    			
 	    			
 	    			//check to see if the genre exists
 	    			//if it does get the genre id
 	    			//else increment the genereId
 	    			
 	    			String genreQuery = "SELECT * FROM genres WHERE genres.name = ?";
 	    			PreparedStatement genreResultStatement = connection.prepareStatement(genreQuery);
 	    			genreResultStatement.setString(1, genreName);
 	    			
 	    			ResultSet genreResultSet = genreResultStatement.executeQuery();
 	    			
 	    			if(genreResultSet.next())
 	    			{
 	    				genreId = genreResultSet.getInt("id");
 	    			}
 	    			else
 	    			{
 	    				Statement statement = connection.createStatement();
 	    				genreQuery = "SELECT max(id) as maxId FROM genres";
 	    				
 	    				ResultSet genreIdSet = statement.executeQuery(genreQuery);
 	    				
 	    				genreIdSet.next();
 	    				
 	    				genreId = genreIdSet.getInt("maxId");
 	    				
 	    				genreId++;
 	    			}
 	    			
 	    			genreResultSet.close();
 	    			starResultSet.close();
 	    			
 	    		}
 	    		
 	    		/*
 	    		 add_movie 
 	    		 (IN newMovieId varchar(10), 
				IN movieTitle varchar(100), 
    			IN movieYear INT, 
    			IN movieDirector varchar(100), 
    			IN starName varchar(100), 
    			IN starId varchar(10), 
    			IN genreName varchar(32), 
    			IN genreId INT)
 	    		 */
 	    		
 	    		CallableStatement addMovie = connection.prepareCall("{call add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
 	    		
 	    		
 	    		addMovie.setString(1, movieId);
 	    		addMovie.setString(2, movieTitle);
 	    		addMovie.setInt(3, movieYear);
 	    		addMovie.setString(4, movieDirector);
 	    		addMovie.setString(5, starName);
 	    		addMovie.setString(6, starId);
 	    		addMovie.setString(7, genreName);
 	    		addMovie.setInt(8, genreId);
 	    		addMovie.setFloat(9, (float) movieRating);
 	    		
 	    		addMovie.execute();
 	    		
 	    		out.println("<p style=\"color:green;\">Movie Successfully Added.</p>");
 	    		
 	    		
 	    		addMovie.close();
 	    		movieResultSet.close();
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
	    	 
	    	out.println("</body>");
	 	    out.println("</html>");
	     }
	     
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
