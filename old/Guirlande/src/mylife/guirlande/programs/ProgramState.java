package mylife.guirlande.programs;

import java.io.Serializable;

/**
 * Etat d'un programme (un programme est une succession d'états)
 * @author pumbawoman
 *
 */
public class ProgramState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6642442436000175495L;

	/**
	 * Nombre d'item de l'état
	 */
	public final static int COUNT = 16; 
	
	/**
	 * Item de donnée de l'état
	 */
	private int[] items;
	
	public ProgramState() {
		items = new int[COUNT];
	}
	
	/**
	 * Définition d'un item
	 * @param index
	 * @param value
	 * @return
	 */
	public int setItem(int index, int value) {
		if(value < 0 || value > 100)
			throw new IllegalArgumentException("La valeur doit être supérieure ou égale à 0 et inférieure ou égale à 100");
		items[index] = value;
		return value;
	}
	
	/**
	 * Obtention d'un item
	 * @param index
	 * @return
	 */
	public int getItem(int index) {
		return items[index];
	}

	/**
	 * Clone de l'objet
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		ProgramState clone = new ProgramState();
		for(int i=0; i<COUNT; i++) {
			clone.items[i] = this.items[i];
		}
		return clone;
	}
}
