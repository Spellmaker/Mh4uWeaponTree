package de.spellmaker.mh4.treeview;

import java.awt.Color;
import java.io.IOException;

import de.spellmaker.mh4.data.CraftData;
import de.spellmaker.mh4.data.Weapon;
import de.spellmaker.mh4.data.WeaponManager;
import de.spellmaker.mh4.tree.TextInBox;

public class Mh4NodeDesigner implements NodeDesigner<TextInBox> {
	private WeaponManager manager;
	
	public Mh4NodeDesigner(WeaponManager manager) throws IOException{
		this.manager = manager;
	}
	
	public void toggleEntry(Weapon w) throws IOException{
		boolean state = manager.isBuilt(w);
		manager.setBuilt(w, !state);
	}
	
	public void addEntry(Weapon w) throws IOException{
		manager.setBuilt(w, true);
	}
	
	public void removeEntry(Weapon w) throws IOException{
		manager.setBuilt(w, false);
	}
	
	@Override
	public NodeDesign getNodeDesign(TextInBox node, boolean selected) {
		Color boxcolor = Color.white;
		Color linecolor = Color.black;
		int linewidth = 1;
		Color textcolor = Color.black;
		int arcsize = 10;
		
		if(selected) boxcolor = Color.gray;
		else if(manager.isBuilt(node.source)){
			boxcolor = Color.yellow;
		}
		else{
			boolean colorize = false;
			if(node.source.getPrice_create() > 0){
				CraftData create = manager.getCreateCraftData(node.source.getId());
				if(create != null) colorize = create.enoughItems(manager);
			}
			if(!colorize){
				Weapon parent = manager.getWeapon(node.source.getWeapon_parent_id());
				if(parent != null && manager.isBuilt(parent)){
					CraftData build = manager.getUpgradeCraftData(node.source.getId());
					if(build != null) colorize = build.enoughItems(manager);
				}
			}
			if(colorize){
				boxcolor = Color.lightGray;
			}
		}
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
