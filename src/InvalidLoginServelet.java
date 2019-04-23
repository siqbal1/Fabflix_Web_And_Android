

import java.io.IOException;
import java.io.PrintWriter;
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
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * Servlet implementation class InvalidLoginServelet
 */
@WebServlet("/loginAgain")
public class InvalidLoginServelet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InvalidLoginServelet() {
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
    		
    		String query = "SELECT  * "
    				+ "FROM customers "
    				+ "WHERE email = ? ";
    		
    		PreparedStatement statement = connection.prepareStatement(query);
    		
    		statement.setString(1, username);

    		ResultSet resultSet = statement.executeQuery();
        	int numOfAccounts = 0;
        	
    		if(resultSet.next())
    		{
    			boolean validatedPassword = false;
    		
    			String encryptedPass = resultSet.getString("password");
    			validatedPassword = new StrongPasswordEncryptor().checkPassword(pass, encryptedPass);
    		
    			if(validatedPassword)
    			{
    				String customerId = resultSet.getString("id");
    				session.setAttribute("loggedIn", true);
    				session.setAttribute("customerId", customerId);
    				response.sendRedirect("mainPage.html");
    			}
    			else
    			{
    				response.sendRedirect("invalidLogin.html");
    			}
    		}
    		else
    		{
    			response.sendRedirect("invalidLogin.html");
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
