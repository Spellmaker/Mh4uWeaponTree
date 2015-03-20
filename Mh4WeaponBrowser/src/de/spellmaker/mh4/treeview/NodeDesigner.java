package de.spellmaker.mh4.treeview;

public interface NodeDesigner<N> {
	public NodeDesign getNodeDesign(N node, boolean selected);
	public EdgeDesign getEdgeDesign(N node1, N node2);
}
