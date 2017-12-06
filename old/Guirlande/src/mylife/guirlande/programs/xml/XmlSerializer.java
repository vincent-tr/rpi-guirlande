package mylife.guirlande.programs.xml;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import mylife.guirlande.programs.Program;
import mylife.guirlande.programs.ProgramGroup;
import mylife.guirlande.programs.ProgramState;

public class XmlSerializer {

	private final JAXBContext context;

	public XmlSerializer() throws Exception {
		context = JAXBContext.newInstance(XmlPrograms.class);
	}

	public String getEncoding() throws Exception {
		Marshaller marshaller = context.createMarshaller();
		return (String) marshaller.getProperty(Marshaller.JAXB_ENCODING);
	}

	private XmlProgram convert(Program input) {
		XmlProgram output = new XmlProgram();

		output.name = input.getName();
		output.description = input.getDescription();

		List<ProgramState> list = input.getStates();
		output.states = new XmlProgramState[list.size()];
		for (int i = 0; i < output.states.length; i++) {
			output.states[i] = convert(list.get(i));
		}

		return output;
	}

	private Program convert(XmlProgram input, int programId) {
		Program output = new Program(programId);

		output.setName(input.name);
		output.setName(input.name);
		output.setDescription(input.description);

		for (XmlProgramState xmlstate : input.states) {
			output.getStates().add(convert(xmlstate));
		}

		return output;
	}

	private XmlProgramState convert(ProgramState input) {
		XmlProgramState output = new XmlProgramState();

		output.values = new int[ProgramState.COUNT];
		for (int i = 0; i < ProgramState.COUNT; i++) {
			output.values[i] = input.getItem(i);
		}

		return output;
	}

	private ProgramState convert(XmlProgramState input) {
		ProgramState output = new ProgramState();

		for (int i = 0; i < ProgramState.COUNT; i++) {
			output.setItem(i, input.values[i]);
		}

		return output;
	}

	public String xmlExport(ProgramGroup programs) throws Exception {

		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter writer = new StringWriter();

		try {
			XmlPrograms xml = new XmlPrograms();
			List<Program> list = programs.getPrograms();
			xml.programs = new XmlProgram[list.size()];
			for (int i = 0; i < xml.programs.length; i++) {
				xml.programs[i] = convert(list.get(i));
			}

			marshaller.marshal(xml, writer);
			return writer.toString();
		} finally {
			writer.close();
		}
	}

	public List<Program> xmlImport(InputStream inputStream,
			ProgramGroup programs) throws Exception {
		
		Unmarshaller unmarshaller = context.createUnmarshaller();
		XmlPrograms xml = (XmlPrograms) unmarshaller.unmarshal(inputStream);

		List<Program> list = new ArrayList<Program>();
		for (XmlProgram xmlProgram : xml.programs) {
			Program program = convert(xmlProgram, programs.getNextId());
			list.add(program);
		}
		return list;
	}
}
