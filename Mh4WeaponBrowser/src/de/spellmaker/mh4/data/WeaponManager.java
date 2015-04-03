package de.spellmaker.mh4.data;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
	private Connection dConn;
	private Connection sConn;
	private Map<Integer, WeaponTree> weaponTrees;
	private List<String> weaponTypes;
	private int activeTree;
	private WeaponTree tree;
	private Map<Integer, CraftData> weaponCreate;
	private Map<Integer, CraftData> weaponUpgrade;
	
	public WeaponManager(Connection dConn, Connection sConn) throws SQLException{
		this.dConn = dConn;
		this.sConn = sConn;
		
		weaponTrees = new HashMap<Integer, WeaponTree>();
		weaponCreate = new HashMap<>();
		weaponUpgrade = new HashMap<>();
		
		Statement s = dConn.createStatement();
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
	
	private void importSaves() throws SQLException{
		Statement s = sConn.createStatement();
		ResultSet rs = s.executeQuery("SELECT name From sqlite_master WHERE type='table' AND name = 'BuildList'");
		boolean load = false;
		
		if(!rs.next()){
			load = true;
		}
		s.close();
		
		if(load){
			System.out.println("BuildList not found in save database, creating it");
			s = sConn.createStatement();
			s.execute("CREATE TABLE 'BuildList' ('weaponId' INTEGER, PRIMARY KEY(weaponId))");
			
			File saveFile = new File("save.txt");
			if(saveFile.exists()){
				try{
					System.out.println("importing legacy save file into database");
					String sql = "SELECT Weapon.id, Weapon.local_name FROM Weapon WHERE ";
					
					boolean first = true;
					
					BufferedReader br = new BufferedReader(new FileReader(saveFile));
					
					while(br.ready()){
						String tmp = br.readLine();
						if(tmp.equals("")) continue;
						if(first) first = false;
						else sql += " OR ";
						sql += "Weapon.local_name = '" + tmp.replace("'", "''") + "'";
					}
					
					br.close();
					if(!first){
						System.out.println(sql);
						
						Statement search = dConn.createStatement();
						ResultSet searchResult = search.executeQuery(sql);
						sql = "";
						PreparedStatement ps = sConn.prepareStatement("INSERT INTO BuildList (weaponId) VALUES (?)");
						
						while(searchResult.next()){
							ps.setInt(1, searchResult.getInt(1));
							ps.executeUpdate();
							//sql += "INSERT INTO BuildList (weaponId) VALUES (" + searchResult.getInt(1) + ");";
						}
						search.close();
						ps.close();
						//System.out.println(sql);
						//s = sConn.createStatement();
						//s.execute(sql);
						s.close();
					}
					
					System.out.println("successfully converted save file");
				}
				catch(IOException e){
					System.out.println("reading save file failed");
				}
			}
		}
	}
	
	public void loadAllWeapons() throws SQLException{
		importSaves();
		
		Statement build = sConn.createStatement();
		ResultSet buildWeapons = build.executeQuery("SELECT * FROM BuildList");
		Map<Integer, Boolean> buildMap = new HashMap<Integer, Boolean>();
		while(buildWeapons.next()){
			buildMap.put(buildWeapons.getInt(1), true);
		}
		build.close();
		
		Statement s = dConn.createStatement();
		String query = "SELECT Weapon.*, MAX(Rank.id) FROM Weapon JOIN ItemWeaponPivot ON Weapon.id = ItemWeaponPivot.weapon_id JOIN Items ON ItemWeaponPivot.item_id = Items.id JOIN Rank ON (Items.rarity >= Rank.rarity_lowest AND Items.rarity <= Rank.rarity_highest) GROUP BY Weapon.id";
		ResultSet allWeapons = s.executeQuery(query);
		while(allWeapons.next()){
			Weapon current = new Weapon(allWeapons);
			weaponTrees.get(current.getWeapontype_id()).putWeapon(current, buildMap.get(current.getId()) != null);
		}
		s.close();
		//load crafts
		s = dConn.createStatement();
		query = "SELECT ItemWeaponPivot.weapon_id, Items.id, Items.rarity, Items.local_name, ItemWeaponPivot.weaponcrafttype_id, ItemWeaponPivot.quantity, Items.link FROM Items JOIN ItemWeaponPivot ON Items.id = ItemWeaponPivot.item_id ORDER BY ItemweaponPivot.weapon_id, ItemweaponPivot.weaponcrafttype_id";
		ResultSet allCrafts = s.executeQuery(query);
		while(!allCrafts.isAfterLast()){
			int id = allCrafts.getInt(1);
			CraftData current = new CraftData(id, allCrafts);
			if(current.crafttype == 1) 	weaponCreate.put(id, current);
			else 						weaponUpgrade.put(id, current);
		}
	
	}
	
	public boolean isBuilt(Weapon w){
		return tree.isBuilt(w);
	}
	
	public void setBuilt(Weapon w, boolean val){
		//find weapon tree
		WeaponTree resp = weaponTrees.get(w.getWeapontype_id());
		if(resp.isBuilt(w) != val){
			try{
				Statement s = sConn.createStatement();
				if(val)  {
					s.execute("INSERT INTO BuildList (weaponId) VALUES (" + w.getId() + ")");
				}
				else{
					s.execute("DELETE FROM BuildList WHERE BuildList.weaponId = " + w.getId());
				}
				s.close();
				
				resp.setBuilt(w, val);
			}
			catch(SQLException e){
				System.out.println("error: database access failed");
				e.printStackTrace();
			}
		}
	}
	
	public int getItemAmount(int id){
		try{
			Statement s = sConn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM Items WHERE Items.id = " + id);
			if(rs.next()){
				int res = rs.getInt(2);
				s.close();
				return res;
			}
			else{
				s.close();
				return 0;
			}
		}
		catch(SQLException e){
			System.out.println("could not fetch save files");
			return 0;
		}
	}
	
	public void setItemAmount(int id, int amount){
		try{
			Statement s = sConn.createStatement();
			s.executeUpdate("INSERT OR REPLACE INTO Items (id, amount) VALUES (" + id + ", " + amount + ")");
			s.close();
		}
		catch(SQLException e){
			System.out.println("error saving");
		}
	}
	
	public Weapon getWeapon(int id){
		return tree.getWeapons().get(id);
	}
	
	public CraftData getCreateCraftData(int id){
		return weaponCreate.get(id);
	}
	
	public CraftData getUpgradeCraftData(int id){
		return weaponUpgrade.get(id);
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
