package mylife.guirlande.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mylife.guirlande.programs.Manager;

public class ExportServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 409162933488594678L;

	private static final Logger log = Logger.getLogger(ExportServlet.class
			.getName());

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			String content;
			content = Manager.getInstance().export();
			String encoding = Manager.getInstance().exportEncoding();
			byte[] data = content.getBytes(encoding);

			response.setContentType("application/octet-stream");
			response.setContentLength(data.length);
			response.setHeader("Content-Disposition",
					"attachment; filename=\"guirlande-export.xml\"");

			ServletOutputStream outStream = response.getOutputStream();
			outStream.write(data);
			outStream.close();

			log.info("Data exported");
		} catch (Exception e) {
			throw new ServletException("Error exporting", e);
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
