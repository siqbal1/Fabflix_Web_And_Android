

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
 * Servlet implementation class ConfirmationPageServelet
 */
@WebServlet("/ConfirmationPage")
public class ConfirmationPageServelet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmationPageServelet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

        //set response mime type
        response.setContentType("text/html");
        
        //get printwriter function to write response
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(true);
        
        out.println("<html>\r\n" + 
        		"<head>\r\n" + 
        		"<meta charset=\"ISO-8859-1\">\r\n" + 
        		"<title>Fabflix Main Page</title>\r\n" + 
        		"<link rel=\"stylesheet\" href=\"SearchPage.css\"/>\r\n" + 
        		"</head>\r\n" + 
        		"\r\n" + 
        		"<div>\r\n" + 
        		"	<h1>\r\n" + 
        		"		<img style=\"vertical-align:middle\" src=\"cameraIcon.png\" alt=\"Camera Icon\" width=\"75\" height=\"75\">\r\n" + 
        		"		<span class=\"title\">Fabflix</span>\r\n" + 
        		"	</h1>\r\n" + 
        		"\r\n" + 
        		"		<ul id=\"navigationBar\">\r\n" + 
        		"			<li><a href=\"mainPage.html\">Home</a></li>\r\n" + 
        		"			<li><a href=\"Browse\">Browse</a></li>\r\n" + 
        		"			<li><a href=\"advancedSearch.html\">Advanced Search</a></li>\r\n" + 
        		"			<li><a class=\"active\" href=\"Checkout\">Checkout</a></li>\r\n" + 
        		"			<li><form action = \"Search\" id=\"searchBar\">\r\n" + 
        		"				Search:<input type=\"text\" name=\"itemSearch\">\r\n" + 
        		"				<input type=\"submit\" value=\"Search\"></form></li>\r\n" + 
        		"		</ul>\r\n" + 
        		"</div>");
        
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String ccNumber = request.getParameter("ccNumber");
        String ccExp = request.getParameter("ccExp");
        

		try {
			
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
    
    		
    		String query = "SELECT * \n"
    				+ "FROM creditcards \n"
    				+ "WHERE id = ? \n"
    						+ "AND firstName = ? \n"
    						+ "AND lastName = ? \n"
    						+ "AND expiration = ? \n";
    		
    		
    		PreparedStatement statement = connection.prepareStatement(query);
    		
    		statement.setString(1, ccNumber);
    		statement.setString(2, firstName);
    		statement.setString(3, lastName);
    		statement.setString(4, ccExp);
    		
    		ResultSet resultSet = statement.executeQuery();
    		
    		boolean foundCard = false;
    		
    		if(resultSet.next())
    		{
    			foundCard = true;
    		}
    		
    		if(!foundCard)
    		{
    			response.sendRedirect("incorrectPaymentInformation.html");
    		}
    		else
    		{
    			String customerId = (String) session.getAttribute("customerId");
    			ArrayList<String> previousItemsIds = (ArrayList<String>) session.getAttribute("previousItemsIds");
    			ArrayList<Integer> previousItemsCount = (ArrayList<Integer>) session.getAttribute("previousItemsCount");
    	        
    			
    			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    			Calendar cal = Calendar.getInstance();
    			String currentDate = dateFormat.format(cal.getTime());
    			
    			Statement updateStatement = connection.createStatement();
    			query = "INSERT INTO sales (customerId, movieId, sale_date) VALUES ";
    			
    			for(int i = 0; i < previousItemsIds.size(); i++)
    			{
    				int movieCount = previousItemsCount.get(i);
    				String movieId = previousItemsIds.get(i);
    				
    				for(int j = 0; j < movieCount; j++)
    					query += "(\"" + customerId + "\", \"" + movieId + "\", \"" + currentDate + "\"), ";
    			}
    			
    			query = query.substring(0, query.length() - 2);
    			
    			int numRows = updateStatement.executeUpdate(query);
    			
    			response.sendRedirect("orderComplete.html");
    			updateStatement.close();
    		}
    		
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
