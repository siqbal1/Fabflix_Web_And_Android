

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

// this annotation maps this Java Servlet Class to a URL
@WebServlet("/stars")
public class StarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public StarServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password
		
        //create string to setup path for CSS file
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>"
        		+ "<title>Fabflix</title>"
        		+ "<link rel='stylesheet' href='style.css'>"
        		+ "</head>");
        
        
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
        		// declare statement
        		Statement statement = connection.createStatement();
        		// prepare query
        		String query = "SELECT movieCastList.title, movieCastList.director, movieCastList.year, genresForMovies.rating, genreList, starsList\n" + 
        				"FROM \n" + 
        				"	(SELECT topMovies.movieId, movies.title, movies.director, movies.year, topMovies.rating, GROUP_CONCAT(genres.name SEPARATOR ', ') as genreList\n" + 
        				"    FROM (SELECT * FROM ratings ORDER BY rating DESC LIMIT 20) as topMovies, movies, genres_in_movies, genres\n" + 
        				"    WHERE topMovies.movieId = movies.id\n" + 
        				"		AND topMovies.movieId = genres_in_movies.movieId\n" + 
        				"		AND genres_in_movies.genreId = genres.id\n" + 
        				"	GROUP BY movies.id, movies.title, movies.director, movies.year) as genresForMovies\n" + 
        				"RIGHT JOIN\n" + 
        				"    (SELECT topMovies.movieId, movies.title, movies.director, movies.year, GROUP_CONCAT(stars.name SEPARATOR ', ') as starsList, topMovies.rating\n" + 
        				"    FROM (SELECT * FROM ratings ORDER BY rating DESC LIMIT 20) as topMovies, movies, stars_in_movies, stars\n" + 
        				"    WHERE topMovies.movieId = movies.id \n" + 
        				"		AND topMovies.movieId = stars_in_movies.movieId\n" + 
        				"        AND stars_in_movies.starId = stars.id\n" + 
        				"	GROUP BY movies.id, movies.title, movies.director, movies.year) as movieCastList\n" + 
        				"ON genresForMovies.movieId = movieCastList.movieId\n" + 
        				"ORDER BY genresForMovies.rating DESC";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);

        		out.println("<body>");
        		out.println("<h1>Highest Rated Movies</h1>");
        		
        		out.println("<table border>");
        		
        		// add table header row
        		out.println("<tr>");
        		out.println("<td>Title</td>");
        		out.println("<td>Year</td>");
        		out.println("<td>Director</td>");
        		out.println("<td>Rating</td>");
        		out.println("<td>Genres</td>");
        		out.println("<td>Stars</td>");
        		out.println("</tr>");
        		
        		// add a row for every star result
        		while (resultSet.next()) {
        			// get a star from result set
        			String movieTitle = resultSet.getString("title");
        			String movieYear = resultSet.getString("year");
        			String movieDirector = resultSet.getString("director");
        			String movieRating = resultSet.getString("rating");
        			String movieGenres = resultSet.getString("genreList");
        			String movieStars = resultSet.getString("starsList");
        			/*
        			String starID = resultSet.getString("id");
        			String starName = resultSet.getString("name");
        			String birthYear = resultSet.getString("birthyear");
        			*/
        			out.println("<tr>");
        			out.println("<td>" + movieTitle + "</td>");
        			out.println("<td>" + movieYear + "</td>");
        			out.println("<td>" + movieDirector + "</td>");
        			out.println("<td>" + movieRating + "</td>");
        			out.println("<td>" + movieGenres + "</td>");
        			out.println("<td>" + movieStars + "</<td>");
        			out.println("</tr>");
        		}
        		
        		out.println("</table>");
        		
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


}
