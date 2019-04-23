

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * Servlet implementation class AndroidSearchServlet
 */
@WebServlet(name = "AndroidSearchServlet", urlPatterns = "/api/android-search")

public class AndroidSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//list of total results
	public static final int LIMIT_SIZE = 10;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AndroidSearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String movieSearch = request.getParameter("movieSearch");
		
		String offset = request.getParameter("offset");
		
		if (offset == null || offset.isEmpty())
			offset = "0";
		
		System.out.println(movieSearch);
		
        
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
    				"WHERE MATCH(movies.title) AGAINST (? IN BOOLEAN MODE) \n" +
    				"   AND movies.id = genres_in_movies.movieId\r\n" + 
    				"	AND genres.id = genres_in_movies.genreId\r\n" + 
    				"	AND movies.id = stars_in_movies.movieId\r\n" + 
    				"	AND stars_in_movies.starId = stars.id\r\n" + 
    				"GROUP BY movies.id, movies.title, movies.director, movies.year \r\n" + 
    				"ORDER BY title \n" + 
    				"LIMIT " + LIMIT_SIZE + "\n" +
    				"OFFSET " + offset;
    		
    		PreparedStatement statement = connection.prepareStatement(query);
    		
    		statement.setString(1, movieSearch);
    		
    		ResultSet resultSet = statement.executeQuery();
    		
    		//Make jsonArray to hold jsonObjects
    		JsonArray jsonArray = new JsonArray();
    		
    		while(resultSet.next())
    		{
    			String movieId = resultSet.getString("id");
    			String movieTitle = resultSet.getString("title");
    			String movieYear = resultSet.getString("year");
    			String movieDirector = resultSet.getString("director");
    			String movieGenres = resultSet.getString("genres");
    			String starsList = resultSet.getString("stars");

    			
    			//make jsonObject to store data retrieved
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("movieId", movieId);
    			jsonObject.addProperty("movieTitle", movieTitle);
    			jsonObject.addProperty("movieDirector", movieDirector);
    			jsonObject.addProperty("movieYear", movieYear);
    			jsonObject.addProperty("movieGenres", movieGenres);
    			jsonObject.addProperty("starsList", starsList);
    			
    			
    			jsonArray.add(jsonObject);
    		}
    		
    		out.write(jsonArray.toString());
    		System.out.println(jsonArray.toString());
    		//set response status to 200 (ok)
    		response.setStatus(200);
    		
    		resultSet.close();
    		statement.close();
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
