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

public class StarXMLParser {
	List<Star> myStars;
	List<String> movieDbStars;
	String nextId;
	Document dom;
	
	public StarXMLParser()
	{
		myStars = new ArrayList<>();
		movieDbStars = new ArrayList<>();
		nextId = "";
	}
	
	public void runParse()
	{
		makeStarTable();
		
		parseXmlFile();
		
		parseDocument();
		
		//printData();
		
		writeToFile(); 
		addStarsToDb();
		//test(); 
	}
	
	public void parseXmlFile()
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			dom = db.parse("actors63.xml");
			
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
		NodeList n1 = docEle.getElementsByTagName("actor");
		
		if(n1 != null && n1.getLength() > 0)
		{
			for(int i = 0; i < n1.getLength(); i++)
			{
				//Get movie element
				Element starEl = (Element) n1.item(i);
				
				Star star = getStar(starEl);
				
				if(star.id.equals("0"))
				{
					System.out.println("Star " + star.name + " already exists.");
				}
				else
				{
					System.out.println("Adding " + star.toString());
					myStars.add(star);
				}
			}
		}
	}
	
	private Star getStar(Element starEl)
	{
		String name = getTextValue(starEl, "stagename");
		int birthYear = getIntValue(starEl, "dob");
		
		String id = "0";
		
		boolean found = checkStarInDb(name);
		
		if(!found)
		{
			id = nextId;
			getNextStarId();
		}
		
		Star s = new Star(id, name, birthYear);
		
		return s;
	}
	
	private boolean checkStarInDb(String starName)
	{
		boolean foundStar = false;
		int size = movieDbStars.size();
		
		for(int i = 0; i < size; i++)
		{
			if(starName != null)
			{
				if(starName.equalsIgnoreCase(movieDbStars.get(i)))
					foundStar = true;
			}
			
			if(foundStar)
				break;
		}
		
		return foundStar;
	}
	
	private void getNextStarId()
	{
		String[] part = nextId.split("(?<=\\D)(?=\\d)");
		int incId = Integer.parseInt(part[1]);
		incId++;
		
		nextId = part[0] + incId;
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
		
		private int getIntValue(Element movieEl, String tagName)
		{
			int intVal = 0;
			String year = getTextValue(movieEl, tagName);
			
			try
			{
				intVal = Integer.parseInt(year);
				
			}catch (NumberFormatException nfe)
			{
				intVal = 0;
			}
			return intVal;
		}
		
		private void printData()
		{
			System.out.println("No of Movies '" + myStars.size() + "'.");
			
			Iterator<Star> it = myStars.iterator();
			
			while(it.hasNext())
			{
				System.out.println(it.next().toString());
			}
		}
		
		public void writeToFile()
		{
			try {
				
				File file = new File("stardata.csv");
				
				if(file.exists())
					file.delete();
				
				PrintWriter pw = new PrintWriter(new File("stardata.csv"));
				
				Iterator<Star> it = myStars.iterator();
				
				System.out.println("Writing to file...");
				
				while(it.hasNext())
				{
					pw.write((it.next().toString()));
				}
				
				pw.close();
				
				System.out.println("Finished Writing to Star file.");
			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void makeStarTable()
		{
			String loginUser = "mytestuser";
	        String loginPasswd = "mypassword";
	        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
	        
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
				
				String query = "SELECT name FROM stars";
	      
				ResultSet rs = statement.executeQuery(query);
				
				while(rs.next())
				{
					String starName = rs.getString("name");
					
					movieDbStars.add(starName);
				}
				
				Statement maxStarId = connection.createStatement();
				
				String maxIdQuery = "SELECT max(id) as maxId FROM stars";
				
				ResultSet maxIdSet = maxStarId.executeQuery(maxIdQuery);
				maxIdSet.next();
				
				String maxId = maxIdSet.getString("maxId");
				
				String[] part = maxId.split("(?<=\\D)(?=\\d)");
				
				int incId = Integer.parseInt(part[1]);
				incId++;
				
				nextId = part[0] + incId;
	        
				rs.close();
				maxIdSet.close();
				maxStarId.close();
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
		
		public void addStarsToDb()
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
				
				String loadQuery = "LOAD DATA LOCAL INFILE 'stardata.csv' INTO TABLE stars FIELDS TERMINATED BY ',' (id, name, birthYear)";
				
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
		myStars.clear();
		movieDbStars.clear();
		
		File file = new File("stardata.csv");
		file.delete();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StarXMLParser sxp = new StarXMLParser();
		
		sxp.runParse();
	}

}
