import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class GenreXMLParser {

	List<Genre> myGenres;
	List<String> movieDbGenres;
	List<Integer> movieDbGenresIds;
	int nextGenreId;
	Document dom;
	
	public GenreXMLParser()
	{
		myGenres = new ArrayList<>();
		movieDbGenres = new ArrayList<>();
		movieDbGenresIds = new ArrayList<>();
		nextGenreId = 0;
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
				
				Genre genre = getGenre(movieEl);
				
				if(genre.id == -1 || genre.name == null)
				{
					System.out.println("Genre " + genre.name + " Exists");
				}
				else
				{
					System.out.println("Adding genre: " + genre.name);
					myGenres.add(genre);
				}
			}
		}
	}
	
	private Genre getGenre(Element movieEl)
	{
		//for each movie elment get values for
		//id, title, year, director
		
		String genreName = getTextValue(movieEl, "cat");
		if (genreName == null)
		{
			genreName = "Unknown";
		}
		
		int genreId = 0;
		//create new movieItem
		Genre g = new Genre(genreId, genreName);
		
		boolean newGenre = checkGenreInDb(g);

		return g;
	}
	
	private boolean checkGenreInDb(Genre genre)
	{
		boolean foundGenre = false;
		
		int size = movieDbGenres.size();
		
		for(int i = 0; i < size; i++)
		{
			if(genre.name != null)
			{
				if(genre.name.equalsIgnoreCase(movieDbGenres.get(i)))
				{
					foundGenre = true;
					genre.id= -1;
					System.out.println("foundGenre " + genre.name);
				}
						
			}
			
			if(foundGenre)
			{
				break;
			}
		}
		
		if(!foundGenre && genre.name != null)
		{
			genre.setId(nextGenreId);
			movieDbGenres.add(genre.name);
			movieDbGenresIds.add(nextGenreId);
			nextGenreId++;
		}
		
		return foundGenre;
	}
	
	private String getTextValue(Element movieEl, String tagName)
	{
		String textVal = null;
		NodeList n1 = movieEl.getElementsByTagName(tagName);
		if(n1 != null && n1.getLength() > 0)
		{
			Element el = (Element) n1.item(0);
			if(el.getFirstChild() == null)
				textVal = "N/A";
			else
			{
				textVal = el.getFirstChild().getNodeValue();
				if(textVal == null)
					textVal = "N/A";
			}
		}
		
		return textVal;
	}
	
	private void printData()
	{
		System.out.println("No of Genres '" + myGenres.size() + "'.");
		
		Iterator<Genre> it = myGenres.iterator();
		
		while(it.hasNext())
		{
			System.out.println(it.next().toString());
		}
	}
	
	public void makeMovieTable()
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
			
			String query = "SELECT name FROM genres ORDER BY id";
      
			ResultSet rs = statement.executeQuery(query);

			while(rs.next())
			{
				String genreName = rs.getString("name");

				movieDbGenres.add(genreName);
			}
			
			Statement maxIdStatement = connection.createStatement();
			
			String maxIdQuery = "SELECT max(id) maxId FROM genres";
			
			ResultSet maxIdSet = maxIdStatement.executeQuery(maxIdQuery);
			
			maxIdSet.next();
			
			int maxId = maxIdSet.getInt("maxId");
			
			nextGenreId = maxId;
			nextGenreId++;
			
			rs.close();
			maxIdSet.close();
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
	
	public void writeToFile()
	{
		try {
			
			File file = new File("genredata.txt");
			
			if(file.exists())
				file.delete();
			
			PrintWriter pw = new PrintWriter(new File("genredata.txt"));
			
			Iterator<Genre> it = myGenres.iterator();
			
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
	
	public void addGenresToDb()
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
			
			String loadQuery = "LOAD DATA LOCAL INFILE 'genredata.txt' INTO TABLE genres "
					+ "FIELDS TERMINATED BY ',' "
					+ "LINES TERMINATED BY '\n' "
					+ "(id, name)";
			
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
		//function to clear lists to save space no aws.
		myGenres.clear();
		movieDbGenres.clear();
		movieDbGenresIds.clear();
		
		File file = new File("genredata.txt");
		file.delete();
	}
	
	public void runParse()
	{
		makeMovieTable();
		
		parseXmlFile();
		
		parseDocument();
		
		//printData();
		
		writeToFile(); 
		
		addGenresToDb();
		//test(); 
	}
	
	public static void main(String[] args)
	{
		GenreXMLParser gxp = new GenreXMLParser();
		
		gxp.runParse();
	}
}
