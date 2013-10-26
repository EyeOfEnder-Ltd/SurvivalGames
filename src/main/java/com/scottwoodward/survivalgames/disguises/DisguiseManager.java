package com.scottwoodward.survivalgames.disguises;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.scottwoodward.survivalgames.SurvivalGames;

public class DisguiseManager {
	
	private static DisguiseManager instance;
	private List<Disguise> disguises;
	
	public static DisguiseManager getInstance(){
		if(instance == null){
			instance = new DisguiseManager();
		}
		return instance;
	}
	
	private DisguiseManager(){
		disguises = new ArrayList<Disguise>();
	}
	
	public void loadDisguises(){
        String path = SurvivalGames.getInstance().getDataFolder() + File.separator + "Disguises";
        File dir = new File(path);
        String[] files = dir.list();
        for(int i = 0; i < files.length; i++){
            File file = new File(path + File.separator + files[i]);
            FileConfiguration disguiseFile = new YamlConfiguration();
            try {
                disguiseFile.load(file);
                String type = disguiseFile.getString("Type");
                int icon = disguiseFile.getInt("Icon");
                short iconData = (short)disguiseFile.getInt("IconData");
                int weapon = disguiseFile.getInt("Weapon");
                String enchantment = disguiseFile.getString("Enchantment");
                String potion = disguiseFile.getString("Potion");
                String permission = disguiseFile.getString("Permission");
                Disguise disguise = new Disguise(type, weapon, enchantment, potion, permission, icon, iconData);
                disguises.add(disguise);
                SurvivalGames.getInstance().getLogger().log(Level.INFO, type + " " + icon + " " + iconData + " " + weapon + " " + enchantment + " " + potion + " " + permission);
            }catch(Exception e){
            	e.printStackTrace();
            }
        }
    }
	
	public List<Disguise> getDisguises(){
		return disguises;
	}

}
