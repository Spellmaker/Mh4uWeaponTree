package de.spellmaker.mh4.treeview;

public class ElementSelectedEvent<N> {
	public final int mouseButton;
	public final N element;
	
	public ElementSelectedEvent(int mouseButton, N element){
		this.mouseButton = mouseButton;
		this.element = element;
	}
}
