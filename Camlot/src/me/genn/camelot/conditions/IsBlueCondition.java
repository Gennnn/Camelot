//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.genn.camelot.conditions;

import com.nisovin.magicspells.castmodifiers.Condition;
import me.genn.camelot.Camelot;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class IsBlueCondition extends Condition {
    public IsBlueCondition() {
    }

    public boolean setVar(String var) {
        return true;
    }

    public boolean check(Player player) {
        System.out.println("ISBLUE (player): " + player.getName() + " " + (Camelot.plugin.bluePlayers.contains(player.getName()) ? "yes" : "no"));
        return Camelot.plugin.bluePlayers.contains(player.getName());
    }

    public boolean check(Player player, LivingEntity target) {
        System.out.println("ISBLUE (target): " + player.getName() + " + " + target);
        if (target instanceof Player) {
            System.out.println("  IS PLAYER");
            return this.check((Player)target);
        } else {
            return false;
        }
    }

    public boolean check(Player player, Location target) {
        return false;
    }
}
