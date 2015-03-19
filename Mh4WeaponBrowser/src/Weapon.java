import java.sql.ResultSet;
import java.sql.SQLException;


public class Weapon {
	public int getId() {
		return id;
	}

	public int getWeapontype_id() {
		return weapontype_id;
	}

	public int getWeapon_parent_id() {
		return weapon_parent_id;
	}

	public boolean isWeapon_final() {
		return weapon_final;
	}

	public int getAttack() {
		return attack;
	}

	public int getDefense() {
		return defense;
	}

	public int getAffinity() {
		return affinity;
	}

	public int getAffinity_virus() {
		return affinity_virus;
	}

	public int getRarity() {
		return rarity;
	}

	public int getSlot() {
		return slot;
	}

	public int getPrice_create() {
		return price_create;
	}

	public int getPrice_upgrade() {
		return price_upgrade;
	}

	public String getLocal_name() {
		return local_name;
	}

	public String getLocal_description() {
		return local_description;
	}

	public String getLink() {
		return link;
	}

	private int id;
	private int weapontype_id;
	private int weapon_parent_id;
	private boolean weapon_final;
	private int attack;
	private int defense;
	private int affinity;
	private int affinity_virus;
	private int rarity;
	private int slot;
	private int price_create;
	private int price_upgrade;
	private String local_name;
	private String local_description;
	private String link;
	
	private boolean dummy;
	
	public boolean isDummy(){
		return dummy;
	}
	
	protected Weapon(){
		this.dummy = true;
	}
	
	public Weapon(ResultSet source) throws SQLException{
		id = source.getInt(1);
		weapontype_id = source.getInt(2);
		weapon_parent_id = source.getInt(3);
		weapon_final = source.getInt(4) == 1;
		attack = source.getInt(5);
		defense = source.getInt(6);
		affinity = source.getInt(7);
		affinity_virus = source.getInt(8);
		rarity = source.getInt(9);
		slot = source.getInt(10);
		price_create = source.getInt(11);
		price_upgrade = source.getInt(12);
		local_name = source.getString(13);
		local_description = source.getString(14);
		link = source.getString(15);
		dummy = false;
	}
}
