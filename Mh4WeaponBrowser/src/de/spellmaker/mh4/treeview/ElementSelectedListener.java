package de.spellmaker.mh4.treeview;

public interface ElementSelectedListener<N> {
	public void elementSelected(ElementSelectedEvent<N> e);
	public void elementDoubleClicked(ElementSelectedEvent<N> e);
}
