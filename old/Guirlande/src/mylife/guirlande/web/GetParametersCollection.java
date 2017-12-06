package mylife.guirlande.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestion de la collection de paramètres
 * @author pumbawoman
 *
 */
public class GetParametersCollection extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4732024423587079257L;

	/**
	 * Formattage des paramètres
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String format() throws UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for(Map.Entry<String, String> item : this.entrySet()) {
			if(first) {
				buffer.append('?');
				first = false;
			}else {
				buffer.append('&');
			}
			buffer.append(item.getKey());
			buffer.append('=');
			buffer.append(URLEncoder.encode(item.getValue(), "ISO-8859-1"));
		}
		return buffer.toString();
	}
}
