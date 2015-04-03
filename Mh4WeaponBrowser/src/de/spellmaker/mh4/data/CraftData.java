package de.spellmaker.mh4.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CraftData {
	public final int crafttype;
	public final Item[] items;
	public final int weaponId;
	
	public CraftData(int weaponId, ResultSet set) throws SQLException{
		this.weaponId = weaponId;
		int pos = 0;
		items = new Item[4];
		crafttype = set.getInt(5);
		do{
			if(set.getInt(5) != crafttype || set.getInt(1) != weaponId) break;
			items[pos] = new Item(set);
			pos++;
		}while(set.next());
	}
	
	public boolean enoughItems(WeaponManager manager){
		for(Item i : items){
			if(i != null){
				if(manager.getItemAmount(i.id) < i.quantity){
					return false;
				}
			}
		}
		return true;
	}
}
