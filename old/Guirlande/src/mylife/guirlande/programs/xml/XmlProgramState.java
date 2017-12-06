package mylife.guirlande.programs.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlList;

class XmlProgramState {

	@XmlList
	@XmlAttribute(name = "values")
	public int[] values;
}
