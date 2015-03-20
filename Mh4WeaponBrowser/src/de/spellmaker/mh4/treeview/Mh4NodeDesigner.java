package de.spellmaker.mh4.treeview;

import java.awt.Color;

import de.spellmaker.mh4.tree.TextInBox;

public class Mh4NodeDesigner implements NodeDesigner<TextInBox> {

	@Override
	public NodeDesign getNodeDesign(TextInBox node, boolean selected) {
		Color boxcolor = Color.white;
		Color linecolor = Color.black;
		int linewidth = 1;
		Color textcolor = Color.black;
		int arcsize = 10;
		
		if(selected) boxcolor = Color.gray;
		if(node.source.isWeapon_final()){
			linecolor = Color.blue;
			linewidth = 2;
		}
		if(node.source.getPrice_create() > 0){
			linewidth = 2;
			textcolor = Color.red;
		}
		
		return new NodeDesign(boxcolor, linecolor, linewidth, textcolor, arcsize);
	}

	@Override
	public EdgeDesign getEdgeDesign(TextInBox node1, TextInBox node2) {
		return new EdgeDesign();
	}

}
