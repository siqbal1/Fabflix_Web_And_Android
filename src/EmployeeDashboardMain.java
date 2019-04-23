

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.DatabaseMetaData;

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
 * Servlet implementation class EmployeeDashboard
 */
@WebServlet("/EmployeeDashboardMain")
public class EmployeeDashboardMain extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeeDashboardMain() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
	        
	     String username = request.getParameter("username");
	     String pass = request.getParameter("password");
	     
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
	        		"			<li><a class=\"active\"href=\"EmployeeDashboardMain\">MetaData</a></li>\r\n" + 
	        		"			<li><a href=\"EmployeeAddStar\">Add Star</a></li>\r\n" + 
	        		"			<li><a href=\"EmployeeDashboardAddMovie\">Add Movie</a></li>\r\n" + 
	        		"		</ul>"  + 
	        		"</div>");
	     
	    out.println("<h2>Database MetaData</h2>");
	     
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
	    		
	    		DatabaseMetaData metadata = connection.getMetaData();
	    		
	    		//Make string array to keep track of tables
	    		String table[] = {"TABLE"};
	    		ArrayList<String> databaseTables = new ArrayList();
	    		
	    		ResultSet resultSet = metadata.getTables(null, null, null, table);
	    		
	    		while(resultSet.next())
	    		{
	    			databaseTables.add(resultSet.getString("TABLE_NAME"));
	    		}
	    		
	    		ResultSet tableDataSet = null;
	    		
	    		for(String tableNames : databaseTables)
	    		{
	    			tableDataSet = metadata.getColumns(null, null, tableNames, null);
	    			
	    			out.println("<p>" + tableNames.toUpperCase() + "<br>");
	    			
	    			while(tableDataSet.next())
	    			{
	    				out.println(tableDataSet.getString("COLUMN_NAME") + " "
	    						+ tableDataSet.getString("TYPE_NAME") + " "
	    						+ tableDataSet.getString("COLUMN_SIZE") + "<br>");
	    				
	    			}
	    			
	    			out.println("<br></p>");
	    		}
	    		
	    		tableDataSet.close();
	    		resultSet.close();
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
