//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.genn.camelot;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spell.SpellCastState;
import com.nisovin.magicspells.castmodifiers.Condition;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.spells.passive.PassiveTrigger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import me.genn.camelot.conditions.IsBlueCondition;
import me.genn.camelot.conditions.IsGreenCondition;
import me.genn.camelot.conditions.IsRedCondition;
import me.genn.gennsgym.GameMode;
import me.genn.gennsgym.GennsGym;
import me.genn.gennsgym.StatisticType;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Camelot extends JavaPlugin implements GameMode, Listener {
    public static Camelot plugin;
    public boolean gameRunning = false;
    private RoundTimer roundtimer = null;
    public boolean gameEnded = false;
    public boolean override = false;
    public long gameStartTime = 0L;
    public int gameStartAttempts = 0;
    public boolean timerRunning;
    public String mapName;
    public static int roundTime;
    public int autoStartTime;
    public int minPlayers;
    public int scoreInterval;
    public int startEndTimerAtPercent;
    public int endTimerDuration;
    public Spell becomeBlueSpell;
    public Spell becomeRedSpell;
    public Spell becomeGreenSpell;
    public List<String> startCommands;
    public Location mapSpawn;
    public ShrineManager shrineManager;
    public Set<String> bluePlayers;
    public Set<String> redPlayers;
    public Set<String> greenPlayers;
    public Set<String> extraPointsPlayers;
    Scoreboard scoreboard;
    Objective objective;
    public Score redCaptureScore;
    public Score blueCaptureScore;
    public Score greenCaptureScore;
    Map<String, Long> deathTimes;
    Map<String, List<String>> topPlayersByStat;
    IntMap<String> xp;
    IntMap<String> killsAsBlue;
    IntMap<String> killsAsRed;
    IntMap<String> killsAsGreen;
    IntMap<String> gameWinsAsGreen;
    IntMap<String> gameWinsAsBlue;
    IntMap<String> gameWinsAsRed;
    IntMap<String> gameWins;
    IntMap<String> swordKills;
    IntMap<String> bowKills;
    IntMap<String> environmentalKills;
    IntMap<String> globalStats;
    Map<String, Long> joinedTimes;
    IntMap<String> timePlayed;
    KillStreakTracker killStreakTracker;
    IntMap<String> timeAtSpawn;
    BukkitTask counterTask;
    BukkitTask endTask;
    BukkitTask experienceTask;
    Location redSpawn;
    Location blueSpawn;
    Location greenSpawn;
    int experienceCounter = 0;
    public GameEvent gameEvent;
    public VolatileCode volatileCode;
    Random random = new Random();
    public String nextmap;
    public String keepController;
    int redCapturePoints;
    int greenCapturePoints;
    int blueCapturePoints;

    public Camelot() {
    }

    public int getGameModeId() {
        return 3;
    }

    public String getGameModeCode() {
        return "Camelot";
    }

    public String getGameModeName() {
        return "Camelot";
    }

    public void onLoad() {
        plugin = this;
        this.loadMagicSpellsStuff();
    }

    public void loadMagicSpellsStuff() {
        Condition.addCondition("isblue", IsBlueCondition.class);
        Condition.addCondition("isred", IsRedCondition.class);
        Condition.addCondition("isgreen", IsGreenCondition.class);

    }

    public void onEnable() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.saveDefaultConfig();
        }

        YamlConfiguration config = new YamlConfiguration();
        List<String> list = new ArrayList();
        File mapFolder = new File("maps/");
        String[] contents = mapFolder.list();

        for(int i = 0; i < contents.length; ++i) {
            list.add(contents[i]);
        }

        System.out.println("Next map is:" + this.nextmap);

        try {
            config.load(configFile);
        } catch (Exception var8) {
            this.getLogger().severe("FAILED TO LOAD CONFIG FILE");
            var8.printStackTrace();
            this.setEnabled(false);
            return;
        }

        ((World)Bukkit.getWorlds().get(0)).setStorm(false);
        ((World)Bukkit.getWorlds().get(0)).setWeatherDuration(300000);
        ((World)Bukkit.getWorlds().get(0)).setGameRuleValue("doTileDrops", "false");
        ((World)Bukkit.getWorlds().get(0)).setGameRuleValue("keepInventory", "false");
        ((World)Bukkit.getWorlds().get(0)).setGameRuleValue("doDaylightCycle", "true");
        this.mapName = config.getString("map-name", "TTG");
        this.autoStartTime = config.getInt("auto-start-time", 0);
        this.minPlayers = config.getInt("min-players", 0);
        this.roundTime = config.getInt("rount-time", 600);
        this.scoreInterval = config.getInt("score-interval", 30);
        this.startEndTimerAtPercent = config.getInt("start-end-timer-at-percent", 20);
        this.endTimerDuration = config.getInt("end-timer-duration", 300);
        this.becomeBlueSpell = MagicSpells.getSpellByInternalName(config.getString("become-blue-spell", "become_blue"));
        this.becomeRedSpell = MagicSpells.getSpellByInternalName(config.getString("become-red-spell", "become_red"));
        this.becomeGreenSpell = MagicSpells.getSpellByInternalName(config.getString("become-green-spell", "become_green"));
        this.startCommands = config.getStringList("start-commands");
        String mapSpawnString = config.getString("map-spawn", "");
        if (mapSpawnString.isEmpty()) {
            this.mapSpawn = ((World)Bukkit.getWorlds().get(0)).getSpawnLocation();
        } else {
            String[] split = mapSpawnString.split(",");
            this.mapSpawn = new Location((World)Bukkit.getWorlds().get(0), (double)Integer.parseInt(split[0]), (double)Integer.parseInt(split[1]), (double)Integer.parseInt(split[2]));
        }

        this.shrineManager = new ShrineManager(this, config);
        this.bluePlayers = Collections.synchronizedSet(new HashSet());
        this.redPlayers = Collections.synchronizedSet(new HashSet());
        this.greenPlayers = Collections.synchronizedSet(new HashSet());
        this.xp = new IntMap();
        this.killsAsBlue = new IntMap();
        this.killsAsGreen = new IntMap();
        this.killsAsRed = new IntMap();
        this.gameWinsAsBlue = new IntMap();
        this.gameWinsAsGreen = new IntMap();
        this.gameWinsAsRed = new IntMap();
        this.swordKills = new IntMap();
        this.bowKills = new IntMap();
        this.environmentalKills = new IntMap();
        this.globalStats = new IntMap();
        this.joinedTimes = new HashMap();
        this.timePlayed = new IntMap();
        this.deathTimes = new HashMap();
        this.killStreakTracker = new KillStreakTracker();
        this.timeAtSpawn = new IntMap();
        this.redSpawn = shrineManager.redSpawn;
        this.blueSpawn = shrineManager.blueSpawn;
        this.greenSpawn = shrineManager.greenSpawn;
        if (this.becomeBlueSpell == null) {
            this.getLogger().severe("BECOME-BLUE-SPELL IS INVALID!");
            this.setEnabled(false);
        } else if (this.becomeRedSpell == null) {
            this.getLogger().severe("BECOME-RED-SPELL IS INVALID!");
            this.setEnabled(false);
        } else if (this.becomeGreenSpell == null) {
            this.getLogger().severe("BECOME-GREEN-SPELL IS INVALID!");
            this.setEnabled(false);
        }

            Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
            GennsGym.initializeGameMode(this);
            GennsGym.registerStatistic("global_xp", StatisticType.XP, GennsGym.getGlobalGameMode());
            GennsGym.registerStatistic("camelot_xp", StatisticType.XP);
            GennsGym.registerStatistic("global_games_played", StatisticType.TOTAL, GennsGym.getGlobalGameMode());
            GennsGym.registerStatistic("camelot_games_played", StatisticType.TOTAL);
            GennsGym.registerStatistic("global_time_played", StatisticType.TOTAL, GennsGym.getGlobalGameMode());
            GennsGym.registerStatistic("camelot_time_played", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_kills_as_blue", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_kills_as_red", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_kills_as_green", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_wins_as_blue", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_wins_as_red", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_wins_as_green", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_wins_total", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_kills_as_offensive", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_kills_as_defensive", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_kills_as_support", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_sword_kills", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_bow_kills", StatisticType.TOTAL);
            GennsGym.registerStatistic("camelot_environmental_kills", StatisticType.TOTAL);
            this.volatileCode = new VolatileCode();
            GennsGym.setServerStatus(this.mapName + ", Starting Soon");
            if (this.autoStartTime > 0) {
                GennsGym.startCountdown(this.autoStartTime, this.minPlayers);
            }
        

    }

    public void onDisable() {
        this.endGame();
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("startgame")) {
            if (!this.gameRunning && !this.gameEnded) {
                GennsGym.stopCountdown();
                this.startGame();
                sender.sendMessage("Game started.");
            } else {
                sender.sendMessage("Game already started.");
            }
        } else if (!command.getName().equalsIgnoreCase("setshrine") || !(sender instanceof Player)) {
            Player player;
            if (command.getName().equalsIgnoreCase("warpgreen")) {
                if (args.length == 1) {
                    player = Bukkit.getPlayerExact(args[0]);
                    if (player != null) {
                        this.shrineManager.respawnGreen(player);
                    }
                } else if (sender instanceof Player) {
                    this.shrineManager.respawnGreen((Player)sender);
                }
            } else if (command.getName().equalsIgnoreCase("warpred")) {
                if (args.length == 1) {
                    player = Bukkit.getPlayerExact(args[0]);
                    if (player != null) {
                        this.shrineManager.respawnRed(player);
                    }
                } else if (sender instanceof Player) {
                    this.shrineManager.respawnRed((Player)sender);
                }
            } else if (command.getName().equalsIgnoreCase("warpshrine")) {
                if (args.length == 1) {
                    player = Bukkit.getPlayerExact(args[0]);
                    if (player != null) {
                        player.teleport(this.shrineManager.getCurrentShrineForTeleport());
                    }
                } else if (sender instanceof Player) {
                    ((Player)sender).teleport(this.shrineManager.getCurrentShrineForTeleport());
                }
            } else if (command.getName().equalsIgnoreCase("nextmap")) {
                if (args.length == 0) {
                    sender.sendMessage("The next map is: " + this.nextmap);
                } else if (args.length == 1) {
                    List<String> list = new ArrayList();
                    File mapFolder = new File("maps/");
                    String[] contents = mapFolder.list();

                    int i;
                    for(i = 0; i < contents.length; ++i) {
                        list.add(contents[i]);
                    }

                    if (list.contains(args[0])) {
                        this.nextmap = args[0];
                        sender.sendMessage("Next map set to: " + this.nextmap);
                    } else {
                        sender.sendMessage("Inavlid map name! Valid map names are:");

                        for(i = 0; i < list.size(); ++i) {
                            sender.sendMessage((String)list.get(i));
                        }
                    }
                } else {
                    sender.sendMessage("Usage: /nextmap [map name]");
                }
            } else if (command.getName().equalsIgnoreCase("setblue")) {
                if (args.length != 1) {
                    sender.sendMessage("Usage: /setblue playername");
                } else {
                    player = Bukkit.getPlayer(args[0]);
                    if (player == null) {
                        sender.sendMessage("No player found");
                    } else {
                        this.gameEvent.setBlue(player);
                        this.redPlayers.remove(player.getName());
                        this.greenPlayers.remove(player.getName());
                        this.bluePlayers.add(player.getName());
                        this.becomeBlueSpell.castSpell(player, SpellCastState.NORMAL, 1.0F, (String[])null);
                        sender.sendMessage("Player " + player.getName() + " is now a blue");
                    }
                }
            } else if (command.getName().equalsIgnoreCase("setred")) {
                if (args.length != 1) {
                    sender.sendMessage("Usage: /setred playername");
                } else {
                    player = Bukkit.getPlayer(args[0]);
                    if (player == null) {
                        sender.sendMessage("No player found");
                    } else {
                        this.gameEvent.setRed(player);
                        this.bluePlayers.remove(player.getName());
                        this.greenPlayers.remove(player.getName());
                        this.redPlayers.add(player.getName());
                        this.becomeRedSpell.castSpell(player, SpellCastState.NORMAL, 1.0F, (String[])null);
                        sender.sendMessage("Player " + player.getName() + " is now a red");
                    }
                }
            } else if (command.getName().equalsIgnoreCase("setgreen")) {
                if (args.length != 1) {
                    sender.sendMessage("Usage: /setgreen playername");
                } else {
                    player = Bukkit.getPlayer(args[0]);
                    if (player == null) {
                        sender.sendMessage("No player found");
                    } else {
                        this.gameEvent.setGreen(player);
                        this.redPlayers.remove(player.getName());
                        this.bluePlayers.remove(player.getName());
                        this.greenPlayers.add(player.getName());
                        this.becomeGreenSpell.castSpell(player, SpellCastState.NORMAL, 1.0F, (String[])null);
                        sender.sendMessage("Player " + player.getName() + " is now a green");
                    }
                }
            } else if (command.getName().equalsIgnoreCase("shrineimmune")) {
                if (args.length != 1 && args.length != 2) {
                    sender.sendMessage("Usage: /shrineimmune playername [on|off]");
                } else {
                    player = Bukkit.getPlayer(args[0]);
                    if (player == null) {
                        sender.sendMessage("No player found");
                    } else if (args.length == 2 && args[1].equalsIgnoreCase("on")) {
                        this.shrineManager.setShrineImmunity(player, true);
                        sender.sendMessage("Player " + player.getName() + " is shrine immune");
                    } else {
                        this.shrineManager.setShrineImmunity(player, false);
                        sender.sendMessage("Player " + player.getName() + " is NOT shrine immune");
                    }
                }
            }
        }

        return true;
    }

    public void startGame() {
        this.gameRunning = true;
        this.gameStartTime = System.currentTimeMillis();
        GennsGym.setServerStatus(this.mapName + ", Keep Controller: NONE");
        this.keepController = "none";
        if (this.startCommands != null) {
            Iterator i$ = this.startCommands.iterator();

            while(i$.hasNext()) {
                String comm = (String)i$.next();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comm);
            }
        }

        this.initializeScoreboard();
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            public void run() {
                Camelot.this.runStatUpdates();
            }
        }, 1800L, 1800L);
        
        this.assignPlayers();
        startRoundCountDown(this.roundTime);
        this.experienceTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            public void run() {
                if (Camelot.this.gameRunning) {
                    Player[] arr$ = (Player[])Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
                    int len$ = arr$.length;

                    for(int i$ = 0; i$ < len$; ++i$) {
                        Player p = arr$[i$];
                        if (Camelot.this.bluePlayers.contains(p.getName()) || Camelot.this.redPlayers.contains(p.getName()) || Camelot.this.greenPlayers.contains(p.getName())) {
                            Camelot.this.xp.increment(p.getName(), 10);
                        }
                    }

                    if (++Camelot.this.experienceCounter >= 120) {
                        Camelot.this.experienceTask.cancel();
                    }
                } else {
                    Camelot.this.experienceTask.cancel();
                }

            }
        }, 600L, 600L);
        if (this.volatileCode != null) {
            this.volatileCode.sendEnderDragonToAllPlayers();
        }
        this.counterTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            public void run() {
                try {
                    Camelot.this.recount();
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }

            }
        }, (long)(this.scoreInterval * 20), (long)(this.scoreInterval * 20));
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                Player[] arr$ = (Player[])Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
                int len$ = arr$.length;

                Player p;
                for(int i$x = 0; i$x < arr$.length; ++i$x) {
                    p = arr$[i$x];
                    if (Camelot.this.redPlayers.contains(p.getName()) && p.getLocation().distanceSquared(Camelot.this.redSpawn) < 30.0D) {
                        Camelot.this.timeAtSpawn.increment(p.getName(), 5);
                    } else {
                        Camelot.this.timeAtSpawn.remove(p.getName());
                    }
                }
                for(int i$x = 0; i$x < arr$.length; ++i$x) {
                    p = arr$[i$x];
                    if (Camelot.this.greenPlayers.contains(p.getName()) && p.getLocation().distanceSquared(Camelot.this.greenSpawn) < 30.0D) {
                        Camelot.this.timeAtSpawn.increment(p.getName(), 5);
                    } else {
                        Camelot.this.timeAtSpawn.remove(p.getName());
                    }
                }
                for(int i$x = 0; i$x < arr$.length; ++i$x) {
                    p = arr$[i$x];
                    if (Camelot.this.bluePlayers.contains(p.getName()) && p.getLocation().distanceSquared(Camelot.this.blueSpawn) < 30.0D) {
                        Camelot.this.timeAtSpawn.increment(p.getName(), 5);
                    } else {
                        Camelot.this.timeAtSpawn.remove(p.getName());
                    }
                }

                List<String> toRemove = new ArrayList();
                Iterator i$ = Camelot.this.timeAtSpawn.keySet().iterator();

                String name;
                while(i$.hasNext()) {
                    name = (String)i$.next();
                    if (Camelot.this.timeAtSpawn.get(name) >= 150) {
                        p = Bukkit.getPlayerExact(name);
                        if (p != null) {
                            GennsGym.sendPlayerToLobby(p);
                        }

                        toRemove.add(name);
                    }
                }

                i$ = toRemove.iterator();

                while(i$.hasNext()) {
                    name = (String)i$.next();
                    Camelot.this.timeAtSpawn.remove(name);
                }

            }
        }, 100L, 100L);

    }

    public void setAsBlue(Player player) {
        this.becomeBlueSpell.castSpell(player, SpellCastState.NORMAL, 1.0F, (String[])null);
        this.bluePlayers.add(player.getName());
    }

    public void setAsRed(Player player) {
        this.becomeRedSpell.castSpell(player, SpellCastState.NORMAL, 1.0F, (String[])null);
        this.redPlayers.add(player.getName());
    }

    public void setAsGreen(Player player) {
        this.becomeGreenSpell.castSpell(player, SpellCastState.NORMAL, 1.0F, (String[])null);
        this.greenPlayers.add(player.getName());
    }

    

    private boolean isTitled(String playerName) {
        Iterator i$ = this.topPlayersByStat.keySet().iterator();

        while(i$.hasNext()) {
            String stat = (String)i$.next();
            if (((List)this.topPlayersByStat.get(stat)).contains(playerName)) {
                return true;
            }
        }

        return false;
    }


   

    private void initializeScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        this.objective = this.scoreboard.getObjective("Keep Control");
        if (this.objective == null) {
            this.objective = this.scoreboard.registerNewObjective("Keep Control", "KeepControl");
            this.objective.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "Keep Control");
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        this.redCaptureScore = this.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "Red"));
        this.greenCaptureScore = this.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GREEN + "Green"));
        this.blueCaptureScore = this.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Blue"));

    }

    

    public void assignPlayers() {
        ArrayList<Player> arr$ = new ArrayList(Bukkit.getOnlinePlayers());
        int len$ = arr$.size();

        
        Collections.shuffle(arr$);
        Random random = new Random();
        int justify = random.nextInt(3);
        justify = justify + 3;
        for(int i = 0; i < arr$.size(); ++i) {
            if (justify % 3 == 1) {
                this.setAsRed((Player)arr$.get(i));
            } else if (justify % 2 == 0) {
                this.setAsGreen((Player)arr$.get(i));
            } else if (justify % 3 == 2) {
            	this.setAsBlue((Player)arr$.get(i));
            }

            ++justify;
        }

    }

    void recount() throws InterruptedException {
        double shrinePowerMod = 0.0D;
        int bluePlayers = 0;
        int redPlayers = 0;
        int greenPlayers = 0;
        double blueValue = this.shrineManager.getBlueValue();
        double redValue = this.shrineManager.getRedValue();
        double greenValue = this.shrineManager.getGreenValue();
        Player[] arr$ = (Player[])Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
        int len$ = arr$.length;
        Iterator iter = Bukkit.getOnlinePlayers().iterator();
        String keepController = shrineManager.keepController;
        int pointsForBlue = 0;
        int pointsForRed = 0;
        int pointsForGreen = 0;
        while (iter.hasNext()) {
        	Player p = (Player) iter.next();
        	if (!p.isDead()) {
        		int pCapPoints;
        		if (this.extraPointsPlayers.contains(p.getName())) {
        			pCapPoints = 2;
        		} else {
        			pCapPoints = 1;
        		}
        		if (this.bluePlayers.contains(p.getName())) {
        			if (this.shrineManager.playerNearShrineForCapture(p)) {
                        	pointsForBlue += pCapPoints;
                        	pointsForRed -= pCapPoints;
                        	pointsForGreen -= pCapPoints;
        			}
        		} else if (this.redPlayers.contains(p.getName())) {
        			if (this.shrineManager.playerNearShrineForCapture(p)) {
                    	pointsForRed += pCapPoints;
                    	pointsForBlue -= pCapPoints;
                    	pointsForGreen -= pCapPoints;
        			}
    			} else if (this.greenPlayers.contains(p.getName())) {
    				if (this.shrineManager.playerNearShrineForCapture(p)) {
                    	pointsForGreen += pCapPoints;
                    	pointsForRed -= pCapPoints;
                    	pointsForBlue -= pCapPoints;
    				}	
        		
    			}
        }
        }
        
        if (this.blueCapturePoints - pointsForBlue <= 0) {
            this.blueCapturePoints = 0;
            
        } else if (this.blueCapturePoints + pointsForBlue >= 100) {
        	this.blueCapturePoints = 100;
        } else {
        	this.blueCapturePoints += pointsForBlue;
        }
        this.blueCaptureScore.setScore(this.blueCapturePoints);
        if (this.redCapturePoints - pointsForRed <= 0) {
            this.redCapturePoints = 0;
            
        } else if (this.redCapturePoints + pointsForRed >= 100) {
        	this.redCapturePoints = 100;
        } else {
        	this.redCapturePoints += pointsForRed;
        }
        this.redCaptureScore.setScore(this.redCapturePoints);
        if (this.greenCapturePoints - pointsForGreen <= 0) {
            this.greenCapturePoints = 0;
            
        } else if (this.greenCapturePoints + pointsForGreen >= 100) {
        	this.greenCapturePoints = 100;
        } else {
        	this.greenCapturePoints += pointsForGreen;
        }
        this.greenCaptureScore.setScore(this.greenCapturePoints);
        if (!keepController.equalsIgnoreCase("green") && !keepController.equalsIgnoreCase("red") && !keepController.equalsIgnoreCase("blue")) {
        	if (this.blueCapturePoints > this.redCapturePoints && this.blueCapturePoints > this.greenCapturePoints && this.blueCapturePoints >= 25) {
        		this.keepController = "blue";
        		this.keepCapture("blue");
        	} else if (this.redCapturePoints > this.blueCapturePoints && this.redCapturePoints > this.greenCapturePoints && this.redCapturePoints >= 25) {
        		this.keepController = "red";
        		this.keepCapture("red");
        	} else if (this.greenCapturePoints > this.blueCapturePoints && this.greenCapturePoints > this.redCapturePoints && this.greenCapturePoints >= 25) {
        		this.keepController = "green";
        		this.keepCapture("green");
        	}
        }
        GennsGym.setServerStatus(this.mapName + "Keep Controller: " + this.keepController.toUpperCase());

        

        


    }

    public void keepCapture(String team) throws InterruptedException {
    	this.keepController = team;
    	if (team.equalsIgnoreCase("blue")) { 
            Bukkit.broadcastMessage(ChatColor.BLUE + "Team BLUE has taken the castle!");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "blue_red_gate_outer_paste");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "green_blue_gate_outer_paste");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "blue_red_gate_inner_paste");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "green_blue_gate_inner_paste");
    	} else if (team.equalsIgnoreCase("red")) {
            Bukkit.broadcastMessage(ChatColor.RED + "Team RED has taken the castle!");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "blue_red_gate_outer_paste");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "green_blue_gate_outer_paste");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "blue_red_gate_inner_paste");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "green_blue_gate_inner_paste");
    	} else if (team.equalsIgnoreCase("green")) {
            Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Team GREEN has taken the castle!");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "blue_red_gate_outer_paste");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "green_blue_gate_outer_paste");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "blue_red_gate_inner_paste");
            this.castSpellAtLocation(this.shrineManager.getCurrentShrine(), "green_blue_gate_inner_paste");
    	}
        

    }
    
    public void castSpellAtLocation(Location targetLocation, String spellName) {
        Spell spell = MagicSpells.getSpellByInternalName(spellName);
        if (spell == null) {
            this.getLogger().warning("Spell not found: " + spellName);
        } else if (!(spell instanceof TargetedLocationSpell)) {
            this.getLogger().warning("Spell is not a targeted location spell: " + spellName);
        } else {
            TargetedLocationSpell spell1 = (TargetedLocationSpell)spell;
            spell1.castAtLocation(targetLocation, 1.0F);
        }
    }

    public void greenGameWin() throws InterruptedException {
        this.gameRunning = false;
        Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "=================================================");
        Bukkit.broadcastMessage(ChatColor.GREEN + "THE GREEN TEAM HAS CAPTURED CAMELOT!");
        Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "=================================================");
        Iterator iter = Bukkit.getOnlinePlayers().iterator();
        while (iter.hasNext()) {
        	Player p = (Player) iter.next();
        	if (this.greenPlayers.contains(p.getName())) {
        		this.gameWinsAsGreen.increment(p.getName());
        		this.gameWins.increment(p.getName());
        	}
        }
        this.endGame();
    }
    public void blueGameWin() throws InterruptedException {
        this.gameRunning = false;
        Bukkit.broadcastMessage(ChatColor.BLUE + "=================================================");
        Bukkit.broadcastMessage(ChatColor.AQUA + "THE BLUE TEAM HAS CAPTURED CAMELOT!");
        Bukkit.broadcastMessage(ChatColor.BLUE + "=================================================");
        Iterator iter = Bukkit.getOnlinePlayers().iterator();
        while (iter.hasNext()) {
        	Player p = (Player) iter.next();
        	if (this.bluePlayers.contains(p.getName())) {
        		this.gameWinsAsBlue.increment(p.getName());
        		this.gameWins.increment(p.getName());
        	}
        }
        this.endGame();
    }
    public void redGameWin() throws InterruptedException {
        this.gameRunning = false;
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "=================================================");
        Bukkit.broadcastMessage(ChatColor.RED + "THE RED TEAM HAS CAPTURED CAMELOT!");
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "=================================================");
        Iterator iter = Bukkit.getOnlinePlayers().iterator();
        while (iter.hasNext()) {
        	Player p = (Player) iter.next();
        	if (this.redPlayers.contains(p.getName())) {
        		this.gameWinsAsRed.increment(p.getName());
        		this.gameWins.increment(p.getName());
        	}
        }
        this.endGame();
    }

    

    public void endGame() {
        if (this.gameRunning) {
            this.gameRunning = false;
            this.gameEnded = true;
            GennsGym.setServerStatus("Game Ended");
            if (this.counterTask != null) {
                this.counterTask.cancel();
                this.counterTask = null;
            }

            if (this.endTask != null) {
                this.endTask.cancel();
                this.endTask = null;
            }

            if (this.volatileCode != null) {
                this.volatileCode.removeEnderDragonForAllPlayers();
            }

            GennsGym.updateRushMode();
            Player[] arr$ = (Player[])Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
            int len$ = arr$.length;

            for(int iS$1 = 0; iS$1 < len$; ++iS$1) {
                Player player = arr$[iS$1];
                if (this.gameEvent.doesPlayerGetExtraXp(player)) {
                    this.xp.increment(player.getName(), 500);
                }

                int amt = this.xp.get(player.getName());
                if (amt > 0) {
                    GennsGym.updateStatistic(player.getName(), "global_xp", amt);
                }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                public void run() {
                    GennsGym.forceRunUpdates();
                    if (System.currentTimeMillis() > Camelot.this.gameStartTime + 600000L) {
                        Player[] arr$ = (Player[])Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
                        int len$ = arr$.length;

                        for(int i$x = 0; i$x < len$; ++i$x) {
                            Player player = arr$[i$x];
                            int amt = Camelot.this.xp.get(player.getName());
                            if (amt > 0) {
                                GennsGym.updateStatistic(player.getName(), "camelot_xp", amt);
                            }

                            GennsGym.updateStatistic(player.getName(), "global_games_played", 1);
                            GennsGym.updateStatistic(player.getName(), "camelot_games_played", 1);
                            Camelot.this.addTimePlayed(player);
                        }

                        GennsGym.updateStatistic(":GLOBAL:", "global_games_played", 1);
                        GennsGym.updateStatistic(":GLOBAL:", "camelot_games_played", 1);
                        Iterator i$ = Camelot.this.globalStats.keySet().iterator();

                        while(i$.hasNext()) {
                            String stat = (String)i$.next();
                            GennsGym.updateStatistic(":GLOBAL:", stat, Camelot.this.globalStats.get(stat));
                        }

                        Camelot.this.globalStats.clear();
                        Camelot.this.runStatUpdates();
                        Camelot.this.runStatUpdatesFinal();
                        Camelot.this.killStreakTracker.processAllKillStreaks();
                    }

                    GennsGym.endGame();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Camelot.plugin, new Runnable() {
                        public void run() {
                            try {
                                Camelot.this.reloadMap();
                            } catch (IOException var2) {
                                var2.printStackTrace();
                            }

                        }
                    }, 100L);
                }
            }, 80L);
        }

    }

    void addTimePlayed(Player player) {
        String playerName = player.getName();
        Long joinedTime = (Long)this.joinedTimes.remove(playerName);
        if (joinedTime != null) {
            int timePlayed = (int)((System.currentTimeMillis() - joinedTime) / 1000L);
            GennsGym.updateStatistic(playerName, "global_time_played", timePlayed);
            GennsGym.updateStatistic(playerName, "camelot_time_played", timePlayed);
            this.timePlayed.increment(playerName, timePlayed);
        }

    }

    private void runStatUpdates() {
        Iterator i$ = this.killsAsBlue.keySet().iterator();

        String name;
        while(i$.hasNext()) {
            name = (String)i$.next();
            GennsGym.updateStatistic(name, "camelot_kills_as_blue", this.killsAsBlue.get(name));
        }

        this.killsAsBlue.clear();
        i$ = this.killsAsRed.keySet().iterator();

        while(i$.hasNext()) {
            name = (String)i$.next();
            GennsGym.updateStatistic(name, "camelot_kills_as_red", this.killsAsRed.get(name));
        }

        this.killsAsRed.clear();
        i$ = this.killsAsGreen.keySet().iterator();

        while(i$.hasNext()) {
            name = (String)i$.next();
            GennsGym.updateStatistic(name, "camelot_kills_as_green", this.killsAsGreen.get(name));
        }

        this.killsAsGreen.clear();
       
    }

    private void runStatUpdatesFinal() {
    	 Iterator i$ = this.gameWinsAsBlue.keySet().iterator();
    	 String name;
         while(i$.hasNext()) {
             name = (String)i$.next();
             GennsGym.updateStatistic(name, "camelot_wins_as_blue", this.gameWinsAsBlue.get(name));
         }

         this.gameWinsAsBlue.clear();
         i$ = this.gameWinsAsRed.keySet().iterator();

         while(i$.hasNext()) {
             name = (String)i$.next();
             GennsGym.updateStatistic(name, "camelot_wins_as_red", this.gameWinsAsRed.get(name));
         }

         this.gameWinsAsRed.clear();
         i$ = this.gameWinsAsGreen.keySet().iterator();

         while(i$.hasNext()) {
             name = (String)i$.next();
             GennsGym.updateStatistic(name, "camelot_wins_as_green", this.gameWinsAsGreen.get(name));
         }

         this.gameWinsAsGreen.clear();
         
         i$ = this.gameWins.keySet().iterator();

         while(i$.hasNext()) {
             name = (String)i$.next();
             GennsGym.updateStatistic(name, "camelot_wins_total", this.gameWins.get(name));
         }

         this.gameWins.clear();

    }

    public static void startRoundCountDown(int seconds) {
        plugin.roundtimer = new RoundTimer(plugin, seconds);
    }

    public void reloadMap() throws IOException {
        FileUtils.deleteDirectory(new File("world/"));
        FileUtils.deleteDirectory(new File("plugins/Essentials/userdata/"));
        FileUtils.deleteDirectory(new File("plugins/Essentials/warps/"));
        FileUtils.deleteDirectory(new File("plugins/WorldGuard/worlds/world/"));
        FileUtils.deleteDirectory(new File("plugins/YAPP/players/"));
        FileUtils.deleteDirectory(new File("plugins/Camelot/"));
        FileUtils.copyDirectory(new File("maps/" + this.nextmap + "/world/"), new File("world/"));
        FileUtils.copyDirectory(new File("maps/" + this.nextmap + "/Essentials/warps/"), new File("plugins/Essentials/warps/"));
        FileUtils.copyDirectory(new File("maps/" + this.nextmap + "/WorldGuard/worlds/"), new File("plugins/WorldGuard/worlds/"));
        FileUtils.copyDirectory(new File("maps/" + this.nextmap + "/Camelot/"), new File("plugins/Camelot/"));
        FileUtils.copyDirectory(new File("baseyapp/YAPP/"), new File("plugins/YAPP/"));
    }
}
