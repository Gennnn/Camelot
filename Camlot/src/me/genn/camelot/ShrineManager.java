//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.genn.camelot;

import com.nisovin.magicspells.util.BoundingBox;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;


public class ShrineManager {
    Camelot plugin;
    ShrineManager.SpawnProtector spawnProtector;
    Location redSpawn;
    Location greenSpawn;
    Location blueSpawn;
    int redSpawnInvuln;
    int greenSpawnInvuln;
    int blueSpawnInvuln;
    List<ShrineManager.Shrine> shrines = new ArrayList();
    int currentShrine = 0;
    List<String> shrineImmune = new ArrayList();
    String keepController;

    public ShrineManager(Camelot plugin, Configuration config) {
        this.plugin = plugin;
        this.spawnProtector = new ShrineManager.SpawnProtector();
        World world = (World)Bukkit.getWorlds().get(0);
        String[] gsp = config.getString("green-spawn", "0,100,0,0").split(",");
        String[] rsp = config.getString("red-spawn", "0,100,0,0").split(",");
        String[] bsp = config.getString("blue-spawn", "0,100,0,0").split(",");
        this.greenSpawn = new Location(world, Double.parseDouble(gsp[0]), Double.parseDouble(gsp[1]), Double.parseDouble(gsp[2]), Float.parseFloat(gsp[3]), 0.0F);
        this.redSpawn = new Location(world, Double.parseDouble(rsp[0]), Double.parseDouble(rsp[1]), Double.parseDouble(rsp[2]), Float.parseFloat(rsp[3]), 0.0F);
        this.blueSpawn = new Location(world, Double.parseDouble(bsp[0]), Double.parseDouble(bsp[1]), Double.parseDouble(bsp[2]), Float.parseFloat(bsp[3]), 0.0F);
        this.keepController = "Uncontrolled";
        this.redSpawnInvuln = config.getInt("red-spawn-invuln", 10);
        this.greenSpawnInvuln = config.getInt("green-spawn-invuln", 10);
        this.blueSpawnInvuln = config.getInt("blue-spawn-invuln", 10);
        Set<String> keys = config.getConfigurationSection("shrines").getKeys(false);
        Iterator i$ = keys.iterator();

        while(i$.hasNext()) {
            String key = (String)i$.next();
            ShrineManager.Shrine shrine = new ShrineManager.Shrine(config.getConfigurationSection("shrines." + key));
            this.shrines.add(shrine);
        }

    }

    

