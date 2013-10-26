package com.scottwoodward.survivalgames.disguises;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Disguise implements Cloneable{
	
	private EntityType type;
	private ItemStack weapon;
	private PotionEffect effect;
	private String permission;
	private ItemStack icon;
	
	Disguise(String typeName, int itemID, String enchantment, String potionName, String permission, int iconID, int iconData) throws Exception{
		type = EntityType.fromName(typeName);
		weapon = new ItemStack(Material.getMaterial(itemID));
		Enchantment enchant = Enchantment.getByName(enchantment);
		weapon.addEnchantment(enchant, 1);
		effect = new PotionEffect(PotionEffectType.getByName(potionName), 120, 0);
		icon = new ItemStack(iconID, 1, (short)iconData);
		this.permission = permission;
	}
	
	private Disguise(EntityType type, ItemStack weapon, PotionEffect effect, String permission, ItemStack icon){
		this.type = type;
		this.weapon = weapon;
		this.effect = effect;
		this.permission = permission;
		this.icon = icon;
	}
	
	public Disguise clone(){
		return new Disguise(type, weapon.clone(), effect, permission, icon.clone());
	}
	
	public String getPermission(){
		return permission;
	}
	
	public ItemStack getWeapon(){
		return weapon;
	}
	
	public PotionEffect getEffect(){
		return effect;
	}
	
	public EntityType getType(){
		return type;
	}
	
	public ItemStack getIcon(){
		return icon;
	}

}
