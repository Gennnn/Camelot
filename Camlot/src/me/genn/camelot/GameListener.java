//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.genn.camelot;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scoreboard.Score;

import com.nisovin.magicspells.Spell.SpellCastState;
import com.nisovin.magicspells.events.SpellTargetEvent;

import me.genn.camelot.GameEvent.DamageResult;
import me.genn.gennsgym.GennsGym;
import me.genn.gennsgym.GennsGym.StatisticCallback;

public class GameListener implements Listener {
    Camelot plugin;

    public GameListener(Camelot plugin) {
        this.plugin = plugin;
    }

    @EventHandler(
        priority = EventPriority.LOWEST
    )
    public void onDeath(PlayerDeathEvent event) {
        final Player p = event.getEntity();
        event.getDrops().clear();
        event.setDroppedExp(0);
        boolean isBlue = this.plugin.bluePlayers.contains(p.getName());
        boolean isRed = this.plugin.redPlayers.contains(p.getName());
        boolean isGreen = this.plugin.greenPlayers.contains(p.getName());
        if (this.plugin.deathTimes.containsKey(p.getName())) {
            long time = (Long)this.plugin.deathTimes.get(p.getName());
            if (time > System.currentTimeMillis() - 5000L) {
                return;
            }
        }

        this.plugin.deathTimes.put(p.getName(), System.currentTimeMillis());
        int monsterKills = 0;
        

        if (this.plugin.gameRunning) {
            

            String playerName;
            Player player;
            Iterator i$;
            
            
            this.processKillForStats(p);
            if (!p.hasPermission("game.ignore")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nick " + p.getName() + " &7" + p.getName());
                    }
                }, 1L);
            }

            this.plugin.shrineManager.setShrineImmunity(p, false);
        }

    }

    private void processKillForStats(LivingEntity dead) {
        Player killer = null;
        String killerWeapon = "";
        killer = dead.getKiller();
        if (killer != null) {
            killerWeapon = this.getWeaponName(killer.getItemInHand());
        } else if (dead instanceof Player) {
            List<MetadataValue> meta = dead.getMetadata("KILLER");
            if (meta != null && meta.size() > 0) {
                Player k = Bukkit.getPlayerExact(((MetadataValue)meta.get(0)).asString());
                if (k != null) {
                    killer = k;
                }
            }

            meta = dead.getMetadata("KILLER_WEAPON");
            if (meta != null && meta.size() > 0) {
                killerWeapon = ChatColor.stripColor(((MetadataValue)meta.get(0)).asString());
            } else if (killer != null && killer.isValid()) {
                killerWeapon = this.getWeaponName(killer.getItemInHand());
            }
        }

        

        if (killer != null) {
            String killerName = killer.getName();
            if (this.plugin.bluePlayers.contains(killerName)) {
                this.plugin.killsAsBlue.increment(killerName);
                this.plugin.globalStats.increment("camelot_kills_as_blue");
            } else if (this.plugin.redPlayers.contains(killerName)) {
                this.plugin.killsAsRed.increment(killerName);
                this.plugin.globalStats.increment("camelot_kills_as_red");
            } else if (this.plugin.greenPlayers.contains(killerName)) {
                this.plugin.killsAsGreen.increment(killerName);
                this.plugin.globalStats.increment("camelot_kills_as_blue");
            }
                if (dead instanceof Player) {
                    String killedDisplayName = ChatColor.stripColor(((Player)dead).getDisplayName());
                    String killedName = ((Player)dead).getName();
                    

                if (this.plugin.shrineManager.playerNearShrineForPoints(killer)) {
                    if (!killerWeapon.equals("Dwarven Runeblade") && !killerWeapon.equals("Excaliju") && !killerWeapon.equals("Destination Bringer") && !killerWeapon.equals("Holy Blade") && !killerWeapon.equals("PROC Starter")) {
                        if (!killerWeapon.equals("Dwarven Shortbow") && !killerWeapon.equals("Dwarven Longbow") && !killerWeapon.equals("Nohdabagel") && !killerWeapon.equals("Virendra") && !killerWeapon.equals("Tinder Flame") && !killerWeapon.equals("Weetigo")) {
                            
                        } else {
                            this.plugin.bowKills.increment(killerName);
                            this.plugin.globalStats.increment("camelot_bow_kills");
                        }
                    } else {
                        this.plugin.swordKills.increment(killerName);
                        this.plugin.globalStats.increment("camelot_sword_kills");
                    }
                }

                this.plugin.killStreakTracker.addKill(killerName);
            } 

            if (dead instanceof Player) {
                Location loc = dead.getLocation();
                GennsGym.addKill(killerName, ((Player)dead).getName(), ChatColor.stripColor(killer.getDisplayName()), ChatColor.stripColor(((Player)dead).getDisplayName()), killerWeapon, this.plugin.mapName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            }
        
        }
    }

    private String getWeaponName(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? ChatColor.stripColor(item.getItemMeta().getDisplayName()) : "";
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (this.plugin.gameRunning) {
            final Player player = event.getPlayer();
            this.plugin.timeAtSpawn.remove(player.getName());
            this.plugin.joinedTimes.put(player.getName(), System.currentTimeMillis());
            if (this.plugin.volatileCode != null) {
                this.plugin.volatileCode.sendEnderDragonToPlayer(event.getPlayer());
            }

            if (player.hasPermission("game.ignore")) {
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                public void run() {
                    if (!player.isDead()) {
                        if (GameListener.this.plugin.gameRunning) {
                        	Random random = new Random();
                            int justify = random.nextInt(3);
                            justify = justify + 3;
                                if (justify % 3 == 1) {
                                    GameListener.this.plugin.setAsRed(player);
                                } else if (justify % 2 == 0) {
                                	GameListener.this.plugin.setAsGreen(player);
                                } else if (justify % 3 == 2) {
                                	GameListener.this.plugin.setAsBlue(player);
                                }

                            
                    }

                    }
                }
            }, 5L);
        } 
        
    }
    
    

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        if (this.plugin.gameRunning) {
            if (event.getPlayer().hasPermission("game.ignore")) {
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                public void run() {
                    Player player = event.getPlayer();
                    if (GameListener.this.plugin.volatileCode != null) {
                        GameListener.this.plugin.volatileCode.sendEnderDragonToPlayer(player);
                    }

                }
            }, 1L);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                public void run() {
                    Player player = event.getPlayer();
                    if (GameListener.this.plugin.bluePlayers.contains(player.getName())) {
                        GameListener.this.plugin.becomeBlueSpell.castSpell(player, SpellCastState.NORMAL, 1.0F, (String[])null);

                    } else if (GameListener.this.plugin.redPlayers.contains(player.getName())) {
                        GameListener.this.plugin.becomeRedSpell.castSpell(player, SpellCastState.NORMAL, 1.0F, (String[])null);

                    } else if (GameListener.this.plugin.greenPlayers.contains(player.getName())) {
                        GameListener.this.plugin.becomeGreenSpell.castSpell(player, SpellCastState.NORMAL, 1.0F, (String[])null);

                    }
                    
                }
            }, 10L);
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (this.plugin.gameRunning) {
            Player player = event.getPlayer();
            this.plugin.addTimePlayed(player);
        }

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().startsWith("!") && event.getMessage().length() > 1) {
            event.setMessage(event.getMessage().substring(1));
            event.setFormat("[!] " + event.getFormat());
        } else {
            Iterator iter;
            if (this.plugin.redPlayers.contains(event.getPlayer().getName())) {
                iter = event.getRecipients().iterator();

                while(iter.hasNext()) {
                    if (this.plugin.greenPlayers.contains(((Player)iter.next()).getName())) {
                        iter.remove();
                    } else if (this.plugin.bluePlayers.contains(((Player)iter.next()).getName())) {
                        iter.remove();
                    }
                }
            } else if (this.plugin.bluePlayers.contains(event.getPlayer().getName())) {
                iter = event.getRecipients().iterator();

                while(iter.hasNext()) {
                    if (this.plugin.greenPlayers.contains(((Player)iter.next()).getName())) {
                        iter.remove();
                    } else if (this.plugin.redPlayers.contains(((Player)iter.next()).getName())) {
                        iter.remove();
                    }
                }
            } else if (this.plugin.greenPlayers.contains(event.getPlayer().getName())) {
                iter = event.getRecipients().iterator();

                while(iter.hasNext()) {
                    if (this.plugin.redPlayers.contains(((Player)iter.next()).getName())) {
                        iter.remove();
                    } else if (this.plugin.bluePlayers.contains(((Player)iter.next()).getName())) {
                        iter.remove();
                    }
                }
            }

        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasPermission("game.ignore")) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!this.plugin.gameRunning && !this.plugin.gameEnded) {
            event.setCancelled(true);
        } 
    }

    @EventHandler(
            ignoreCancelled = true
        )
    public void onDamage2(EntityDamageByEntityEvent event) {
        if (!this.plugin.gameRunning && !this.plugin.gameEnded) {
            event.setCancelled(true);
        } else {
            if (event.getEntity() instanceof Player) {
                Player attacked = (Player)event.getEntity();
                Entity e = event.getDamager();
                if (e instanceof Projectile && ((Projectile)e).getShooter() instanceof Player) {
                    e = (Entity) ((Projectile)e).getShooter();
                }

                if (e instanceof Player) {
                    Player attacker = (Player)e;
                    DamageResult eventDamageCheck = this.plugin.gameEvent.checkDamage(attacker, attacked);
                    if (eventDamageCheck == DamageResult.DENY) {
                        event.setCancelled(true);
                    } else if (eventDamageCheck == DamageResult.NORMAL) {
                        if (this.plugin.redPlayers.contains(attacked.getName()) && this.plugin.redPlayers.contains(attacker.getName())) {
                            event.setCancelled(true);
                        } else if (this.plugin.bluePlayers.contains(attacked.getName()) && this.plugin.bluePlayers.contains(attacker.getName())) {
                            event.setCancelled(true);
                        } else if (this.plugin.greenPlayers.contains(attacked.getName()) && this.plugin.greenPlayers.contains(attacker.getName())) {
                            event.setCancelled(true);
                        }
                    }

                }
            }
        }
    }
    
    
    @EventHandler
    public void onProjectileLand(ProjectileHitEvent event) {
        event.getEntity().remove();
    }

    @EventHandler
    public void onSpellTarget(SpellTargetEvent event) {
        Player player = event.getCaster();
        if (!player.hasPermission("game.ignore")) {
            if (event.getTarget() instanceof Player) {
                Player target = (Player)event.getTarget();
                if (this.plugin.redPlayers.contains(player.getName())) {
                    if (event.getSpell().isBeneficial()) {
                        if (!this.plugin.redPlayers.contains(target.getName())) {
                            event.setCancelled(true);
                        }
                    } else if (!this.plugin.greenPlayers.contains(target.getName())) {
                        event.setCancelled(true);
                    } else if (!this.plugin.bluePlayers.contains(target.getName())) {
                        event.setCancelled(true);
                    }
                } else if (this.plugin.bluePlayers.contains(player.getName())) {
                    if (event.getSpell().isBeneficial()) {
                        if (!this.plugin.bluePlayers.contains(target.getName())) {
                            event.setCancelled(true);
                        }
                    } else if (!this.plugin.greenPlayers.contains(target.getName())) {
                        event.setCancelled(true);
                    } else if (!this.plugin.redPlayers.contains(target.getName())) {
                        event.setCancelled(true);
                    }
                } else if (this.plugin.greenPlayers.contains(player.getName())) {
                    if (event.getSpell().isBeneficial()) {
                        if (!this.plugin.greenPlayers.contains(target.getName())) {
                            event.setCancelled(true);
                        }
                    } else if (!this.plugin.redPlayers.contains(target.getName())) {
                        event.setCancelled(true);
                    } else if (!this.plugin.bluePlayers.contains(target.getName())) {
                        event.setCancelled(true);
                    }
                }

            }
        }
    }

    @EventHandler(
        priority = EventPriority.MONITOR
    )
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem() && event.getItem().getType() == Material.MONSTER_EGG) {
            event.setCancelled(true);
        } else if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.POTION && event.getItem().getDurability() == 8197 && !event.getPlayer().hasPermission("game.ignore")) {
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Material type = event.getClickedBlock().getType();
            if (type == Material.CHEST || type == Material.ENDER_CHEST || type == Material.TRAPPED_CHEST || type == Material.BEACON || type == Material.DISPENSER || type == Material.DROPPER || type == Material.FURNACE || type == Material.BREWING_STAND || type == Material.HOPPER) {
                event.setCancelled(true);
            }
        }

    }
    

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        EntityType type = event.getRightClicked().getType();
        if (type == EntityType.MINECART_CHEST || type == EntityType.MINECART_HOPPER || type == EntityType.MINECART_FURNACE) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE || event.getEntityType() == EntityType.SKELETON) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (!event.getPlayer().hasPermission("game.ignore")) {
            event.setCancelled(true);
        } 

    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getSlotType() == SlotType.CONTAINER) {
            this.plugin.timeAtSpawn.remove(event.getWhoClicked().getName());
        }

    }

    @EventHandler
    public void onCraftPrepare(PrepareItemCraftEvent event) {
        event.getInventory().setResult((ItemStack)null);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == SlotType.CRAFTING) {
            event.setCancelled(true);
        } else if (event.getSlotType() == SlotType.ARMOR) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setExpToDrop(0);
        if (!event.getPlayer().hasPermission("game.ignore")) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent event) {
        if (event.getBlock().getType() == Material.FIRE) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            event.getNewState().setType(Material.AIR);
        }

    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Material t = event.getEntity().getItemStack().getType();
        if (t == Material.GRAVEL || t == Material.SAND) {
            event.getEntity().remove();
        }

    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == SpawnReason.SPAWNER_EGG) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
    	if (event.getEntity() instanceof Player) {
    		Player p = (Player) event.getEntity();
    		event.setFoodLevel(19);
	        ((Player)event.getEntity()).setSaturation(20.0F);
    	} else {
    		event.setFoodLevel(20);
            ((Player)event.getEntity()).setSaturation(20.0F);
    	}
        
    }

    @EventHandler
    public void onHangingRemove(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player && !((Player)event.getRemover()).isOp()) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (!(event instanceof HangingBreakByEntityEvent)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onHangingRightClick(PlayerInteractEntityEvent event) {
        if (!event.getPlayer().isOp()) {
            if (event.getRightClicked() instanceof Hanging) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler(
        priority = EventPriority.LOWEST
    )
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().split(" ")[0].toLowerCase();
        if (cmd.equals("/list") || cmd.equals("/who")) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            TreeMap<String, String> r = new TreeMap();
            TreeMap<String, String> b = new TreeMap();
            TreeMap<String, String> g = new TreeMap();
            TreeMap<String, String> o = new TreeMap();
            Player[] arr$ = Bukkit.getOnlinePlayers().toArray(new Player[ Bukkit.getOnlinePlayers().size() ]);
            int c = arr$.length;

            for(int i$ = 0; i$ < c; ++i$) {
                Player p = arr$[i$];
                if (player.canSee(p)) {
                    if (this.plugin.bluePlayers.contains(p.getName())) {
                        if (!p.getDisplayName().contains(p.getName())) {
                            b.put(p.getName().toLowerCase(), p.getDisplayName() + "(" + p.getName() + ")");
                        } else {
                            b.put(p.getName().toLowerCase(), p.getDisplayName());
                        }
                    } else if (this.plugin.redPlayers.contains(p.getName())) {
                        if (!p.getDisplayName().contains(p.getName())) {
                            r.put(p.getName().toLowerCase(), p.getDisplayName() + "(" + p.getName() + ")");
                        } else {
                            r.put(p.getName().toLowerCase(), ChatColor.DARK_RED + p.getName());
                        }
                    } else if (this.plugin.greenPlayers.contains(p.getName())) {
                        if (!p.getDisplayName().contains(p.getName())) {
                            g.put(p.getName().toLowerCase(), p.getDisplayName() + "(" + p.getName() + ")");
                        } else {
                            g.put(p.getName().toLowerCase(), ChatColor.DARK_RED + p.getName());
                        }
                    } else {
                        o.put(p.getName().toLowerCase(), p.getDisplayName());
                    }
                }
            }

            int c1 = 0;
            int lineLength = 55;
            player.sendMessage(ChatColor.YELLOW + "PLAYERS ONLINE (" + (b.size() + r.size() + g.size() + o.size()) + "):");
            String name;
            String msg;
            Iterator i$;
            if (b.size() > 0) {
                player.sendMessage(ChatColor.BLUE + "  Blue Team (" + b.size() + "):");
                c1 = 0;
                msg = "    ";
                i$ = b.values().iterator();

                while(i$.hasNext()) {
                    name = (String)i$.next();
                    if (ChatColor.stripColor(msg).length() + ChatColor.stripColor(name).length() + 2 > lineLength) {
                        player.sendMessage(msg);
                        msg = "    ";
                    }

                    msg = msg + name;
                    ++c1;
                    if (c1 < b.size()) {
                        msg = msg + ChatColor.WHITE + ", ";
                    }
                }

                player.sendMessage(msg);
            }
            
            if (g.size() > 0) {
                player.sendMessage(ChatColor.GREEN + "  Green Team (" + g.size() + "):");
                c1 = 0;
                msg = "    ";
                i$ = g.values().iterator();

                while(i$.hasNext()) {
                    name = (String)i$.next();
                    if (ChatColor.stripColor(msg).length() + ChatColor.stripColor(name).length() + 2 > lineLength) {
                        player.sendMessage(msg);
                        msg = "    ";
                    }

                    msg = msg + name;
                    ++c1;
                    if (c1 < g.size()) {
                        msg = msg + ChatColor.WHITE + ", ";
                    }
                }

                player.sendMessage(msg);
            }

            if (r.size() > 0) {
                player.sendMessage(ChatColor.RED + "  Red Team (" + r.size() + "):");
                c1 = 0;
                msg = "    ";
                i$ = r.values().iterator();

                while(i$.hasNext()) {
                    name = (String)i$.next();
                    if (ChatColor.stripColor(msg).length() + ChatColor.stripColor(name).length() + 2 > lineLength) {
                        player.sendMessage(msg);
                        msg = "    ";
                    }

                    msg = msg + name;
                    ++c1;
                    if (c1 < r.size()) {
                        msg = msg + ChatColor.WHITE + ", ";
                    }
                }

                player.sendMessage(msg);
            }

            if (o.size() > 0) {
                player.sendMessage(ChatColor.GRAY + "  Others:");
                c1 = 0;
                msg = "    ";
                i$ = o.values().iterator();

                while(i$.hasNext()) {
                    name = (String)i$.next();
                    if (ChatColor.stripColor(msg).length() + ChatColor.stripColor(name).length() + 2 > lineLength) {
                        player.sendMessage(msg);
                        msg = "    ";
                    }

                    msg = msg + name;
                    ++c1;
                    if (c1 < o.size()) {
                        msg = msg + ChatColor.WHITE + ", ";
                    }
                }

                player.sendMessage(msg);
            }
        }

    }

    

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!event.getPlayer().hasPermission("game.ignore")) {
            event.setCancelled(true);
        }

    }
}
