package de.spellmaker.mh4.itemview;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import de.spellmaker.mh4.data.CraftData;
import de.spellmaker.mh4.data.Weapon;
import de.spellmaker.mh4.data.WeaponManager;
import de.spellmaker.mh4.treeview.Mh4NodeDesigner;

@SuppressWarnings("serial")
public class ItemView extends JPanel {
	private WeaponManager manager;
	private Weapon selected;
	
	private CraftPanel create;
	private CraftPanel upgrade;
	
	public ItemView(WeaponManager manager, Mh4NodeDesigner designer, JComponent parent){ 
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        this.manager = manager;
        this.selected = null;
        create = new CraftPanel("Create", manager, designer, parent);
        upgrade = new CraftPanel("Upgrade", manager, designer, parent);
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
	}
}
