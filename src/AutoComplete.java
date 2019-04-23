

import java.io.IOException;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class AutoComplete
 */
@WebServlet("/AutoComplete")
public class AutoComplete extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AutoComplete() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
        
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
    		
    		//handle autocomplete lookup
    		JsonArray jsonArray = new JsonArray();
    		
    		String searchQuery = request.getParameter("query");
    		
    		//return the empty json array if query is null or empty
    		if (searchQuery == null || searchQuery.trim().isEmpty())
    		{
    			response.getWriter().write(jsonArray.toString());
    			return;
    		}
    		
    		//split the searchQuery into different parts
    		String[] parts = searchQuery.split("\\s+");
    		
    		String fullTextSearch = "";
    		
    		for(int i = 0; i < parts.length; i++)
    		{
    			fullTextSearch += "+" + parts[i] + "* ";
    		}
    		
    		//search the database for results and add the results to Json array
    		String movieSearch = "SELECT id, title "
    				+ "FROM movies "
    				+ "WHERE MATCH(title) AGAINST(? IN BOOLEAN MODE) "
    				+ "LIMIT 10";
    		
    		PreparedStatement movieStatement = connection.prepareStatement(movieSearch);
    		
    		movieStatement.setString(1, fullTextSearch);
    		
    		ResultSet movieSet = movieStatement.executeQuery();
    		
    		while(movieSet.next())
    		{
    			String movieId = movieSet.getString("id");
    			String movieTitle = movieSet.getString("title");
    			
    			System.out.println(movieId + ", " + movieTitle);
    			
    			jsonArray.add(generateJsonObject(movieId, movieTitle));
    			
    		}
    		
    		response.getWriter().write(jsonArray.toString());
    		System.out.println(jsonArray.toString());
    		
    		movieSet.close();
    		movieStatement.close();
    		connection.close();
    		
    		return;
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
    		
    		System.out.println("e");
    		response.sendError(500, e.getMessage());
		}

	}
	
	private static JsonObject generateJsonObject(String movieId, String movieTitle) {
		// TODO Auto-generated method stub
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", movieTitle);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("movieId", movieId);
		additionalDataJsonObject.addProperty("category", "Movie");
		
		
		jsonObject.add("data", additionalDataJsonObject);
		
		return jsonObject;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
