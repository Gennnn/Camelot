package me.genn.camelot;

import me.genn.gennsgym.GennsGym;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KillStreakTracker {
    final int killStreakDelay = 3000;
    Map<String, Long> lastKillTimes = new HashMap();
    IntMap<String> killCounts = new IntMap();

    public KillStreakTracker() {
    }

    public void addKill(String killer) {
        Long lastKill = (Long)this.lastKillTimes.get(killer);
        if (lastKill != null) {
            if (lastKill + 10000L > System.currentTimeMillis()) {
                this.killCounts.increment(killer);
            } else {
                int count = this.killCounts.get(killer);
                if (count > 2) {
                    GennsGym.updateStatistic(killer, "camelot_longest_kill_streak", count);
                }

                this.killCounts.set(killer, 1);
            }
        } else {
            this.killCounts.set(killer, 1);
        }

        this.lastKillTimes.put(killer, System.currentTimeMillis());
    }

    public void processAllKillStreaks() {
        Iterator i$ = this.killCounts.keySet().iterator();

        while(i$.hasNext()) {
            String killer = (String)i$.next();
            int count = this.killCounts.get(killer);
            if (count > 2) {
                GennsGym.updateStatistic(killer, "camelot_longest_kill_streak", count);
            }
        }

        this.lastKillTimes.clear();
        this.killCounts.clear();
    }
}
