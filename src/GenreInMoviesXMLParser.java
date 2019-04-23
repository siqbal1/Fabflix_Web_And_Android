import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GenreInMoviesXMLParser {
	List<GenreMovies> myGenreMovies;
	HashMap<Integer, String> movieDbGenres;
	List<String> movieDbMovieIds;
	Document dom;
	int unknownGenreId;
	public GenreInMoviesXMLParser()
	{
		movieDbGenres = new HashMap<Integer, String>();
		myGenreMovies = new ArrayList<>();
		movieDbMovieIds = new ArrayList<>();
		unknownGenreId = 0;
	}
	
	public void parseXmlFile()
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			dom = db.parse("mains243.xml");
			
		 } catch (ParserConfigurationException pce) {
	            pce.printStackTrace();
	     } catch (SAXException se) {
	            se.printStackTrace();
	     } catch (IOException ioe) {
	            ioe.printStackTrace();
	     }
	}
	
	private void parseDocument()
	{
		//get root element
		Element docEle = dom.getDocumentElement();
		
		//get nodelist of <movies> elements
		NodeList n1 = docEle.getElementsByTagName("film");
		
		if(n1 != null && n1.getLength() > 0)
		{
			for(int i = 0; i < n1.getLength(); i++)
			{
				//Get movie element
				Element movieEl = (Element) n1.item(i);
				
				GenreMovies genreMovie = getGenreMovie(movieEl);
				
				if(genreMovie.movieId == null || genreMovie.movieId.equals("NPID") || genreMovie.movieId.equals("NULL"))
				{
					System.out.println("Movie is not present in database or genre val Null.");
					System.out.println(genreMovie.toString());
				}
				else
				{
					System.out.println("Adding " + genreMovie.toString());
					myGenreMovies.add(genreMovie);
				}
				
			}
		}
	}
	
	private GenreMovies getGenreMovie(Element movieEl)
	{
		//for each movie elment get values for
		//id, title, year, director
		String movieId = getTextValue(movieEl, "fid");
		String genreName = getTextValue(movieEl, "cat");
		
		checkMovieExists(movieId);
		int genreId = getGenreId(genreName);
		
		GenreMovies gm = new GenreMovies(genreId, movieId);
		
		return gm;	
	}
	
	private boolean checkMovieExists(String movieId)
	{
		boolean foundMovie = false;
		int size = movieDbMovieIds.size();
		
		for(int i = 0; i < size; i++)
		{
			if (movieId != null && movieDbMovieIds.get(i) != null)
			{
				if(movieId.equalsIgnoreCase(movieDbMovieIds.get(i)))
				{
					foundMovie = true;
				}
			}
			
			if(foundMovie)
				break;
		}
		
		if(!foundMovie)
			movieId = "NPID"; //not present in db
		
		return foundMovie;
	}
	
	private int getGenreId(String genreName)
	{
		boolean foundGenre = false;
		int genreId = unknownGenreId;
		
		for (Entry<Integer, String> entry : movieDbGenres.entrySet()) {
		    Integer id = entry.getKey();
		    String name = entry.getValue();
		    
		    if(name.equalsIgnoreCase(genreName))
		    {
		    	genreId = id;
		    	foundGenre = true;
		    }
		    
		    if(foundGenre)
		    	break;
		}
		
		return genreId;
	}
	
	private String getTextValue(Element movieEl, String tagName)
	{
		String textVal = null;
		NodeList n1 = movieEl.getElementsByTagName(tagName);
		if(n1 != null && n1.getLength() > 0)
		{
			Element el = (Element) n1.item(0);
			if(el.getFirstChild() == null)
				textVal = "NULL";
			else
			{
				textVal = el.getFirstChild().getNodeValue();
				if(textVal == null)
					textVal = "NULL";
			}
		}
		
		return textVal;
	}
	
	private void printData()
	{
		System.out.println("No of Movies '" + myGenreMovies.size() + "'.");
		
		Iterator<GenreMovies> it = myGenreMovies.iterator();
		
		while(it.hasNext())
		{
			System.out.println(it.next().toString());
		}
	}
	public void makeGenreTable()
	{
        
        try
        {
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                System.out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/MasterDB");
            
            if (ds == null)
                System.out.println("ds is null.");
			
            Connection connection = ds.getConnection();
            
			Statement statement = connection.createStatement();
			
			String query = "SELECT * FROM genres";
      
			ResultSet rs = statement.executeQuery(query);

			while(rs.next())
			{
				int genreId = rs.getInt("id");
				String genreName = rs.getString("name");
				movieDbGenres.put(genreId, genreName);
				
				if(genreName.equalsIgnoreCase("N/A"))
					unknownGenreId = genreId;
			}

			rs.close();
			
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
        	System.out.println("Exception in doGet: " + e.getMessage());
        }
	}
	
	
	public void makeMovieTable()
	{
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
        try
        {
	        Class.forName("com.mysql.jdbc.Driver").newInstance();
			// create database connection
			Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			
			Statement statement = connection.createStatement();
			
			String query = "SELECT id FROM movies";
      
			ResultSet rs = statement.executeQuery(query);

			while(rs.next())
			{
				String movieId = rs.getString("id");
				movieDbMovieIds.add(movieId);
			}

			rs.close();
			
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
        	System.out.println("Exception in doGet: " + e.getMessage());
        }
	}
	
	public void writeToFile()
	{
		try {
			
			File file = new File("genredata.csv");
			
			if(file.exists())
				file.delete();
			
			PrintWriter pw = new PrintWriter(new File("genremovies.csv"));
			
			Iterator<GenreMovies> it = myGenreMovies.iterator();
			
			System.out.println("Writing to file...");
			
			while(it.hasNext())
			{
				pw.write((it.next().toString()));
			}
			
			pw.close();
			
			System.out.println("Finished Writing to Genre file.");
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addGenreMoviesToDb()
	{
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
        try
        {
	        Class.forName("com.mysql.jdbc.Driver").newInstance();
			// create database connection
			Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			
			Statement statement = connection.createStatement();
			
			String loadQuery = "LOAD DATA LOCAL INFILE 'genremovies.csv' INTO TABLE genres_in_movies FIELDS TERMINATED BY ',' (genreId, movieId)";
			
			statement.executeQuery(loadQuery);
			
			System.out.println("Loading Complete");
			
			clearContent();
			
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
            	System.out.println("Exception in doGet: " + e.getMessage());
    	}
		
	}
	
	public void clearContent()
	{
		myGenreMovies.clear();
		movieDbGenres.clear();
		movieDbMovieIds.clear();
		
		File file = new File("genremovies.csv");
		file.delete();
	}
	
	public void runParse()
	{
		makeMovieTable();
		
		makeGenreTable();
		
		parseXmlFile();
		
		parseDocument();
		
		//printData();
		
		writeToFile(); 
		
		addGenreMoviesToDb();
		//test(); 
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GenreInMoviesXMLParser gmxp = new GenreInMoviesXMLParser();
		
		gmxp.runParse();
	}

}
