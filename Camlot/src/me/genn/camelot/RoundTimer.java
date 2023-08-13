//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.genn.camelot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class RoundTimer {
    Camelot plugin;
    int seconds;
    int minPlayers;
    RoundTimer.Countdown countdown;

    public RoundTimer(Camelot plugin, int seconds) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.startTimer();
    }

    void startTimer() {
        this.countdown = new RoundTimer.Countdown(this.plugin);
        Thread thread = new Thread(this.countdown);
        thread.start();
    }

    public void stopTimer() {
        this.countdown.stop();
    }

    void broadcastTimeRemaining(final int s) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
                Bukkit.broadcastMessage(ChatColor.GOLD + "GAME STARTS IN " + s + " SECONDS!");
            }
        });
    }

    void countdownDone() throws InterruptedException {
        if (this.plugin.keepController.equalsIgnoreCase("red")) {
            this.plugin.redGameWin();
            this.stopTimer();
        } else if (this.plugin.keepController.equalsIgnoreCase("blue")) {
            this.plugin.blueGameWin();
            this.stopTimer();
        } else if (this.plugin.keepController.equalsIgnoreCase("green")) {
            this.plugin.greenGameWin();
            this.stopTimer();
        } else {
            Camelot.plugin.endGame();
        }

    }

    class Countdown implements Runnable {
        boolean running = true;

        Countdown(Camelot plugin) {
        }

        public void run() {
            int remaining = RoundTimer.this.seconds;

            try {
                while(remaining > 0) {
                    if (!this.running) {
                        return;
                    }

                    plugin.volatileCode.setDragonHealth((int)Math.floor((remaining / RoundTimer.this.seconds) / 200.0D));
                    Thread.sleep(1000L);
                    --remaining;
                }
            } catch (Exception var3) {
            }

            if (this.running) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(RoundTimer.this.plugin, new Runnable() {
                    public void run() {
                        try {
                            RoundTimer.this.countdownDone();
                        } catch (InterruptedException var2) {
                            var2.printStackTrace();
                        }

                    }
                });
            }

        }

        public void stop() {
            this.running = false;
        }
    }
}
