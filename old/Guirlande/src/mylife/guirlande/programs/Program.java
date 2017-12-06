package mylife.guirlande.programs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

public class Program implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -619530802327937087L;

	private String name;
	private String description;
	private List<ProgramState> states;
	private int id;

	public Program(int id) {
		states = new ArrayList<ProgramState>();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ProgramState> getStates() {
		return this.states;
	}

	public String getContent() {
		StringBuffer buffer = new StringBuffer();
		for (ProgramState state : states) {
			for (int i = 0; i < ProgramState.COUNT; i++) {
				if (i > 0)
					buffer.append(';');
				buffer.append(state.getItem(i));
			}
			buffer.append('\n');
		}
		return buffer.toString();
	}

	public void setContent(String value) {
		List<ProgramState> localStates = new ArrayList<ProgramState>();
		Scanner reader = new Scanner(value);
		try {
			int lineIndex = 0;
			while (reader.hasNextLine()) {

				// gestion de la ligne
				++lineIndex;
				String line = reader.nextLine();
				if (StringUtils.isEmpty(line))
					continue;

				// split de la ligne en valeurs
				String[] split = line.split(";");
				List<String> parts = new ArrayList<String>();
				for (String item : split) {

					// on enleve les parties vides
					if (StringUtils.isEmpty(item))
						continue;
					parts.add(item);
				}

				if (parts.size() != ProgramState.COUNT)
					throw new IllegalArgumentException("Ligne " + lineIndex
							+ " : Nombre de parties invalides : " + parts.size());

				// définition de l'état de programme
				ProgramState state = new ProgramState();
				for (int i = 0; i < ProgramState.COUNT; i++) {
					try {
						state.setItem(i, Integer.parseInt(parts.get(i)));
					} catch (NumberFormatException ex) {
						throw new IllegalArgumentException("Ligne " + lineIndex
								+ ", Partie " + (i + 1) + " : Valeur invalide : "
								+ ex.getMessage());
					}
				}
				localStates.add(state);
			}

			// la lecture est terminée avec succès, on peut définir le contenu
			states.clear();
			for (ProgramState state : localStates) {
				states.add(state);
			}

		} finally {
			reader.close();
		}
	}
	
	/**
	 * Création du programme par défaut
	 * @param id
	 * @return
	 */
	public static Program createDefault(int id) {
		Program program = new Program(id);
		program.setName("Off");
		program.setDescription("Toutes les lumières sont éteintes");
		program.getStates().add(new ProgramState());
		return program;
	}
}
