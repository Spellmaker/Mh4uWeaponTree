package de.spellmaker.mh4.tree;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.Stack;

import org.abego.treelayout.TreeLayout;

import de.spellmaker.mh4.data.WeaponManager;
import de.spellmaker.mh4.itemview.ItemView;
import de.spellmaker.mh4.treeview.ElementSelectedEvent;
import de.spellmaker.mh4.treeview.ElementSelectedListener;
import de.spellmaker.mh4.treeview.Mh4NodeDesigner;
import de.spellmaker.mh4.treeview.TreeViewPane;


public class TreeDataModel implements ElementSelectedListener<TextInBox> {
	private WeaponManager manager;
	private TreeViewPane<TextInBox> treeView;
	private Stack<TreeLayout<TextInBox>> stack;
	private Mh4NodeDesigner designer;
	private ItemView infoPanel;
	
	public TreeDataModel(WeaponManager weapon, TreeViewPane<TextInBox> treeView, Mh4NodeDesigner designer, ItemView infoPanel){
		this.manager = weapon;
		this.treeView = treeView;
		this.designer = designer;
		this.infoPanel = infoPanel;
		stack = new Stack<>();
		stack.push(manager.getTree());
		
		this.treeView.addElementSelectedListener(this);
		this.treeView.setTree(stack.peek());
	}

	@Override
	public void elementSelected(ElementSelectedEvent<TextInBox> e) {
		// do nothing
		if(e.element == null) infoPanel.setSelected(null);
		else infoPanel.setSelected(e.element.source);
	}

	@Override
	public void elementDoubleClicked(ElementSelectedEvent<TextInBox> e) {
		if(e.mouseButton == MouseEvent.BUTTON1){
			try {
				designer.toggleEntry(e.element.source.getLocal_name());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			treeView.repaint();
			//stack.push(manager.getTree(e.element));
			//treeView.setTree(stack.peek());
		}
		else if(e.mouseButton == MouseEvent.BUTTON2){
			try{
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI(e.element.source.getLink()));
			}
			catch(Exception exc){
				System.out.println("uri opening not supported");
			}
		}
		else if(e.mouseButton == MouseEvent.BUTTON3){
			TreeLayout<TextInBox> current = stack.peek();
			stack.push(manager.getReverseTree(current.getTree().getRoot(), e.element));
			treeView.setTree(stack.peek());
		}
	}

	public void back(){
		TreeLayout<TextInBox> topMost = stack.pop();
		if(stack.isEmpty()){
			stack.push(topMost);
		}
		else{
			treeView.setTree(stack.peek());
		}
	}
	
	public void changeWeaponTree(String s){
		stack.clear();
		manager.setActiveTree(s);
		stack.push(manager.getTree());
		treeView.setTree(stack.peek());
	}
}
