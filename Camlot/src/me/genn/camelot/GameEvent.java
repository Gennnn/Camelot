//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.genn.camelot;

import me.genn.camelot.Camelot;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class GameEvent {
    protected Camelot plugin;
    protected Random random;

    public GameEvent(Camelot plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public abstract String getName();

    public abstract void run();

    public abstract void setBlue(Player var1);
    
    public abstract void setRed(Player var1);
    
    public abstract void setGreen(Player var1);

    public abstract boolean doesPlayerGetExtraXp(Player var1);

    public abstract GameEvent.DamageResult checkDamage(Player var1, Player var2);

    public static enum DamageResult {
        NORMAL,
        ALLOW,
        DENY;

        private DamageResult() {
        }
    }
}
