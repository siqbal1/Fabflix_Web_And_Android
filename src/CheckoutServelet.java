

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CheckoutServelet
 */
@WebServlet("/Checkout")
public class CheckoutServelet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckoutServelet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stubbing to setup path for css file
        
        //set response mime type
        response.setContentType("text/html");
        
        //get printwriter function to write response
        PrintWriter out = response.getWriter();
       
        
        out.println("<!DOCTYPE html>\r\n" + 
        		"<html>\r\n" + 
        		"<head>\r\n" + 
        		"<meta charset=\"ISO-8859-1\">\r\n" + 
        		"<title>Fabflix Checkout Page</title>\r\n" + 
        		"\r\n" + 
        		" <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\r\n" + 
        		"\r\n" + 
        		"    <!-- include jquery autocomplete JS  -->\r\n" + 
        		" <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.devbridge-autocomplete/1.4.7/jquery.autocomplete.min.js\"></script>\r\n" + 
        		"    \r\n" + 
        		"<link rel=\"stylesheet\" href=\"checkoutPage.css\"/>\r\n" + 
        		"</head>\r\n" + 
        		"\r\n" + 
        		"<div>\r\n" + 
        		"	<h1>\r\n" + 
        		"		<img style=\"vertical-align:middle\" src=\"cameraIcon.png\" alt=\"Camera Icon\" width=\"75\" height=\"75\">\r\n" + 
        		"		<span class=\"title\">Fabflix</span>\r\n" + 
        		"	</h1>\r\n" + 
        		"\r\n" + 
        		"		<ul id=\"navigationBar\">\r\n" + 
        		"			<li><a href=\"mainPage.html\" onClick=\"loadCache();\">Home</a></li>\r\n" + 
        		"			<li><a href=\"Browse\" onClick=\"loadCache();\">Browse</a></li>\r\n" + 
        		"			<li><a href=\"advancedSearch.html\" onClick=\"loadCache();\">Advanced Search</a></li>\r\n" + 
        		"			<li><a class=\"active\" href=\"Checkout\" onClick=\"loadCache();\">Checkout</a></li>\r\n" + 
        		"			<li><form action = \"Search\" id=\"searchBar\">\r\n" + 
        		"				Search:<input type=\"text\" name=\"itemSearch\" id=\"autocomplete\">\r\n" + 
        		"				<input type=\"submit\" onclick=\"handleNormalSearch(document.getElementById('autocomplete').value)\" value=\"Search\"></form>\r\n" + 
        		"				\r\n" + 
        		"				<script src=\"autocomplete.js\"></script>\r\n" + 
        		"			</li>\r\n" + 
        		"		</ul>\r\n" + 
        		"</div>\r\n");
        
        out.println("<body>");
        
        //make a shopping list
        //store in session so that the cart exists for the entire time user is logged in
        HttpSession session = request.getSession(true);
        
        ArrayList<String> previousItemsIds = (ArrayList<String>) session.getAttribute("previousItemsIds");
        ArrayList<String> previousItemsTitles = (ArrayList<String>) session.getAttribute("previousItemsTitles");
        ArrayList<Integer> previousItemsCount = (ArrayList<Integer>) session.getAttribute("previousItemsCount");
        
        //if updating cart items
        
        
        if (previousItemsIds == null) {
            previousItemsIds = new ArrayList<>();
            previousItemsTitles = new ArrayList<>();
            previousItemsCount = new ArrayList<>();
            
            session.setAttribute("previousItemsIds", previousItemsIds); // Add the newly created ArrayList to session, so that it could be retrieved next time
            session.setAttribute("previousItemsTitles", previousItemsTitles);
            session.setAttribute("previousItemsCount", previousItemsCount);
        }
        
        String newCartItemId = request.getParameter("newCartItemId");
        String newCartItemTitle = request.getParameter("newCartItemTitle");
        
        //create variable to enable user to update cart quantity
        boolean itemExistsInCart = false;
        
        synchronized (previousItemsIds) {
            if (newCartItemId != null && newCartItemTitle != null) 
            {
            	
            	if(previousItemsIds.contains(newCartItemId))
            	{
            		int index = previousItemsIds.indexOf(newCartItemId);
           			int currentCount = previousItemsCount.get(index);
           			previousItemsCount.set(index, ++currentCount);
           			itemExistsInCart = true;
            	}
            }
            
            if (newCartItemId != null && newCartItemTitle != null && !itemExistsInCart)
            {
           		previousItemsIds.add(newCartItemId); // Add the new item to the previousItems ArrayList
           		previousItemsTitles.add(newCartItemTitle);
           		previousItemsCount.add(1);
            }
            
            //make function to allow user to update cart quantity
            if(request.getParameterMap().containsKey("updateQuantity") && request.getParameterMap().containsKey("movieIndex"))
            {
            	int updateQuantity = Integer.parseInt(request.getParameter("updateQuantity"));
            	int movieIndex = Integer.parseInt(request.getParameter("movieIndex"));
            	
            	if(updateQuantity <= 0)
            	{
            		previousItemsIds.remove(movieIndex);
            		previousItemsTitles.remove(movieIndex);
            		previousItemsCount.remove(movieIndex);
            	}
            	else
            		previousItemsCount.set(movieIndex, updateQuantity);
            }
            
            if(previousItemsIds.size() == 0)
            	out.println("<i>Shopping Cart is empty.</i>");
            else
            {
            	out.println("<div>");
            	out.println("<table id=\"cartTable\">");
            	out.println("<tr>\n"
            			+ "<th>Movie Id</th>\n"
            			+ "<th>Title</th>\n"
            			+ "<th>Quantity</th>\n"
            			+ "<th>Update Quantity</th>\n"
            			+ "<th>Remove Item</th>\n"
            			+ "</tr>");
            			
            	int cartSize = previousItemsIds.size();
            	
            	for(int i = 0; i < cartSize; i++)
            	{
            		String movieId = previousItemsIds.get(i);
            		String movieTitle = previousItemsTitles.get(i);
            		int movieCount = previousItemsCount.get(i);
            		out.println("<tr>");
            		out.println("<td>" + movieId + "</td>");
            		out.println("<td>"
        					+ "<form action=\"SingleMovie\" method = \"GET\">"
        					+       "<input type=\"hidden\" name=\"movieId\" value=\"" + movieId + "\">\n"
        					+		"<button class = \"movieLinkButton\" type=\"submit\">" + movieTitle + "</button>"
        					+	"</form>\n"
        					+ "</td>"
        					+ "<td>" + movieCount + "</td>"
        					+ "<td>"
        					+ "	<form action=\"Checkout\" method=\"GET\">"
        					+ "		Quantity:<input type=\"number\" name=\"updateQuantity\" >\n"
        					+"		<input type=\"hidden\" name=\"movieIndex\" value=\"" + i + "\">\n"	
        					+ "		<button type=\"submit\">Update Cart</button>\n"
        					+ "	</form>\n"
        					+"</td>\n"
        					+"<td>"
        					+ "	<form action=\"Checkout\" method=\"GET\">"
        					+ "		<input type=\"hidden\" name=\"updateQuantity\" value=\"0\">\n"
        					+"		<input type=\"hidden\" name=\"movieIndex\" value=\"" + i + "\">\n"	
        					+ "		<button type=\"submit\">Remove Item</button>\n"
        					+" </form>\n"
        					+ "</td>\n"
        					+ "</tr>");
            		
            	}
            	
            	out.println("</table>");
            	out.println("</div>");
            	out.println("<br>");
            	out.println("<div id=\"paymentInfoButton\">");
            	out.println("<form action=\"paymentInformation.html\" method=\"GET\">\n"
				+ "		<button class=\"paymentInfoButton\" type=\"submit\">Enter Card Payment Information</button>\n"
				+" </form>\n");
            	out.println("</div>");
            	out.println("</body>");
            	out.println("</html>");
            	out.close();
            }
            
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
