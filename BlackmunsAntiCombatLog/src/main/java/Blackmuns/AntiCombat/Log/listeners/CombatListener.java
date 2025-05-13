package Blackmuns.AntiCombat.Log.listeners;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class CombatListener implements Listener {
    private final JavaPlugin plugin;
    private final HashMap<UUID, BukkitRunnable> timers = new HashMap<>();
    private final HashMap<UUID, BossBar> bossBars = new HashMap<>();
    private final int TIMER_DURATION;

    public CombatListener(JavaPlugin plugin, int timerDuration) {
        this.plugin = plugin;
        this.TIMER_DURATION = timerDuration;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker) {
            plugin.getLogger().info(victim.getName() + " was damaged by " + attacker.getName());
            startCombatTimer(victim);
            startCombatTimer(attacker);
        }
    }

    private void startCombatTimer(Player player) {
        UUID uuid = player.getUniqueId();

        // Cancel old timer if exists
        if (timers.containsKey(uuid)) {
            timers.get(uuid).cancel();
        }

        // Remove old boss bar if exists
        if (bossBars.containsKey(uuid)) {
            bossBars.get(uuid).removeAll();
        }

        // Create new boss bar
        BossBar bossBar = Bukkit.createBossBar("Combat Timer: " + TIMER_DURATION + "s", BarColor.RED, BarStyle.SOLID);
        bossBar.addPlayer(player);
        bossBar.setProgress(1.0);
        bossBars.put(uuid, bossBar);

        BukkitRunnable timer = new BukkitRunnable() {
            private int timeLeft = TIMER_DURATION;

            @Override
            public void run() {
                if (timeLeft > 0) {
                    bossBar.setTitle("Combat Timer: " + timeLeft + "s");
                    bossBar.setProgress((double) timeLeft / TIMER_DURATION);
                    timeLeft--;
                } else {
                    timers.remove(uuid);
                    bossBars.remove(uuid);
                    bossBar.removeAll();
                    cancel();
                }
            }
        };

        timer.runTaskTimer(plugin, 0, 20);
        timers.put(uuid, timer);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (timers.containsKey(uuid)) {
            timers.get(uuid).cancel();
            timers.remove(uuid);
        }

        if (bossBars.containsKey(uuid)) {
            bossBars.get(uuid).removeAll();
            bossBars.remove(uuid);
        }
    }
}
