package mylife.guirlande.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import mylife.guirlande.programs.Manager;

public class ImportServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 766623936848155981L;

	private static final Logger log = Logger.getLogger(ImportServlet.class
			.getName());

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		try {

			// execution de l'import
			String importType = request.getParameter("importType");
			Part importFile = request.getPart("importFile");
			InputStream content = importFile.getInputStream();

			try {
				if ("replace".equals(importType))
					Manager.getInstance().importAndReplace(content);
				else if ("add".equals(importType))
					Manager.getInstance().importAndMerge(content);
				else
					throw new UnsupportedOperationException(
							"Invalid import type : " + importType);
			} finally {
				content.close();
			}

			// redirection sur la page de paramètres
			String redirect = Utils.getRedirectBasePath(request);
			redirect += "/parameters.jsp";
			GetParametersCollection parameters = new GetParametersCollection();
			parameters.put("info", "Import terminé avec succès");
			response.sendRedirect(redirect + parameters.format());
		} catch (Exception e) {
			log.severe("Error importing : " + e.toString());
			throw new ServletException("Error importing", e);
		}
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
