package mylife.guirlande.programs.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

class XmlProgram {

	@XmlAttribute
	public String name;

	@XmlAttribute
	public String description;

	@XmlElement(name = "state")
	public XmlProgramState[] states;
}
