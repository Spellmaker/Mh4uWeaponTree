package de.spellmaker.mh4.treeview;

import java.awt.Color;

public class NodeDesign {
	public final Color BOX_COLOR;
	public final Color LINE_COLOR;
	public final int LINE_WIDTH;
	public final Color TEXT_COLOR;
	public final int ARC_SIZE;
	
	public NodeDesign(Color boxcolor, Color linecolor, int linewidth, Color textcolor, int arcsize){
		BOX_COLOR = boxcolor;
		LINE_COLOR = linecolor;
		LINE_WIDTH = linewidth;
		TEXT_COLOR = textcolor;
		ARC_SIZE = arcsize;
	}
}
