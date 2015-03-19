import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.abego.treelayout.TreeLayout;

public class WeaponBrowserMain {

	public static void main(String[] args) throws Exception{
		Connection c = null;
		try{
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:data\\database.sqlite");
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Opened connection successfully");
		WeaponManager manager = new WeaponManager(c);
		
		manager.loadAllWeapons();
        // create the layout
        TreeLayout<TextInBox> treeLayout = manager.getTree();

        // Create a panel that draws the nodes and edges and show the panel
        TextInBoxTreePane panel = new TextInBoxTreePane(treeLayout);
        
        JScrollPane scrollPanel = new JScrollPane(panel);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.init(scrollPanel);
        
        showInDialog(scrollPanel, panel, manager);


	}
	
	private static void showInDialog(JComponent panel, TextInBoxTreePane tree, WeaponManager manager) {
        JDialog dialog = new JDialog();
        Container contentPane = dialog.getContentPane();
        ((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(
                        10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout());
        
        //control panel above the scroll pane
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //back button
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tree.back();
			}
		});
        controlPanel.add(btnBack);
        controlPanel.add(new JTextField(100));
        
        JComboBox<Object> comboBox = new JComboBox<Object>(manager.getWeaponTypes().toArray());
        comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedItem = comboBox.getSelectedItem().toString();
				manager.setActiveTree(selectedItem);
				tree.setTree(manager.getTree());
			}
		});
        
        controlPanel.add(comboBox);
        
        contentPane.add(controlPanel, BorderLayout.NORTH);
        contentPane.add(tree, BorderLayout.CENTER);
        dialog.pack();
        dialog.setSize(1600, 900);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
}


}
