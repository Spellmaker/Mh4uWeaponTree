package de.spellmaker.mh4.itemview;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.spellmaker.mh4.data.CraftData;
import de.spellmaker.mh4.data.Item;
import de.spellmaker.mh4.data.WeaponManager;

@SuppressWarnings("serial")
public class CraftPanel extends JPanel implements ActionListener, MouseListener {
	private JLabel title;
	private JPanel body;
	private JLabel[] itemNames;
	private JButton[] left;
	private JLabel[] myItems;
	private JButton[] right;
	private JLabel[] neededItems;
	private WeaponManager manager;
	private JButton build;
	
	private Item[] items;
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(getWidth(), 100);
	}
	
	public CraftPanel(String title, WeaponManager manager){
		this.manager = manager;
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(new BorderLayout());
		
		this.title = new JLabel(title);
		this.add(this.title, BorderLayout.NORTH);
		
		this.body = new JPanel(new GridBagLayout());
		
		this.add(this.body, BorderLayout.CENTER);
		
		this.itemNames = new JLabel[4];
		this.left = new JButton[4];
		this.myItems = new JLabel[4];
		this.right = new JButton[4];
		this.neededItems = new JLabel[4];
		
		this.items = new Item[4];
		
		for(int i = 0; i < 4; i++){
			this.itemNames[i] = new JLabel();
			this.itemNames[i].addMouseListener(this);
			GridBagConstraints constr = new GridBagConstraints();
			constr.gridx = 0; constr.gridy = i;
			constr.gridwidth = 3; constr.gridheight = 1;
			constr.insets = new Insets(5, 5, 5, 5);
			this.body.add(itemNames[i], constr);
			
			constr = new GridBagConstraints();
			constr.gridx = 4; constr.gridy = i;
			constr.gridwidth = 1; constr.gridheight = 1;
			constr.insets = new Insets(5, 5, 5, 5);
			this.left[i] = new JButton("<");
			this.left[i].addActionListener(this);
			this.body.add(left[i], constr);
			
			constr = new GridBagConstraints();
			constr.gridx = 5; constr.gridy = i;
			constr.gridwidth = 1; constr.gridheight = 1;
			constr.insets = new Insets(5, 5, 5, 5);
			this.myItems[i] = new JLabel();
			this.body.add(myItems[i], constr);
			
			constr = new GridBagConstraints();
			constr.gridx = 6; constr.gridy = i;
			constr.gridwidth = 1; constr.gridheight = 1;
			constr.insets = new Insets(5, 5, 5, 5);
			this.right[i] = new JButton(">");
			this.right[i].addActionListener(this);
			this.body.add(this.right[i], constr);
			
			constr = new GridBagConstraints();
			constr.gridx = 7; constr.gridy = i;
			constr.gridwidth = 1; constr.gridheight = 1;
			constr.insets = new Insets(5, 5, 5, 5);
			this.neededItems[i] = new JLabel();
			this.body.add(neededItems[i], constr);
		}
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = 3;
		constr.gridy = 5;
		constr.gridwidth = 2; constr.gridheight = 1;
		this.build = new JButton("Build");
		this.build.addActionListener(this);
		this.body.add(build, constr);
		
		
		
		wipe();
	}
	
	public void setSource(CraftData cd){
		wipe();
		if(cd != null){
			for(int i = 0; i < 4; i++){
				Item c = cd.items[i];
				if(c == null){
					itemNames[i].setText("-");
				}
				else{
					itemNames[i].setText(c.name);
					left[i].setVisible(true);
					myItems[i].setText("" + manager.getItemAmount(c.id));
					right[i].setVisible(true);
					neededItems[i].setText("" + c.quantity);
					items[i] = c;
					build.setVisible(true);
				}
			}
		}
	}
	
	private void wipe(){
		for(int i = 0; i < 4; i++){
			itemNames[i].setText("");
			left[i].setVisible(false);
			myItems[i].setText("");
			right[i].setVisible(false);
			neededItems[i].setText("");
			items[i] = null;
		}
		build.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(build)){
			for(int i = 0; i < 4; i++){
				if(items[i] != null){
					int amt = manager.getItemAmount(items[i].id);
					amt = (int) Math.max(0, amt - items[i].quantity);
					manager.setItemAmount(items[i].id, amt);
					myItems[i].setText("" + amt);
				}
			}
		}
		else
		for(int i = 0; i < 4; i++){
			if(e.getSource().equals(left[i])){
				if(items[i] != null){
					int amt = manager.getItemAmount(items[i].id);
					amt = (int) Math.max(0, amt - 1);
					manager.setItemAmount(items[i].id, amt);
					myItems[i].setText("" + amt);
					System.out.println("new amount is: " + amt);
				}
				break;
			}
			else if(e.getSource().equals(right[i])){
				if(items[i] != null){
					int amt = manager.getItemAmount(items[i].id);
					amt += 1;
					manager.setItemAmount(items[i].id, amt);
					myItems[i].setText("" + amt);
					System.out.println("new amount is: " + amt);
				}
				break;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for(int i = 0; i < 4; i++){
			if(e.getSource().equals(itemNames[i])){
				if(items[i] != null){
					try{
						Desktop desktop = Desktop.getDesktop();
						desktop.browse(new URI(items[i].link));
					}
					catch(Exception exc){
						System.out.println("could not open browser");
					}
				}
				break;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
