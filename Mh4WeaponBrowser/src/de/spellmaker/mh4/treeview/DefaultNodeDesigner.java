package de.spellmaker.mh4.treeview;

import java.awt.Color;

public class DefaultNodeDesigner<N> implements NodeDesigner<N> {

	@Override
	public NodeDesign getNodeDesign(N node, boolean selected) {
		if(selected)
			return new NodeDesign(Color.red, Color.black, 1, Color.black, 10);
		else
			return new NodeDesign(Color.orange, Color.black, 1, Color.black, 10);
	}

	@Override
	public EdgeDesign getEdgeDesign(N node1, N node2) {
		return new EdgeDesign();
	}

}
