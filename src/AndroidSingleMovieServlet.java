

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class AndroidSingleMovieServlet
 */
@WebServlet(name = "AndroidSingleMovieServlet", urlPatterns = "/api/android-singleMovie")

public class AndroidSingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AndroidSingleMovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String movieId = request.getParameter("movieId");
		
		System.out.println(movieId);
        
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        
        try
        {
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/SlaveDB");
            
            if (ds == null)
                out.println("ds is null.");
            
            Connection connection = ds.getConnection();
    		
    		String query = 
    				"SELECT movies.id, movies.title, movies.director, movies.year, GROUP_CONCAT(DISTINCT genres.name) as genres, GROUP_CONCAT(DISTINCT stars.name) as stars\r\n" + 
    				"FROM movies, stars, stars_in_movies, genres, genres_in_movies\r\n" +  
    				"WHERE movies.id = ? \n" +
    				"   AND movies.id = genres_in_movies.movieId\r\n" + 
    				"	AND genres.id = genres_in_movies.genreId\r\n" + 
    				"	AND movies.id = stars_in_movies.movieId\r\n" + 
    				"	AND stars_in_movies.starId = stars.id\r\n";
    		
    		PreparedStatement statement = connection.prepareStatement(query);

    		statement.setString(1, movieId);
    		
    		ResultSet resultSet = statement.executeQuery();
    		
    		JsonObject jsonObject = new JsonObject();
    		
    		if(resultSet.next())
    		{
    			String movieTitle = resultSet.getString("title");
    			String movieYear = resultSet.getString("year");
    			String movieDirector = resultSet.getString("director");
    			String movieGenres = resultSet.getString("genres");
    			String starsList = resultSet.getString("stars");
    			
    			//make jsonObject
    			jsonObject.addProperty("movieTitle", movieTitle);
    			jsonObject.addProperty("movieDirector", movieDirector);
    			jsonObject.addProperty("movieYear", movieYear);
    			jsonObject.addProperty("movieGenres", movieGenres);
    			jsonObject.addProperty("starsList", starsList);
    		}
    		
    		out.write(jsonObject.toString());
    		System.out.println(jsonObject.toString());
    		response.setStatus(200);
    		
    		statement.close();
    		resultSet.close();
    		connection.close();

        } catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
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
