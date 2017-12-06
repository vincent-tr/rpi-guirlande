package mylife.guirlande.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// http://wiki.apache.org/tomcat/HowTo#How_do_I_override_the_default_home_page_loaded_by_Tomcat.3F

public class DispatcherServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2733167566419311995L;

	private static final Logger log = Logger.getLogger(DispatcherServlet.class
			.getName());

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String redirect = Utils.getRedirectBasePath(request);
		redirect += "/index.jsp";
		
		log.info("redirect: " + redirect);

		response.sendRedirect(redirect);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
}
