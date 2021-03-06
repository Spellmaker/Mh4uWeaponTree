package de.spellmaker.mh4.data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeaponTree {
	private int weaponTypeId;
	private String treeName;
	private Map<Integer, Weapon> allWeapons;
	private Map<Integer, List<Weapon>> weaponChildren;
	private List<Weapon> finalWeapons;
	private List<Weapon> rootWeapons;
	private Map<Weapon, Boolean> weaponBuilt;
	
	public WeaponTree(int id, String name){
		this.weaponTypeId = id;
		this.treeName = name;
		
		allWeapons = new HashMap<Integer, Weapon>();
		finalWeapons = new ArrayList<Weapon>();
		rootWeapons = new ArrayList<Weapon>();
		weaponChildren = new HashMap<Integer, List<Weapon>>();
		weaponBuilt = new HashMap<Weapon, Boolean>();
	}
	
	public String getName(){
		return treeName;
	}
	
	public int getId(){
		return weaponTypeId;
	}
	
	public boolean isBuilt(Weapon w){
		Boolean state = weaponBuilt.get(w);
		return (state != null) && state;
	}
	
	public void setBuilt(Weapon w, boolean built){
		weaponBuilt.put(w, built);
	}
	
	public void putWeapon(Weapon w, boolean build){
		if(w.getWeapontype_id() == weaponTypeId){
			allWeapons.put(w.getId(), w);
			if(w.isWeapon_final()){
				finalWeapons.add(w);
			}
			if(w.getWeapon_parent_id() == 0){
				rootWeapons.add(w);
			}
			else{
				List<Weapon> currentList = weaponChildren.get(w.getWeapon_parent_id());
				if(currentList == null){
					currentList = new ArrayList<Weapon>();
					weaponChildren.put(w.getWeapon_parent_id(), currentList);
				}
				currentList.add(w);
			}
			
			weaponBuilt.put(w, build);
		}
	}
	
	public List<Weapon> getChildren(int id){
		List<Weapon> res = weaponChildren.get(id);
		if(res == null) return res;
		return Collections.unmodifiableList(res);
	}
	
	public Map<Integer, Weapon> getWeapons(){
		return Collections.unmodifiableMap(allWeapons);
	}
	
	public List<Weapon> getFinalWeapons(){
		return Collections.unmodifiableList(finalWeapons);
	}
	
	public List<Weapon> getRootWeapons(){
		return Collections.unmodifiableList(rootWeapons);
	}
}
