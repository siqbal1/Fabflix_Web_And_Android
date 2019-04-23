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

public class StarsInMovieXMLParser {

	
		// TODO Auto-generated method stub
		List<StarsInMovie> myStarsInMovies;
		List<String> movieDbMovieIds;
		Map<String, String> movieDbStars;
		Document dom;
		
		public StarsInMovieXMLParser()
		{
			myStarsInMovies = new ArrayList<>();
			movieDbMovieIds = new ArrayList<>();
			movieDbStars = new HashMap<String, String>();
		}
		
		public void runParse()
		{
			makeDbTables();
			
			parseXmlFile();
			
			parseDocument();
			
			printData();
			
			writeToFile(); 
			
			addToDb();
			//test(); 
		}
		
		private void printData()
		{
			System.out.println("No of Movies '" + myStarsInMovies.size() + "'.");
			
			Iterator<StarsInMovie> it = myStarsInMovies.iterator();
			
			while(it.hasNext())
			{
				System.out.println(it.next().toString());
			}
		}
		
		public void parseXmlFile()
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			try
			{
				DocumentBuilder db = dbf.newDocumentBuilder();
				
				dom = db.parse("casts124.xml");
				
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
			NodeList n1 = docEle.getElementsByTagName("m");
			
			if(n1 != null && n1.getLength() > 0)
			{
				for(int i = 0; i < n1.getLength(); i++)
				{
					//Get movie element
					Element movieEl = (Element) n1.item(i);
					
					StarsInMovie starsInMovie = getStarsInMovie(movieEl);
					
					if(starsInMovie.movieId.equals("NULL") 
							|| starsInMovie.starId.equals("NULL"))
					{
						System.out.println("Error: Star or Movie Does Not Exist.");
						System.out.println(starsInMovie.toString());
					}
					else
					{
						System.out.println("Adding stars in movie " + starsInMovie.toString());
						myStarsInMovies.add(starsInMovie);
					}
				}
			}
		}
		
		private StarsInMovie getStarsInMovie(Element movieEl)
		{
			String starName = getTextValue(movieEl, "a");
			String movieId = getTextValue(movieEl, "f");
			
			String starId = getStarId(starName);
			
			boolean found = checkMovieInDb(movieId);
			
			if(!found)
			{
				movieId = "NULL";
			}
			
			StarsInMovie s = new StarsInMovie(starId, movieId);
			
			return s;
		}
		
		private boolean checkMovieInDb(String movieId)
		{
			boolean found = false;
			int size = movieDbMovieIds.size();
			
			for(int i = 0; i < size; i++)
			{
				if(movieId != null)
				{
					if(movieId.equals(movieDbMovieIds.get(i)))
						found = true;
				}
				
				if(found)
					break;
			}
			
			return found;
		}
		
		private String getStarId(String starName)
		{
			boolean found = false;
			String starId = "NULL";
			
			for(Map.Entry<String, String> star : movieDbStars.entrySet())
			{
				String name = star.getValue();
				
				if(starName != null)
				{
					if(starName.equals(name))
					{
						starId = star.getKey();
						found = true;
					}
				}
				
				if(found)
					break;
			}

			return starId;
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
		
		public void writeToFile()
		{
			try {
				
				File file = new File("stardata.csv");
				
				if(file.exists())
					file.delete();
				
				PrintWriter pw = new PrintWriter(new File("starsinmoviedata.csv"));
				
				Iterator<StarsInMovie> it = myStarsInMovies.iterator();
				
				System.out.println("Writing to file...");
				
				while(it.hasNext())
				{
					pw.write((it.next().toString()));
				}
				
				pw.close();
				
				System.out.println("Finished Writing to StarsInMovie file.");
			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void makeDbTables()
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
				
				Statement starStatement = connection.createStatement();
				
				String starQuery = "SELECT id, name FROM stars";
	      
				ResultSet rs = starStatement.executeQuery(starQuery);
				
				while(rs.next())
				{
					String starId = rs.getString("id");
					String starName = rs.getString("name");
					
					movieDbStars.put(starId, starName);
				}
				
				Statement  movieStatement = connection.createStatement();
				
				String movieQuery = "SELECT id FROM movies";
				
				ResultSet movieSet = movieStatement.executeQuery(movieQuery);
				
				while(movieSet.next())
				{
					String movieId = movieSet.getString("id");
					
					movieDbMovieIds.add(movieId);
				}
				
				rs.close();
				movieSet.close();
				movieStatement.close();
				starStatement.close();
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
		
		public void addToDb()
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
				
				String loadQuery = "LOAD DATA LOCAL INFILE 'starsinmoviedata.csv' INTO TABLE stars_in_movies FIELDS TERMINATED BY ',' (starId, movieId)";
				
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
			myStarsInMovies.clear();
			movieDbMovieIds.clear();
			movieDbStars.clear();
			
			File file = new File("starsinmoviedata.csv");
			file.delete();
		}
		
		public static void main(String[] args) 
		{
			StarsInMovieXMLParser smxp = new StarsInMovieXMLParser();
			
			smxp.runParse();
		}

}
