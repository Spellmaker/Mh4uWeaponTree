import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.Configuration.Location;
import org.abego.treelayout.util.DefaultConfiguration;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;


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
		ResultSet allWeapons = s.executeQuery("SELECT * FROM Weapon");
		while(allWeapons.next()){
			Weapon current = new Weapon(allWeapons);
			weaponTrees.get(current.getWeapontype_id()).putWeapon(current);
		}
	}
	
	public Weapon getWeapon(int id){
		return tree.getWeapons().get(id);
	}
	
	public TreeLayout<TextInBox> getTree(){
		RootWeapon rootWeapon = new RootWeapon();
		TextInBox root = new TextInBox(rootWeapon);
		
		DefaultTreeForTreeLayout<TextInBox> resultTree = new DefaultTreeForTreeLayout<TextInBox>(root);
		for(Weapon w : tree.getRootWeapons()){
			TextInBox txt = new TextInBox(w);
			
			resultTree.addChild(root, txt);
			putChildren(resultTree, w, txt);
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
