package de.spellmaker.mh4.itemview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.spellmaker.mh4.data.CraftData;
import de.spellmaker.mh4.data.Item;

@SuppressWarnings("serial")
public class CraftPanel extends JPanel {
	private JLabel title;
	private JPanel body;
	private JLabel[] itemNames;
	private JButton[] left;
	private JLabel[] itemQuantity;
	private JButton[] right;
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(getWidth(), 100);
	}
	
	public CraftPanel(String title){
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(new BorderLayout());
		
		this.title = new JLabel(title);
		this.add(this.title, BorderLayout.NORTH);
		
		this.body = new JPanel(new GridBagLayout());
		
		this.add(this.body, BorderLayout.CENTER);
		
		this.itemNames = new JLabel[4];
		this.left = new JButton[4];
		this.itemQuantity = new JLabel[4];
		this.right = new JButton[4];
		
		for(int i = 0; i < 4; i++){
			this.itemNames[i] = new JLabel();
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
			this.body.add(left[i], constr);
			
			constr = new GridBagConstraints();
			constr.gridx = 5; constr.gridy = i;
			constr.gridwidth = 1; constr.gridheight = 1;
			constr.insets = new Insets(5, 5, 5, 5);
			this.itemQuantity[i] = new JLabel();
			this.body.add(itemQuantity[i], constr);
			
			constr = new GridBagConstraints();
			constr.gridx = 6; constr.gridy = i;
			constr.gridwidth = 1; constr.gridheight = 1;
			constr.insets = new Insets(5, 5, 5, 5);
			this.right[i] = new JButton(">");
			this.body.add(this.right[i], constr);
		}
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
					itemQuantity[i].setText("" + c.quantity);
					right[i].setVisible(true);
				}
			}
		}
	}
	
	private void wipe(){
		for(int i = 0; i < 4; i++){
			itemNames[i].setText("");
			left[i].setVisible(false);
			itemQuantity[i].setText("");
			right[i].setVisible(false);
		}
	}
}
