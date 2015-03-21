package de.spellmaker.mh4.itemview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import de.spellmaker.mh4.data.CraftData;
import de.spellmaker.mh4.data.Item;
import de.spellmaker.mh4.data.Weapon;
import de.spellmaker.mh4.data.WeaponManager;

@SuppressWarnings("serial")
public class ItemView extends JPanel {
	private WeaponManager manager;
	private Weapon selected;
	
	private CraftPanel create;
	private CraftPanel upgrade;
	
	public ItemView(WeaponManager manager){ 
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        this.manager = manager;
        this.selected = null;
        create = new CraftPanel("Create");
        upgrade = new CraftPanel("Upgrade");
        this.add(create);
        this.add(upgrade);
        this.add(new JPanel());
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(400, this.getHeight());
	}
	
	public void setSelected(Weapon w){
		this.selected = w;
		//this.removeAll();
		if(w == null){
			create.setSource(null);
			upgrade.setSource(null);
		}
		else{
			CraftData create = manager.getCreateCraftData(selected.getId());
			CraftData upgrade = manager.getUpgradeCraftData(selected.getId());
			this.create.setSource(create);
			this.upgrade.setSource(upgrade);
		}
		repaint();
		
		
		/*if(w == null){
			txtText.setText("");
		}
		else{
			CraftData create = manager.getCreateCraftData(selected.getId());
			CraftData upgrade = manager.getUpgradeCraftData(selected.getId());
			String txt = "Create:\n";
			txt += build(create);
			txt += "\n";
			txt += "Upgrade:\n";
			txt += build(upgrade);
			txtText.setText(txt);
			repaint();
		}*/
	}
	
	private String build(CraftData craft){
		String txt = "";
		if(craft == null) txt += "-\n";
		else{
			for(Item i : craft.items){
				if(i == null) txt += "-\n";
				else{
					txt += i.name + " x " + i.quantity + "\n";
				}
			}
		}
		return txt;
	}
}
