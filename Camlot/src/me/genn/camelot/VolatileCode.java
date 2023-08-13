//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.genn.camelot;

import com.nisovin.magicspells.MagicSpells;
import java.util.Iterator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class VolatileCode {
    String name = "Blank";
    double timeRemaining = 200.0D;

    public VolatileCode() {
    }

    public void sendEnderDragonToAllPlayers() {


        //for(int i$ = 0; i$ < len$; ++i$) {
        //   Player p = arr$[i$];
        //    MagicSpells.getBossBarManager().setPlayerBar(p, this.name + "Shrine Power", 1.0D);
        //}
    	Iterator i = Bukkit.getOnlinePlayers().iterator();
        while(i.hasNext()) {
        	Player p = (Player)i.next();
        	MagicSpells.getBossBarManager().setPlayerBar(p, this.name, 1.0D);
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Camelot.plugin, new Runnable() {
            public void run() {
            	Iterator i = Bukkit.getOnlinePlayers().iterator();
                while(i.hasNext()) {
                	Player p = (Player)i.next();
                	MagicSpells.getBossBarManager().setPlayerBar(p, VolatileCode.this.name, Camelot.roundTime / 200.0D);
                }

            }
        }, 1L, 10L);
    }

    public void sendEnderDragonToPlayer(Player p) {
    	MagicSpells.getBossBarManager().setPlayerBar(p, this.name + "Shrine Power", 1.0D);
    }

    public void removeEnderDragonForAllPlayers() {
    	Iterator i = Bukkit.getOnlinePlayers().iterator();
        while(i.hasNext()) {
        	Player p = (Player)i.next();
        	MagicSpells.getBossBarManager().removePlayerBar(p);
        }

    }

    public void setDragonHealth(int remainingTime) {
    	Iterator i = Bukkit.getOnlinePlayers().iterator();
        while(i.hasNext()) {
        	Player p = (Player)i.next();
        	MagicSpells.getBossBarManager().setPlayerBar(p, this.name, remainingTime / 200.0D);
        }

        this.timeRemaining = (double)remainingTime;

        }


    public void setDragonName(String team) {
        
        
        Iterator i = Bukkit.getOnlinePlayers().iterator();
        while(i.hasNext()) {
        	Player p = (Player)i.next();
        	if (team.equalsIgnoreCase("red")) {
        		this.name = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "The RED team contols the castle!";
            	MagicSpells.getBossBarManager().setPlayerBar(p, this.name, this.timeRemaining / 200.0D);
        	} else if (team.equalsIgnoreCase("blue")) {
        		this.name = ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + "The BLUE team contols the castle!";
            	MagicSpells.getBossBarManager().setPlayerBar(p, this.name, this.timeRemaining / 200.0D);
        	} else if (team.equalsIgnoreCase("green")) {
        		this.name = ChatColor.DARK_GREEN.toString() + ChatColor.BOLD.toString() + "The GREEN team contols the castle!";
            	MagicSpells.getBossBarManager().setPlayerBar(p, this.name, this.timeRemaining / 200.0D);
        	} else {
        		this.name = ChatColor.WHITE.toString() + "No team controls the castle!";
                MagicSpells.getBossBarManager().setPlayerBar(p, this.name, this.timeRemaining / 200.0D);
        	}
        }

        


    }


}




