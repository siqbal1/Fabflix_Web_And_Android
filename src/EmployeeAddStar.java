

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
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * Servlet implementation class EmployeeAddStar
 */
@WebServlet("/EmployeeAddStar")
public class EmployeeAddStar extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeeAddStar() {
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
	        		"			<li><a class=\"active\" href=\"EmployeeAddStar\">Add Star</a></li>\r\n" + 
	        		"			<li><a href=\"EmployeeDashboardAddMovie\">Add Movie</a></li>\r\n" + 
	        		"		</ul>"  + 
	        		"</div>");
	     out.println("<body>");
	     
	     out.println("<h2>Add Star<h2>");
	     out.println("<br>");
	     
	     //make form to take in star input
	     //star 
	     
	     out.println("<form action=\"EmployeeAddStar\" method=\"GET\">"
	     		+ "Star Name:<input type=\"text\" name=\"starName\"> <br>"
	     		+ "Birth Year:<input type=\"text\" name=\"birthYear\"> <br>"
	     		+ "<input type=\"hidden\" name=\"requestSent\" value=\"true\">"
	     		+ "<input type=\"submit\" value=\"Add Star\">"
	     		+ "</form>");
	     
	     if(request.getParameterMap().containsKey("starName") && request.getParameter("starName").isEmpty())
	     {
	    	 out.println("<p style=\"color:red;\"> Error: Star Name cannot be empty. </p>");
	     }	 
	    	 
	 		
	 	 if(request.getParameterMap().containsKey("requestSent") && !request.getParameter("starName").isEmpty())
	 	     {
	 	    	 String starName = "";
	 	    	 Integer birthYear = 0;
	 	     
	 	    	 if(request.getParameterMap().containsKey("starName"))
	 	    		 starName = request.getParameter("starName");
	 	    	 
	 	    	 if(request.getParameterMap().containsKey("birthYear"))
	 	    		 birthYear = Integer.parseInt(request.getParameter("birthYear"));
	 	    	 
	 	    	 
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
	 	    		Statement statement = connection.createStatement();
	 	    		
	 	    	 
	 	    		String starIdQuery = "SELECT max(id) as maxId FROM stars";
	 	    		ResultSet starIdSet = statement.executeQuery(starIdQuery);
	 	         
	 	    		starIdSet.next();
	 	    		
	 	    		String maxStarId = starIdSet.getString("maxId");
	 	    		
	 	    		String[] part = maxStarId.split("(?<=\\D)(?=\\d)");
	 	    		
	 	    		String nextIdStart = part[0];
	 	    		Integer nextIdNumber = Integer.parseInt(part[1]);
	 	    		//get Next Id
	 	    		nextIdNumber++;
	 	    		
	 	    		String insertStarQuery;
	 	    		if(birthYear == 0)
	 	    		{
	 	    			insertStarQuery = "INSERT INTO stars (id, name) VALUES(\"" + nextIdStart + nextIdNumber + "\","
	 	    					+ "\"" + starName + "\")";
	 	    		}
	 	    		else
	 	    		{
	 	    			insertStarQuery = "INSERT INTO stars (id, name, birthYear) VALUES(\"" + nextIdStart + nextIdNumber + "\","
	 	    					+ "\"" + starName + "\", \"" + birthYear + "\")";
	 	    		}
	 	    		
	 	    		out.println("<p style=\"color:green;\">Star has been added.</p>");
	 	    		
	 	    		Statement updateStatement = connection.createStatement();
	 	    		int starAdded = updateStatement.executeUpdate(insertStarQuery);
	 	    		
	 	    		updateStatement.close();
	 	    		connection.close();
	 	    		starIdSet.close();
	 	    		
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
