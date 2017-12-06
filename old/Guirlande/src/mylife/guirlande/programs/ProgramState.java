package mylife.guirlande.programs;

import java.io.Serializable;

/**
 * Etat d'un programme (un programme est une succession d'�tats)
 * @author pumbawoman
 *
 */
public class ProgramState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6642442436000175495L;

	/**
	 * Nombre d'item de l'�tat
	 */
	public final static int COUNT = 16; 
	
	/**
	 * Item de donn�e de l'�tat
	 */
	private int[] items;
	
	public ProgramState() {
		items = new int[COUNT];
	}
	
	/**
	 * D�finition d'un item
	 * @param index
	 * @param value
	 * @return
	 */
	public int setItem(int index, int value) {
		if(value < 0 || value > 100)
			throw new IllegalArgumentException("La valeur doit �tre sup�rieure ou �gale � 0 et inf�rieure ou �gale � 100");
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
