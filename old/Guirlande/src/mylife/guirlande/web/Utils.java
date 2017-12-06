package mylife.guirlande.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.wurfl.core.Device;
import net.sourceforge.wurfl.core.WURFLHolder;
import net.sourceforge.wurfl.core.WURFLManager;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class Utils {

	private static final Logger log = Logger.getLogger(Utils.class.getName());

	public static final String getDojoInclude(HttpServletRequest request) {
		return request.getContextPath() + "/dojo/dojo.js";
	}

	public static final String getDojoStyle(HttpServletRequest request) {
		return request.getContextPath() + "/dijit/themes/claro/claro.css";
	}

	public static final String getMetroStyle(HttpServletRequest request) {
		return getMetroBase(request) + "metro.css";
	}

	public static final String getMetroBase(HttpServletRequest request) {
		return request.getContextPath() + "/metro-ui/";
	}

	public static final String getResource(HttpServletRequest request,
			String name) {
		return request.getContextPath() + "/resources/" + name;
	}

	public static final String escape(String value) {
		return StringEscapeUtils.escapeHtml(value);
	}

	public static final String escapeUrl(String value)
			throws UnsupportedEncodingException {
		return URLEncoder.encode(value, "ISO-8859-1");
	}

	public static final String playServlet(HttpServletRequest request,
			String verb, String value) throws UnsupportedEncodingException {
		return request.getContextPath() + "/play?" + verb + "="
				+ Utils.escapeUrl(value);
	}

	public static final String parametersServlet(HttpServletRequest request,
			String action, String value) throws UnsupportedEncodingException {
		String url = request.getContextPath() + "/parameters";
		if (StringUtils.isEmpty(action))
			return url;
		url += "?action=" + Utils.escapeUrl(action);
		if (StringUtils.isEmpty(value))
			return url;
		url += "&value=" + Utils.escapeUrl(value);
		return url;
	}

	public static final String exportServlet(HttpServletRequest request) throws UnsupportedEncodingException {
		return request.getContextPath() + "/export";
	}

	public static final String importServlet(HttpServletRequest request) throws UnsupportedEncodingException {
		return request.getContextPath() + "/import";
	}

	public static final boolean isMobile(HttpServletRequest request) {
		WURFLHolder wurfl = (WURFLHolder) request.getServletContext().getAttribute(
				WURFLHolder.class.getName());

		WURFLManager manager = wurfl.getWURFLManager();
		
		Device device = manager.getDeviceForRequest(request);

		boolean isMobile = device.getCapabilityAsBool("is_wireless_device");

		log.info("Device: " + device.getId());
		log.info("isMobile: " + isMobile);
		
		return isMobile;
	}
	
	public static final String getRedirectBasePath(HttpServletRequest request) {
		boolean isMobile = Utils.isMobile(request);
		String redirect = request.getContextPath();
		if (isMobile)
			redirect += "/guirlande-mobile";
		else
			redirect += "/guirlande-base";
		return redirect;
	}
}
