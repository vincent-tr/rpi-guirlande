package mylife.guirlande.programs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import mylife.guirlande.programs.net.NetServer;
import mylife.guirlande.programs.xml.XmlSerializer;

public class Manager {

	private static final Logger log = Logger.getLogger(Manager.class.getName());

	public static final String DB_FILENAME = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "guirlande.db";

	private final static Manager instance = new Manager();

	public static Manager getInstance() {
		return instance;
	}

	/**
	 * Liste des programmes gérés
	 */
	private ProgramGroup programs;

	private Manager() {
		load();
	}

	// http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serial-arch.html#4176

	private ProgramGroup deserialize() throws Exception {
		File file = new File(DB_FILENAME);
		if (!file.exists())
			return null;
		FileInputStream in = null;
		ObjectInputStream s = null;
		try {
			in = new FileInputStream(DB_FILENAME);
			s = new ObjectInputStream(in);
			return (ProgramGroup) s.readObject();
		} finally {
			if (s != null)
				s.close();
			if (in != null)
				in.close();
		}
	}

	private void serialize(ProgramGroup obj) throws Exception {
		FileOutputStream out = null;
		ObjectOutputStream s = null;
		try {
			out = new FileOutputStream(DB_FILENAME);
			s = new ObjectOutputStream(out);
			s.writeObject(obj);

		} finally {
			if (s != null)
				s.close();
			if (out != null)
				out.close();
		}
	}

	private void load() {
		log.info("Loading database from file " + DB_FILENAME);

		try {
			programs = deserialize();
			log.info("Data loaded");
		} catch (Exception e) {
			log.severe("Error loading data !" + e.toString());
		}
		if (programs == null) {
			programs = new ProgramGroup();
			programs.getPrograms().add(createDefaultProgram());
			programs.setActiveIndex(0);
			try {
				serialize(programs);
			} catch (Exception e) {
				log.severe("Error saving data !" + e.toString());
			}
			log.info("Data not loaded, default created");
		}
	}

	private Program createDefaultProgram() {
		return Program.createDefault(programs.getNextId());
	}

	private void save() {
		log.info("Saving db4o database to file " + DB_FILENAME);

		try {
			serialize(programs);
			log.info("Data saved");
		} catch (Exception e) {
			log.severe("Error saving data !" + e.toString());
		}
	}

	public synchronized List<Program> getPrograms() {
		return Collections.unmodifiableList(programs.getPrograms());
	}

	/**
	 * Obtention d'un programme par son id
	 * 
	 * @param id
	 * @return
	 */
	public synchronized Program getById(int id) {
		for (Program program : programs.getPrograms()) {
			if (program.getId() == id)
				return program;
		}
		return null;
	}

	/**
	 * Création d'un nouveau programme
	 * 
	 * @return
	 */
	public synchronized Program newProgram() {
		int id = programs.getNextId();
		Program program = new Program(id);
		program.setName("Programme " + id);
		program.setDescription("Description du programme " + id);
		program.getStates().add(new ProgramState());
		programs.getPrograms().add(program);
		save();
		return program;
	}

	/**
	 * Suppression d'un programme
	 * 
	 * @param program
	 * @return
	 */
	public synchronized boolean deleteProgram(Program program) {

		boolean isActive = getActive() == program;

		// suppression du programme
		if (!programs.getPrograms().remove(program))
			return false;

		// si c'est le dernier on recrée un programme par défaut
		if (programs.getPrograms().size() == 0)
			programs.getPrograms().add(createDefaultProgram());

		// si c'était l'actif on revient au début. Si c'était le dernier
		// programme c'était forcément l'actif
		if (isActive)
			programs.setActiveIndex(0);

		// enregistrement
		save();

		return true;
	}

	/**
	 * Indique qu'un programme a été mis à jour
	 * 
	 * @param program
	 */
	public synchronized void updateProgram(Program program) {
		save();
	}

	public synchronized Program getActive() {
		return programs.getPrograms().get(programs.getActiveIndex());
	}

	public synchronized void setActive(Program program) {
		for (int i = 0; i < programs.getPrograms().size(); i++) {
			if (programs.getPrograms().get(i) == program) {
				setActiveIndex(i);
				return;
			}
		}
		throw new IllegalArgumentException("Program not found");
	}

	public synchronized void setActiveIndex(int index) {
		if (index < 0 || index >= programs.getPrograms().size())
			throw new IndexOutOfBoundsException();
		programs.setActiveIndex(index);

		executeActive();
		save();
	}

	public synchronized int getActiveIndex() {
		return programs.getActiveIndex();
	}

	public synchronized void moveNext() {
		setActiveIndex((getActiveIndex() + 1) % programs.getPrograms().size());
	}

	public synchronized void movePrevious() {
		setActiveIndex((getActiveIndex() - 1 + programs.getPrograms().size())
				% programs.getPrograms().size());
	}

	public String exportEncoding() throws Exception {
		XmlSerializer serializer = new XmlSerializer();
		return serializer.getEncoding();
	}

	public synchronized String export() throws Exception {
		XmlSerializer serializer = new XmlSerializer();
		return serializer.xmlExport(programs);
	}

	private List<Program> readImport(InputStream content) throws Exception {
		XmlSerializer serializer = new XmlSerializer();
		return serializer.xmlImport(content, programs);
	}

	public synchronized void importAndReplace(InputStream content)
			throws Exception {
		List<Program> progs = readImport(content);
		programs.getPrograms().clear();
		programs.getPrograms().addAll(progs);
		setActiveIndex(0);
	}

	public synchronized void importAndMerge(InputStream content)
			throws Exception {
		List<Program> progs = readImport(content);
		programs.getPrograms().addAll(progs);
	}

	public synchronized void init() {
		executeActive();
	}

	private void executeActive() {
		Program program = getActive();
		NetServer.getInstance().sendExecute(program);
	}
}
