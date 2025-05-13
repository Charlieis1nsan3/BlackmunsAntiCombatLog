package Blackmuns.AntiCombat.Log;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import Blackmuns.AntiCombat.Log.listeners.CombatListener;

public class BlackmunsAntiCombatLog extends JavaPlugin implements CommandExecutor {
    private static int TIMER_DURATION = 15; 

    @Override
    public void onEnable() {
        getLogger().info("BlackmunsAntiCombatLog is enabling...");
        getServer().getPluginManager().registerEvents(new CombatListener(this, TIMER_DURATION), this);
        this.getCommand("clog").setExecutor(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("BlackmunsAntiCombatLog is disabling...");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("clog")) {
            if (args.length == 2 && args[0].equalsIgnoreCase("time")) {
                try {
                    int newTime = Integer.parseInt(args[1]);
                    if (newTime > 0) {
                        TIMER_DURATION = newTime;
                        sender.sendMessage("Combat log time set to " + TIMER_DURATION + " seconds.");
                    } else {
                        sender.sendMessage("Time must be positive.");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("Please enter a valid number.");
                }
                return true;
            } else {
                sender.sendMessage("Usage: /clog time <duration>");
                return true;
            }
        }
        return false;
    }

    public static int getTimerDuration() {
        return TIMER_DURATION;
    }
}