package mylife.guirlande.programs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProgramGroup implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2775361872000642847L;
	
	private List<Program> programs;
	private int activeIndex;
	private int idCounter;

	public ProgramGroup() {
		programs = new ArrayList<Program>();
	}
	
	public List<Program> getPrograms() {
		return programs;
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}
	
	public int getNextId() {
		return ++idCounter;
	}
}
