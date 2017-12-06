package mylife.guirlande.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import mylife.guirlande.programs.Manager;
import mylife.guirlande.programs.net.NetServer;

public class LoaderServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3048747336425442414L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		try {
		
		NetServer.getInstance().start();
		Manager.getInstance().init();
		}
		catch(Exception e) {
			throw new ServletException("Error in LoaderServlet", e);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		
		NetServer.getInstance().stop();
	}

}
