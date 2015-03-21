package de.spellmaker.mh4.treeview;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.spellmaker.mh4.tree.TextInBox;

public class Mh4NodeDesigner implements NodeDesigner<TextInBox> {
	private List<String> selectedLines;
	private File saveFile;
	
	public Mh4NodeDesigner(String file) throws IOException{
		saveFile = new File(file);
		selectedLines = new ArrayList<>();
		if(!saveFile.exists()){
			if(saveFile.getParentFile() != null)
				saveFile.getParentFile().mkdirs();
		}
		else{
			BufferedReader br = new BufferedReader(new FileReader(saveFile));
			while(br.ready()){
				selectedLines.add(br.readLine());
			}
			br.close();
		}
	}
	
	private void save() throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));
		for(String l : selectedLines){
			bw.write(l + System.getProperty("line.separator"));
		}
		bw.close();
	}
	
	public void toggleEntry(String s) throws IOException{
		if(selectedLines.contains(s)){
			selectedLines.remove(s);
		}
		else{
			selectedLines.add(s);
		}
		save();
	}
	
	public void addEntry(String s) throws IOException{
		selectedLines.add(s);
		save();
	}
	
	public void removeEntry(String s) throws IOException{
		selectedLines.remove(s);
		save();
	}
	
	@Override
	public NodeDesign getNodeDesign(TextInBox node, boolean selected) {
		Color boxcolor = Color.white;
		Color linecolor = Color.black;
		int linewidth = 1;
		Color textcolor = Color.black;
		int arcsize = 10;
		
		if(selected) boxcolor = Color.gray;
		if(selectedLines.contains(node.source.getLocal_name())) boxcolor = Color.yellow;
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
