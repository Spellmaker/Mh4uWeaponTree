package de.spellmaker.mh4.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Item {
	public final int id;
	public final int rarity;
	public final String name;
	public final int quantity;
	public final String link;
	
	public Item(ResultSet set) throws SQLException{
		this.id = set.getInt(2);
		this.rarity = set.getInt(3);
		this.name = set.getString(4);
		this.quantity = set.getInt(6);
		this.link = set.getString(7);
	}
}