    public boolean playerNearShrineForCapture(Player player) {
        return ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).captureBoundingBox.contains(player);
    }

    public boolean playerNearShrineForPoints(Player player) {
        return ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).pointsBoundingBox.contains(player);
    }
    
    public String getControllingTeam() {
        return this.keepController;
    }
    
    public void setControllingTeam(String newController) {
    	this.keepController = newController;
    }

    public boolean changeShrineGreen() {
        ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).changeToGreen();
        if (this.currentShrine == this.shrines.size() - 1) {
            return true;
        } else {
            ++this.currentShrine;
            return false;
        }
    }
    public boolean changeShrineRed() {
        ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).changeToRed();
        if (this.currentShrine == this.shrines.size() - 1) {
            return true;
        } else {
            ++this.currentShrine;
            return false;
        }
    }
    public boolean changeShrineBlue() {
        ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).changeToBlue();
        if (this.currentShrine == this.shrines.size() - 1) {
            return true;
        } else {
            ++this.currentShrine;
            return false;
        }
    }

    public void respawnGreen(Player player) {
        player.teleport(this.greenSpawn);
        int protect = this.currentShrine == 0 ? this.greenSpawnInvuln : ((ShrineManager.Shrine)this.shrines.get(this.currentShrine - 1)).greenSpawnInvuln;
        if (protect > 0) {
            this.spawnProtector.protectFor(player, protect);
        }

    }

    public void respawnRed(Player player) {
        player.teleport(this.redSpawn);
        int protect = this.currentShrine == 0 ? this.redSpawnInvuln : ((ShrineManager.Shrine)this.shrines.get(this.currentShrine - 1)).redSpawnInvuln;
        if (protect > 0) {
            this.spawnProtector.protectFor(player, protect);
        }

    }
    
    public void respawnBlue(Player player) {
        player.teleport(this.blueSpawn);
        int protect = this.currentShrine == 0 ? this.blueSpawnInvuln : ((ShrineManager.Shrine)this.shrines.get(this.currentShrine - 1)).blueSpawnInvuln;
        if (protect > 0) {
            this.spawnProtector.protectFor(player, protect);
        }

    }

    public Location getGreenSpawn() {
        Location loc;
        if (this.currentShrine == 0) {
            loc = this.greenSpawn.clone();
        } else {
            loc = ((ShrineManager.Shrine)this.shrines.get(this.currentShrine - 1)).greenSpawn.clone();
        }

        for(Block block = loc.getBlock(); block.getType() != Material.AIR; block = loc.getBlock()) {
            loc.add(0.0D, 1.0D, 0.0D);
        }

        loc.add(0.5D, 0.5D, 0.5D);
        return loc;
    }

    public Location getRedSpawn() {
        Location loc;
        if (this.currentShrine == 0) {
            loc = this.redSpawn.clone();
        } else {
            loc = ((ShrineManager.Shrine)this.shrines.get(this.currentShrine - 1)).redSpawn.clone();
        }

        for(Block block = loc.getBlock(); block.getType() != Material.AIR; block = loc.getBlock()) {
            loc.add(0.0D, 1.0D, 0.0D);
        }

        loc.add(0.5D, 0.5D, 0.5D);
        return loc;
    }

    public Location getCurrentShrine() {
        return ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).center.clone();
    }

    public Location getCurrentShrineForTeleport() {
        Location loc = ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).center.clone();

        for(Block block = loc.getBlock(); block.getType() != Material.AIR; block = loc.getBlock()) {
            loc.add(0.0D, 1.0D, 0.0D);
        }

        loc.add(0.5D, 0.5D, 0.5D);
        return loc;
    }


    public Location getFinalShrine() {
        return ((ShrineManager.Shrine)this.shrines.get(this.shrines.size() - 1)).center.clone();
    }

    public boolean atFirstShrine() {
        return this.currentShrine == 0;
    }

    public boolean atFinalShrine() {
        return this.currentShrine == this.shrines.size() - 1;
    }

    public double getBlueValue() {
        return ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).blueValue;
    }

    public double getRedValue() {
        return ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).redValue;
    }

    public double getGreenValue() {
        return ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).greenValue;
    }

    public boolean shouldRestore() {
        return ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).restore;
    }

    public double getMaxShrinePower() {
        return ((ShrineManager.Shrine)this.shrines.get(this.currentShrine)).maxPower;
    }

    

    public void setShrineImmunity(Player player, boolean immune) {
        if (immune) {
            this.shrineImmune.add(player.getName());
        } else {
            this.shrineImmune.remove(player.getName());
        }

    }

   

    class Shrine {
        Location center;
        Location greenSpawn;
        Location redSpawn;
        Location blueSpawn;
        BoundingBox captureBoundingBox;
        int captureRadius;
        BoundingBox pointsBoundingBox;
        int pointsRadius;
        double blueValue;
        double greenValue;
        double redValue;
        boolean restore;
        double maxPower;
        double decay;
        int decayAfter;
        int pulseRange;
        int redSpawnInvuln;
        int blueSpawnInvuln;
        int greenSpawnInvuln;
        int keepRadius;
        
        
        public Shrine(ConfigurationSection config) {
            String s = config.getString("coords");
            String[] coords = s.split(",");
            this.center = new Location((World)Bukkit.getWorlds().get(0), Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]), Float.parseFloat(coords[3]), 0.0F);
            coords = config.getString("green-spawn", s).split(",");
            this.greenSpawn = new Location((World)Bukkit.getWorlds().get(0), Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]), Float.parseFloat(coords[3]), 0.0F);
            this.captureRadius = config.getInt("capture-radius", 10);
            this.captureBoundingBox = new BoundingBox(this.center, (double)this.captureRadius);
            this.pointsRadius = config.getInt("points-radius", 500);
            this.pointsBoundingBox = new BoundingBox(this.center, (double)this.pointsRadius);
            this.redValue = config.getDouble("red-value", 0.5D);
            this.greenValue = config.getDouble("green-value", 1.0D);
            this.blueValue = config.getDouble("blue-value", 5.0D);
            this.restore = config.getBoolean("restore", true);
            this.maxPower = (double)config.getInt("max-power", 100);
            this.decay = config.getDouble("decay", 0.0D);
            this.decayAfter = config.getInt("decay-after", 0);
            this.pulseRange = config.getInt("pulse-range", 20);
            this.keepRadius = config.getInt("keep-radius", 30);
            System.out.println("LOADED SHRINE: center=" + this.center + "; caprad=" + this.captureRadius + "; greenv=" + this.greenValue + "; redv=" + this.redValue + "; bluev=" + this.blueValue + "; pulse=" + this.pulseRange);
        }

        public void changeToGreen() {
            for(int x = this.center.getBlockX() - keepRadius; x <= this.center.getBlockX() + keepRadius; ++x) {
                for(int y = this.center.getBlockY() - keepRadius; y <= this.center.getBlockY() + keepRadius; ++y) {
                    for(int z = this.center.getBlockZ() - keepRadius; z <= this.center.getBlockZ() + keepRadius; ++z) {
                        Block block = this.center.getWorld().getBlockAt(x, y, z);
                        if (block.getType() == Material.WOOL && block.getData() == 11) {
                            block.setData((byte)13);
                        } else if (block.getType() == Material.CARPET && block.getData() == 11) {
                            block.setData((byte)13);
                        } else if (block.getType() == Material.STAINED_GLASS && block.getData() == 11) {
                            block.setData((byte)13);
                        } else if (block.getType() == Material.WOOL && block.getData() == 14) {
                            block.setData((byte)13);
                        } else if (block.getType() == Material.CARPET && block.getData() == 14) {
                            block.setData((byte)13);
                        } else if (block.getType() == Material.STAINED_GLASS && block.getData() == 14) {
                            block.setData((byte)13);
                        }
                    }
                }
            }

        }
        public void changeToRed() {
            for(int x = this.center.getBlockX() - keepRadius; x <= this.center.getBlockX() + keepRadius; ++x) {
                for(int y = this.center.getBlockY() - keepRadius; y <= this.center.getBlockY() + keepRadius; ++y) {
                    for(int z = this.center.getBlockZ() - keepRadius; z <= this.center.getBlockZ() + keepRadius; ++z) {
                        Block block = this.center.getWorld().getBlockAt(x, y, z);
                        if (block.getType() == Material.WOOL && block.getData() == 11) {
                            block.setData((byte)14);
                        } else if (block.getType() == Material.CARPET && block.getData() == 11) {
                            block.setData((byte)14);
                        } else if (block.getType() == Material.STAINED_GLASS && block.getData() == 11) {
                            block.setData((byte)14);
                        } else if (block.getType() == Material.WOOL && block.getData() == 13) {
                            block.setData((byte)14);
                        } else if (block.getType() == Material.CARPET && block.getData() == 13) {
                            block.setData((byte)14);
                        } else if (block.getType() == Material.STAINED_GLASS && block.getData() == 13) {
                            block.setData((byte)14);
                        }
                    }
                }
            }

        }
        
        public void changeToBlue() {
            for(int x = this.center.getBlockX() - keepRadius; x <= this.center.getBlockX() + keepRadius; ++x) {
                for(int y = this.center.getBlockY() - keepRadius; y <= this.center.getBlockY() + keepRadius; ++y) {
                    for(int z = this.center.getBlockZ() - keepRadius; z <= this.center.getBlockZ() + keepRadius; ++z) {
                        Block block = this.center.getWorld().getBlockAt(x, y, z);
                        if (block.getType() == Material.WOOL && block.getData() == 13) {
                            block.setData((byte)11);
                        } else if (block.getType() == Material.CARPET && block.getData() == 13) {
                            block.setData((byte)11);
                        } else if (block.getType() == Material.STAINED_GLASS && block.getData() == 13) {
                            block.setData((byte)11);
                        } else if (block.getType() == Material.WOOL && block.getData() == 14) {
                            block.setData((byte)11);
                        } else if (block.getType() == Material.CARPET && block.getData() == 14) {
                            block.setData((byte)11);
                        } else if (block.getType() == Material.STAINED_GLASS && block.getData() == 14) {
                            block.setData((byte)11);
                        }
                    }
                }
            }

        }
        
    }
        

    class SpawnProtector implements Listener {
        Set<String> protect = new HashSet();

        public SpawnProtector() {
            Bukkit.getPluginManager().registerEvents(this, ShrineManager.this.plugin);
        }

        public void protectFor(final Player player, int duration) {
            this.protect.add(player.getName());
            Bukkit.getScheduler().scheduleSyncDelayedTask(ShrineManager.this.plugin, new Runnable() {
                public void run() {
                    SpawnProtector.this.protect.remove(player.getName());
                }
            }, (long)(duration * 20));
        }

        @EventHandler
        public void onDamage(EntityDamageEvent event) {
            if (event.getEntity() instanceof Player && this.protect.contains(((Player)event.getEntity()).getName())) {
                event.setCancelled(true);
            }

        }

        @EventHandler
        public void onTeleport(PlayerTeleportEvent event) {
            this.protect.remove(event.getPlayer().getName());
        }
    }
}
