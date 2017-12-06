package mylife.guirlande.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mylife.guirlande.programs.Manager;
import mylife.guirlande.programs.Program;

import org.apache.commons.lang.StringUtils;

public class ParametersServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6231282256766574806L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		int selectedId = -1;
		
		String action = request.getParameter("action");
		if (!StringUtils.isEmpty(action)) {
			if ("newProgram".equals(action)) {
				selectedId = newProgram();
			} else if ("deleteProgram".equals(action)) {
				int programId = Integer.parseInt(request.getParameter("value"));
				deleteProgram(programId);
			} else if ("reloadProgram".equals(action)) {
				int programId = Integer.parseInt(request.getParameter("value"));
				selectedId = programId;
			} else if ("saveProgram".equals(action)) {
				int programId = Integer.parseInt(request.getParameter("value"));
				saveProgram(programId, request);
				selectedId = programId;
			}
		}

		String redirect = Utils.getRedirectBasePath(request);
		redirect += "/parameters.jsp";
		GetParametersCollection parameters = new GetParametersCollection();
		if (selectedId > -1)
			parameters.put("selectedId", "" + selectedId);
		String alert = (String)request.getAttribute("alert");
		if(!StringUtils.isEmpty(alert))
			parameters.put("alert", alert);
		response.sendRedirect(redirect + parameters.format());
	}

	private int newProgram() {
		Program program = Manager.getInstance().newProgram();
		return program.getId();
	}

	private boolean deleteProgram(int programId) {
		Program program = Manager.getInstance().getById(programId);
		if (program == null)
			return false;
		return Manager.getInstance().deleteProgram(program);
	}
	
	private void saveProgram(int programId, HttpServletRequest request) {
		Program program = Manager.getInstance().getById(programId);
		if (program == null)
			return;
		try {
			program.setName(request.getParameter("txtProgramName"));
			program.setDescription(request.getParameter("txtProgramDescription"));
			program.setContent(request.getParameter("txtProgramContents"));
		}
		catch(Exception ex) {
			request.setAttribute("alert", ex.getMessage());
		}
		Manager.getInstance().updateProgram(program);
	}
}
