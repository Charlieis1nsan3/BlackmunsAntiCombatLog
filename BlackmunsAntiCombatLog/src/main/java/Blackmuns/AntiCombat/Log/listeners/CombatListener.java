package Blackmuns.AntiCombat.Log.listeners;

import org.bukkit.Material;
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
        if (timers.containsKey(player.getUniqueId())) {
            timers.get(player.getUniqueId()).cancel();
        }

        BukkitRunnable timer = new BukkitRunnable() {
            private int timeLeft = TIMER_DURATION;

            @Override
            public void run() {
                if (timeLeft > 0) {
                    player.sendMessage("&c&lYou are in combat! Time left: " + timeLeft + " seconds.");
                    timeLeft--;
                } else {
                    timers.remove(player.getUniqueId());
                    player.sendMessage("&l&fYou are no longer in combat.");
                    cancel();
                }
            }
        };

        timer.runTaskTimer(plugin, 0, 20);
        timers.put(player.getUniqueId(), timer);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (timers.containsKey(player.getUniqueId())) {
            BukkitRunnable timer = timers.get(player.getUniqueId());
            timer.cancel();

            timers.remove(player.getUniqueId());
            player.sendMessage("You have left the server, combat timer canceled.");
        }
    }
}