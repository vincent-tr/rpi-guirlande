package mylife.guirlande.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import mylife.guirlande.programs.Manager;
import mylife.guirlande.programs.Program;

public class PlayServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6249542340796890229L;

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String programId = request.getParameter("programId");
		if(!StringUtils.isEmpty(programId)) {
			int id = Integer.parseInt(programId);
			Program selected = null;
			for(Program prog : Manager.getInstance().getPrograms()) {
				if(prog.getId() == id) {
					selected = prog;
					break;
				}
			}
			if(selected != null)
				Manager.getInstance().setActive(selected);
		}
		
		String relative = request.getParameter("relative");
		if(relative != null) {
			if("previous".equals(relative))
				Manager.getInstance().movePrevious();
			else if("next".equals(relative))
				Manager.getInstance().moveNext();
		}
		
		String redirect = request.getContextPath();
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
