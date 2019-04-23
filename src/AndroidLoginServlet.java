

import java.io.IOException;
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

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class AndroidLoginServlet
 */
@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/android-login")
public class AndroidLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AndroidLoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//get parameters
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		System.out.println(username);
		System.out.println(password);
        
        //verify username and password
        
        try {
        	
        	/*Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		// declare statement
    		*/
        	
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                System.out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/SlaveDB");
            
            if (ds == null)
                System.out.println("ds is null.");
            
            Connection connection = ds.getConnection();
    		
    		String query = "SELECT  * "
    				+ "FROM customers "
    				+ "WHERE email = ?";
    		
    		PreparedStatement statement = connection.prepareStatement(query);
    		
    		statement.setString(1, username);
    		
    		ResultSet resultSet = statement.executeQuery();
    		
    		//make jsonObject to send response object;
    		
    		JsonObject responseJsonObject = new JsonObject();
    		
    		if(resultSet.next())
    		{
    			boolean validatedPassword =  false;
    			
    			String encryptedPass = resultSet.getString("password");
    			
    			validatedPassword = new StrongPasswordEncryptor().checkPassword(password, encryptedPass);
        		
    			//if login success:
    			if(validatedPassword)
    			{
    				responseJsonObject.addProperty("status", "success");
    				responseJsonObject.addProperty("message", "Login Success");
    			}
    			else
    			{
    				//if the login fails
    				responseJsonObject.addProperty("status", "fail");
    				responseJsonObject.addProperty("message", "Incorrect username or password");
    			}
    		}
    		else
    		{
    			responseJsonObject.addProperty("status", "fail");
				responseJsonObject.addProperty("message", "Incorrect username or password");
			
    		}
    		
    		
    		if(responseJsonObject.get("status").getAsString().equals("success"))
    		{
    			//login was a success
    			request.getSession().setAttribute("loggedIn", "true");
    			response.getWriter().write(responseJsonObject.toString());
    			
    		}
    		else
    		{
    			response.getWriter().write(responseJsonObject.toString());
    		}
    	
    		
    		resultSet.close();
    		statement.close();
    		connection.close();
    		
        }  catch (Exception e) {
        	e.printStackTrace();
        }

        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	

}
