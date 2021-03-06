import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import de.spellmaker.mh4.data.WeaponManager;
import de.spellmaker.mh4.itemview.ItemView;
import de.spellmaker.mh4.tree.TextInBox;
import de.spellmaker.mh4.tree.TreeDataModel;
import de.spellmaker.mh4.treeview.Mh4NodeDesigner;
import de.spellmaker.mh4.treeview.TreeViewPane;


@SuppressWarnings("serial")
public class GUI extends JFrame {
	private Connection sqlConn;
	private Connection sqlSave;
	private WeaponManager manager;
	private TreeViewPane<TextInBox> treePane;
	private TreeDataModel model;
	
	public GUI() throws Exception{
		File dbFile = new File("data\\database.sqlite");
		if(!dbFile.exists()){
			System.out.println("ERROR: Missing Mh4 Database file. Exiting...");
			System.exit(0);
		}
		
		sqlConn = null;
		try{
			Class.forName("org.sqlite.JDBC");
			sqlConn = DriverManager.getConnection("jdbc:sqlite:data\\database.sqlite");
			sqlSave = DriverManager.getConnection("jdbc:sqlite:data\\myitems.sqlite");
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		
		//test if the save file tables exists
		Statement testTable = sqlSave.createStatement();
		ResultSet rs = testTable.executeQuery("SELECT name From sqlite_master WHERE type='table' AND name = 'Items'");
		if(!rs.next()){
			Statement createTable = sqlSave.createStatement();
			createTable.execute("CREATE TABLE 'Items' ('id' INTEGER, 'amount' INTEGER, PRIMARY KEY(id))");
			createTable.close();
		}
		testTable.close();
		
		System.out.println("Opened connection successfully");
		manager = new WeaponManager(sqlConn, sqlSave);
		
		manager.loadAllWeapons();
        treePane = new TreeViewPane<>();
        Mh4NodeDesigner designer = new Mh4NodeDesigner(manager);
        ItemView infoPanel = new ItemView(manager, designer, treePane);
        treePane.setNodeDesigner(designer);
        model = new TreeDataModel(manager, treePane, designer, infoPanel);
        treePane.setTree(manager.getTree());
        
        Container contentPane = this.getContentPane();
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
				model.back();
			}
		});
        controlPanel.add(btnBack);
        
        JTextField field = new JTextField(50);
        field.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = field.getText();
				treePane.scrollTo(new TextInBox(text));
			}
		});
        controlPanel.add(field);
        
        
        JComboBox<Object> comboBox = new JComboBox<Object>(manager.getWeaponTypes().toArray());
        comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedItem = comboBox.getSelectedItem().toString();
				model.changeWeaponTree(selectedItem);
			}
		});
        
        controlPanel.add(comboBox);
        //controlPanel.add(new JLabel("Controls: Drag to pan | Scroll to zoom | Double-Left: View Sub-Tree | Double-Middle: Open Weapon in Browser | Double-Right: View Path to Weapon"));
        JCheckBox checkBox = new JCheckBox();
        checkBox.setText("Display all weapons");
        checkBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				designer.displayAll(e.getStateChange() != ItemEvent.DESELECTED);
				treePane.repaint();
			}
		});
        controlPanel.add(checkBox);
        
        
        contentPane.add(controlPanel, BorderLayout.NORTH);
        contentPane.add(treePane, BorderLayout.CENTER);
        contentPane.add(infoPanel, BorderLayout.EAST);
        pack();
        setSize(1600, 900);
        setLocationRelativeTo(null);
        
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
