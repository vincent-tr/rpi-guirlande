package mylife.guirlande.programs.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "programs")
class XmlPrograms {

	@XmlElement(name = "program")
	public XmlProgram[] programs;
}
