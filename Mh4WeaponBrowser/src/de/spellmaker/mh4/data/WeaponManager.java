package de.spellmaker.mh4.data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.Configuration.Location;
import org.abego.treelayout.util.DefaultConfiguration;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;

import de.spellmaker.mh4.tree.TextInBox;
import de.spellmaker.mh4.tree.TextInBoxNodeExtentProvider;


public class WeaponManager {
	private Connection conn;
	private Map<Integer, WeaponTree> weaponTrees;
	private List<String> weaponTypes;
	private int activeTree;
	private WeaponTree tree;
	
	public WeaponManager(Connection c) throws SQLException{
		this.conn = c;
		
		weaponTrees = new HashMap<Integer, WeaponTree>();
		Statement s = conn.createStatement();
		ResultSet types = s.executeQuery("SELECT id, local_name FROM WeaponType");
		weaponTypes = new ArrayList<String>();
		while(types.next()){
			int typeId = types.getInt(1);
			String typeName = types.getString(2);
			weaponTrees.put(typeId, new WeaponTree(typeId, typeName));
			weaponTypes.add(typeName);
		}
		
		activeTree = 1;
		tree = weaponTrees.get(activeTree);
	}
	
	public void setActiveTree(int id){
		if(id >= 1 && id <= weaponTypes.size()){
			activeTree = id;
			tree = weaponTrees.get(activeTree);
		}
	}
	
	public void setActiveTree(String s){
		setActiveTree(weaponTypes.indexOf(s) + 1);
	}
	
	public List<String> getWeaponTypes(){
		return Collections.unmodifiableList(weaponTypes);
	}
	
	public void loadAllWeapons() throws SQLException{
		Statement s = conn.createStatement();
		String query = "SELECT Weapon.*, MAX(Rank.id) FROM Weapon JOIN ItemWeaponPivot ON Weapon.id = ItemWeaponPivot.weapon_id JOIN Items ON ItemWeaponPivot.item_id = Items.id JOIN Rank ON (Items.rarity >= Rank.rarity_lowest AND Items.rarity <= Rank.rarity_highest) GROUP BY Weapon.id";
		ResultSet allWeapons = s.executeQuery(query);
		while(allWeapons.next()){
			Weapon current = new Weapon(allWeapons);
			weaponTrees.get(current.getWeapontype_id()).putWeapon(current);
		}
	}
	
	public Weapon getWeapon(int id){
		return tree.getWeapons().get(id);
	}
	
	public TreeLayout<TextInBox> getReverseTree(TextInBox start, TextInBox end){
		DefaultTreeForTreeLayout<TextInBox> resultTree = null;
		Stack<TextInBox> nodes = new Stack<>();
		Weapon current = end.source;
		while(current != null && current.getId() != start.source.getId()){
			nodes.push(new TextInBox(current));
			current = getWeapon(current.getWeapon_parent_id());
		}
		
		TextInBox prev = nodes.pop();
		resultTree = new DefaultTreeForTreeLayout<TextInBox>(prev);
		
		while(!nodes.isEmpty()){
			resultTree.addChild(prev, nodes.peek());
			prev = nodes.pop();
		}
		
		double gapBetweenLevels = 50;
        double gapBetweenNodes = 10;
        DefaultConfiguration<TextInBox> configuration = new DefaultConfiguration<TextInBox>(
                        gapBetweenLevels, gapBetweenNodes, Location.Left);

        // create the NodeExtentProvider for TextInBox nodes
        TextInBoxNodeExtentProvider nodeExtentProvider = new TextInBoxNodeExtentProvider();

        // create the layout
        TreeLayout<TextInBox> treeLayout = new TreeLayout<TextInBox>(resultTree,
                        nodeExtentProvider, configuration);
		
		
		return treeLayout;	
	}
	
	public TreeLayout<TextInBox> getTree(){
		return getTree(null);
	}
	
	public TreeLayout<TextInBox> getTree(TextInBox start){
		DefaultTreeForTreeLayout<TextInBox> resultTree = null;
		if(start == null){
			RootWeapon rootWeapon = new RootWeapon();
			TextInBox root = new TextInBox(rootWeapon);
			resultTree = new DefaultTreeForTreeLayout<TextInBox>(root);
			for(Weapon w : tree.getRootWeapons()){
				TextInBox txt = new TextInBox(w);
				resultTree.addChild(root, txt);
				putChildren(resultTree, w, txt);
			}
		}
		else{
			TextInBox root = new TextInBox(start.source);
			resultTree = new DefaultTreeForTreeLayout<TextInBox>(root);
			for(Weapon w : tree.getChildren(start.source.getId())){
				TextInBox txt = new TextInBox(w);
				resultTree.addChild(root, txt);
				putChildren(resultTree, w, txt);
			}
		}
		double gapBetweenLevels = 50;
        double gapBetweenNodes = 10;
        DefaultConfiguration<TextInBox> configuration = new DefaultConfiguration<TextInBox>(
                        gapBetweenLevels, gapBetweenNodes, Location.Left);

        // create the NodeExtentProvider for TextInBox nodes
        TextInBoxNodeExtentProvider nodeExtentProvider = new TextInBoxNodeExtentProvider();

        // create the layout
        TreeLayout<TextInBox> treeLayout = new TreeLayout<TextInBox>(resultTree,
                        nodeExtentProvider, configuration);
		
		
		return treeLayout;	
	}
	
	private void putChildren(DefaultTreeForTreeLayout<TextInBox> t, Weapon root, TextInBox rootTxt){
		List<Weapon> children = tree.getChildren(root.getId());
		if(children != null){
			for(Weapon w : children){
				TextInBox txt = new TextInBox(w);
				t.addChild(rootTxt, txt);
				putChildren(t, w, txt);
			}
		}
	}
}
